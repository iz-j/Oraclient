package iz.oraclient.web.process.connection;

import iz.oraclient.web.process.connection.dto.Connection;
import iz.oraclient.web.spring.jdbc.DatabaseException;

import java.util.List;

/**
 *
 * @author izumi_j
 *
 */
public interface ConnectionService {

	void add(Connection connection);

	List<Connection> getAll();

	void remove(String id);

	Connection get(String id);

	void activateConnection(String id) throws DatabaseException;
}
