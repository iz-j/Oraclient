package iz.dbui.web.process.database;

import iz.dbui.web.process.database.dto.ExecutionResult;
import iz.dbui.web.process.database.dto.SqlTemplate;
import iz.dbui.web.spring.jdbc.DatabaseException;

import java.util.List;

/**
 *
 * @author izumi_j
 *
 */
public interface DatabaseService {

	List<SqlTemplate> getMatchedTemplates(String term);

	ExecutionResult executeSql(SqlTemplate sql) throws DatabaseException;
}
