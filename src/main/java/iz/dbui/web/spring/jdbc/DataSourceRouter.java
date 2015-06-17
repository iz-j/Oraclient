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
 * @author iz_j
 *
 */
public final class DataSourceRouter extends AbstractDataSource {
	private static final Logger logger = LoggerFactory.getLogger(DataSourceRouter.class);

	private final Map<String, DataSource> targetDataSources = new ConcurrentHashMap<>();

	/**
	 * Add new connection.
	 *
	 * @param connectionInfo
	 * @throws DatabaseException
	 */
	public void addNewConnection(JdbcConnectionInfo connectionInfo)
			throws DatabaseException {
		final String url = connectionInfo.getOracleUrl();

		if (targetDataSources.containsKey(connectionInfo.getId())) {
			logger.debug("DataSource for {}({}) has already been created.", connectionInfo.getId(), url);
			return;
		}

		final org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
		ds.setDriverClassName("oracle.jdbc.OracleDriver");
		ds.setUrl(url);
		ds.setUsername(connectionInfo.getUsername());
		ds.setPassword(connectionInfo.getPassword());

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

		targetDataSources.put(connectionInfo.getId(), ds);
		logger.debug("New connection created. {} {}/{}", url, connectionInfo.getUsername(),
				connectionInfo.getPassword());
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
