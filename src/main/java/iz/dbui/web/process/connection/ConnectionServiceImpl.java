package iz.dbui.web.process.connection;

import iz.dbui.base.AppDataManager;
import iz.dbui.web.process.connection.dto.Connection;
import iz.dbui.web.process.connection.dto.Connections;
import iz.dbui.web.spring.jdbc.DataSourceRouter;
import iz.dbui.web.spring.jdbc.DatabaseException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author izumi_j
 *
 */
@Service
public class ConnectionServiceImpl implements ConnectionService {
	private static final Logger logger = LoggerFactory.getLogger(ConnectionServiceImpl.class);

	@Autowired
	private DataSource dataSource;

	@Override
	public void add(Connection connection) {
		connection.id = UUID.randomUUID().toString();

		final Connections connections = AppDataManager.load(Connections.class);
		connections.getList().add(connection);
		AppDataManager.save(connections);
		logger.debug("Saved Connections = {}", connections);
	}

	@Override
	public List<Connection> getAll() {
		final Connections connections = AppDataManager.load(Connections.class);
		connections.getList().sort((o1, o2) -> {
			return ObjectUtils.compare(o1.name, o2.name);
		});
		logger.debug("Sorted Connections = {}", connections);
		return connections.getList();
	}

	@Override
	public void remove(String id) {
		final Connections connections = AppDataManager.load(Connections.class);
		final boolean removed = connections.getList().removeIf(c -> {
			return StringUtils.equals(id, c.id);
		});
		if (removed) {
			AppDataManager.save(connections);
			logger.debug("{} was removed.", id);
		} else {
			logger.warn("{} was not found!", id);
		}
	}

	@Override
	public Connection get(String id) {
		final Connections connections = AppDataManager.load(Connections.class);
		final Optional<Connection> c = connections.getList().stream().filter(e -> {
			return StringUtils.equals(id, e.id);
		}).findFirst();

		if (!c.isPresent()) {
			throw new IllegalStateException("Connection was not found! id = " + id);
		}
		return c.get();
	}

	@Override
	public void activateConnection(String id) throws DatabaseException {
		final Connection c = get(id);

		if (dataSource instanceof DataSourceRouter) {
			((DataSourceRouter)dataSource).addNewConnection(c.id, c.host, c.port, c.sid, c.username, c.password);
		} else {
			throw new IllegalStateException("DataSource must be DataSourceRouter!");
		}
	}
}
