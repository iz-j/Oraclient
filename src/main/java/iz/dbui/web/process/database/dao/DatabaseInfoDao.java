package iz.dbui.web.process.database.dao;

import iz.dbui.web.process.database.dto.Column;
import iz.dbui.web.process.database.dto.definition.ColumnInfo;
import iz.dbui.web.process.database.dto.definition.TableInfo;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author iz_j
 *
 */

public interface DatabaseInfoDao {

	/**
	 * @param connectionId
	 *            for cache by each connections.
	 * @return table names
	 */
	List<String> findAllTableNames(String connectionId);

	/**
	 * @param tableName
	 * @return table info without column infos
	 */
	TableInfo findTableBy(String tableName);

	/**
	 * @param connectionId
	 *            for cache by each connections.
	 * @param tableName
	 * @return column definitions
	 */
	List<ColumnInfo> findColumnsBy(String connectionId, String tableName);

	/**
	 * @param tableName
	 * @return column definitions
	 */
	List<ColumnInfo> findColumnsBy(String tableName);

	/**
	 * @param tableName
	 * @return primary key names
	 */
	List<String> findPrimaryKeysBy(String tableName);

	/**
	 * @param sqlSentence
	 * @return column informations and query results as string
	 */
	Pair<List<Column>, List<List<String>>> executeQuery(String sqlSentence);

	/**
	 * @param sqlSentence
	 * @return updated count
	 */
	int executeUpdate(String sqlSentence);

	/**
	 * @param sqlSentence
	 */
	void execute(String sqlSentence);
}
