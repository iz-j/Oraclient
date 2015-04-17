package iz.dbui.web.process.database;

import iz.dbui.web.process.database.dto.ExecutionResult;
import iz.dbui.web.process.database.dto.SqlTemplate;

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
