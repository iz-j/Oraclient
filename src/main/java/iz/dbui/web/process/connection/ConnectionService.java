package iz.dbui.web.process.connection;

import iz.dbui.web.process.connection.dto.Connection;
import iz.dbui.web.spring.jdbc.DatabaseException;

import java.util.List;

/**
 *
 * @author iz_j
 *
 */
public interface ConnectionService {

	void add(Connection connection);

	List<Connection> getAll();

	void remove(String id);

	Connection get(String id);

	void activateConnection(String id) throws DatabaseException;
}
