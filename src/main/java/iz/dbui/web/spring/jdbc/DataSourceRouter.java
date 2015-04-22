package iz.dbui.web.spring.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.AbstractDataSource;

/**
 * This class returns {@link javax.sql.DataSource} by {@link ConnectionContext#getCurrentId()} of current thread.
 *
 * @author izumi_j
 *
 */
public final class DataSourceRouter extends AbstractDataSource {
	private static final Logger logger = LoggerFactory.getLogger(DataSourceRouter.class);

	private final Map<String, DataSource> targetDataSources = new ConcurrentHashMap<>();

	/**
	 * Add new connection.
	 *
	 * @param id
	 * @param host
	 * @param port
	 * @param sid
	 * @param username
	 * @param password
	 * @throws DatabaseException
	 */
	public void addNewConnection(String id, String host, int port, String sid, String username, String password)
			throws DatabaseException {
		final String url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + sid;

		if (targetDataSources.containsKey(id)) {
			logger.debug("DataSource for {}({}) has already been created.", id, url);
			return;
		}

		final org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
		ds.setDriverClassName("oracle.jdbc.OracleDriver");
		ds.setUrl(url);
		ds.setUsername(username);
		ds.setPassword(password);

		ds.setDefaultAutoCommit(false);
		ds.setInitialSize(1);
		ds.setMaxActive(1);
		ds.setMaxIdle(1);
		ds.setMinIdle(1);
		ds.setTestOnBorrow(true);
		ds.setTestOnReturn(false);
		ds.setTestWhileIdle(false);
		ds.setValidationQuery("SELECT 1 FROM DUAL");

		ds.setJdbcInterceptors(StatementManager.class.getName());

		// Validate
		try {
			ds.getConnection().close();
		} catch (SQLException e) {
			logger.error("Failed to connect!", e);
			throw new DatabaseException("Could not connect to [" + url + "] !", e);
		}

		targetDataSources.put(id, ds);
		logger.debug("New connection created. {} {}/{}", url, username, password);
	}

	@Override
	public Connection getConnection() throws SQLException {
		final DataSource ds = targetDataSources.get(ConnectionContext.getCurrentId());
		if (ds != null) {
			return ds.getConnection();
		} else {
			throw new IllegalStateException("DataSource was not found! id = " + ConnectionContext.getCurrentId());
		}
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		throw new UnsupportedOperationException("#getConnection(String username, String password) not supported!");
	}

}
