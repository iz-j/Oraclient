package iz.dbui.web.spring.jdbc;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * Just wrapper of {@link DataSourceTransactionManager}.
 *
 * @author izumi_j
 *
 */
@SuppressWarnings("serial")
public final class TxManagerWrapper extends DataSourceTransactionManager {
	private static final Logger logger = LoggerFactory.getLogger(TxManagerWrapper.class);

	@PostConstruct
	public void disableSpringLogger() {
		// XXX Maybe, this is very hacky!
		super.logger = new org.apache.commons.logging.Log() {

			@Override
			public boolean isTraceEnabled() {
				return false;
			}

			@Override
			public boolean isDebugEnabled() {
				return false;
			}

			@Override
			public boolean isInfoEnabled() {
				return false;
			}

			@Override
			public boolean isWarnEnabled() {
				return false;
			}

			@Override
			public boolean isErrorEnabled() {
				return true;
			}

			@Override
			public boolean isFatalEnabled() {
				return true;
			}

			@Override
			public void trace(Object message) {
			}

			@Override
			public void trace(Object message, Throwable t) {
			}

			@Override
			public void debug(Object message) {
			}

			@Override
			public void debug(Object message, Throwable t) {
			}

			@Override
			public void info(Object message) {
			}

			@Override
			public void info(Object message, Throwable t) {
			}

			@Override
			public void warn(Object message) {
			}

			@Override
			public void warn(Object message, Throwable t) {
			}

			@Override
			public void error(Object message) {
				logger.error(message.toString());
			}

			@Override
			public void error(Object message, Throwable t) {
				logger.error(message.toString(), t);
			}

			@Override
			public void fatal(Object message) {
				logger.error(message.toString());
			}

			@Override
			public void fatal(Object message, Throwable t) {
				logger.error(message.toString(), t);
			}
		};
	}

	@Override
	protected void doBegin(Object transaction, TransactionDefinition definition) {
		if (logger.isTraceEnabled()) {
			StopWatch sw = new StopWatch();
			sw.start();
			super.doBegin(transaction, definition);
			sw.stop();
			logger.trace("Begin transaction [time = {}ms]. For '{}' by Definition = {}.", sw.getTime(),
					definition.getName(), definition);
		} else {
			super.doBegin(transaction, definition);
		}
	}

	@Override
	protected Object doSuspend(Object transaction) {
		logger.trace("Suspend current transaction.");
		return super.doSuspend(transaction);
	}

	@Override
	protected void doResume(Object transaction, Object suspendedResources) {
		logger.trace("Resume former transaction.");
		super.doResume(transaction, suspendedResources);
	}

	@Override
	protected void doCommit(DefaultTransactionStatus status) {
		if (logger.isTraceEnabled()) {
			StopWatch sw = new StopWatch();
			sw.start();
			super.doCommit(status);
			sw.stop();
			logger.trace("Commit transaction [time = {}ms].", sw.getTime());
		} else {
			super.doCommit(status);
		}
	}

	@Override
	protected void doRollback(DefaultTransactionStatus status) {
		if (logger.isTraceEnabled()) {
			StopWatch sw = new StopWatch();
			sw.start();
			super.doRollback(status);
			sw.stop();
			logger.trace("Rollback transaction [time = {}ms].", sw.getTime());
		} else {
			super.doRollback(status);
		}
	}
}
