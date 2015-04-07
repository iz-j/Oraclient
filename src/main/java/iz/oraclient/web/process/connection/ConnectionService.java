package iz.oraclient.web.process.connection;

import iz.oraclient.web.process.connection.dto.Connection;

import java.util.List;

/**
 *
 * @author izumi_j
 *
 */
public interface ConnectionService {

	void add(Connection connection);

	List<Connection> getAll();
}
