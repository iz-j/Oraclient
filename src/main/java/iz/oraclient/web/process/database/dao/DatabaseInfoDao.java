package iz.oraclient.web.process.database.dao;

import java.util.List;

/**
 *
 * @author izumi_j
 *
 */
public interface DatabaseInfoDao {

	List<String> findAllTableNames(String connectionId);
}
