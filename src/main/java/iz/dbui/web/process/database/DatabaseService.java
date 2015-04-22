package iz.dbui.web.process.database;

import iz.dbui.web.process.database.dto.ExecutionResult;
import iz.dbui.web.process.database.dto.LocalChanges;
import iz.dbui.web.process.database.dto.SqlTemplate;
import iz.dbui.web.spring.jdbc.DatabaseException;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author izumi_j
 *
 */
public interface DatabaseService {

	List<SqlTemplate> getMatchedTemplates(String term);

	ExecutionResult executeSql(SqlTemplate sql) throws DatabaseException;

	@Transactional(rollbackFor = { DatabaseException.class })
	Map<String, String> save(LocalChanges changes) throws DatabaseException;

	void save(SqlTemplate sql);

	List<String> getSqlCompletions(String term, String tableName);
}
