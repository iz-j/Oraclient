package iz.oraclient.web.process.database;

import iz.oraclient.web.process.database.dto.ExecutionResult;
import iz.oraclient.web.process.database.dto.SqlTemplate;

import java.util.List;

/**
 *
 * @author izumi_j
 *
 */
public interface DatabaseService {

	List<SqlTemplate> getMatchedTemplates(String term);

	ExecutionResult executeSql(SqlTemplate sql);
}
