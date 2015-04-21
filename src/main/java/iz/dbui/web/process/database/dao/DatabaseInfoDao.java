package iz.dbui.web.process.database.dao;

import iz.dbui.web.process.database.dto.ColumnInfo;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author izumi_j
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
	 * @param connectionId
	 *            for cache by each connections.
	 * @param tableName
	 * @return column informations
	 */
	List<ColumnInfo> findColumnsBy(String connectionId, String tableName);

	/**
	 * @param connectionId
	 *            for cache by each connections.
	 * @param tableName
	 * @return primary key names
	 */
	List<String> findPrimaryKeysBy(String connectionId, String tableName);

	/**
	 * @param sqlSentence
	 * @return column informations and query results as string
	 */
	Pair<List<ColumnInfo>, List<List<String>>> executeQuery(String sqlSentence);

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
