package iz.oraclient.web.process.connection;

import iz.oraclient.base.AppDataManager;
import iz.oraclient.web.process.connection.dto.Connection;
import iz.oraclient.web.process.connection.dto.Connections;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author izumi_j
 *
 */
@Service
public class ConnectionServiceImpl implements ConnectionService {
	private static final Logger logger = LoggerFactory.getLogger(ConnectionServiceImpl.class);

	@Override
	public void add(Connection connection) {
		connection.id = RandomStringUtils.randomAlphanumeric(20);

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

}
