package iz.dbui.web.process.database.dao;

import iz.dbui.web.process.database.dto.ColumnInfo;
import iz.dbui.web.process.database.dto.ExecutionResult;

import java.util.List;

/**
 *
 * @author izumi_j
 *
 */
public interface DatabaseInfoDao {

	List<String> findAllTableNames(String connectionId);

	List<ColumnInfo> findColumnsBy(String connectionId, String tableName);

	ExecutionResult executeQuery(String sqlSentence);

	ExecutionResult executeUpdate(String sqlSentence);

	ExecutionResult execute(String sqlSentence);
}
