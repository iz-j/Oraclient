package iz.oraclient.web.spring.jdbc;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.tomcat.jdbc.pool.interceptor.AbstractCreateStatementInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author izumi_j
 *
 */
public final class StatementManager extends AbstractCreateStatementInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(StatementManager.class);

	private static final class StatementRef {
		private WeakReference<Statement> statement;

		private StatementRef(Statement statement) {
			this.statement = new WeakReference<>(statement);
		}

		private Statement get() {
			return statement.get();
		}
	}

	private static final Map<Long, Set<StatementRef>> statementsMap = new ConcurrentHashMap<>();

	synchronized public static void abortAllExecutions() {
		statementsMap.forEach((k, v) -> {
			logger.trace("Abort for thread of {}.", k);
			v.forEach(s -> {
				final Statement stmt = s.get();
				if (stmt != null) {
					try {
						stmt.cancel();
					} catch (Exception e) {
						logger.warn("Ignore this exception.", e);
					}
				}
			});
		});
	}

	@Override
	public Object createStatement(Object proxy, Method method, Object[] args, Object statement, long time) {
		if (statement instanceof Statement) {
			final long threadId = Thread.currentThread().getId();
			Set<StatementRef> statements = statementsMap.get(threadId);
			if (statements == null) {
				statements = Collections.newSetFromMap(new ConcurrentHashMap<StatementRef, Boolean>());
				statementsMap.put(threadId, statements);
			}
			statements.add(new StatementRef((Statement)statement));
			logger.trace("{} statements is alive. threadId = {}", statements.size(), threadId);
		}
		return statement;
	}

	@Override
	public void closeInvoked() {
		final long threadId = Thread.currentThread().getId();
		final Set<StatementRef> statements = statementsMap.get(threadId);

		if (CollectionUtils.isEmpty(statements)) {
			logger.trace("No statements in this process.");
			return;
		}

		logger.trace("{} statements were used. threadId = {}", statements.size(), threadId);
		statementsMap.remove(threadId);
	}

}
