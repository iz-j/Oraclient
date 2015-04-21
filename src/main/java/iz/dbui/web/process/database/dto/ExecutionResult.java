package iz.dbui.web.process.database.dto;

import java.util.List;

/**
 *
 * @author izumi_j
 *
 */
public class ExecutionResult {
	/**
	 * If holds query result, this is true.
	 */
	public boolean query = true;

	/**
	 * If holds query result, it is true when primary keys are set.
	 */
	public boolean editable = false;
	/**
	 * It is set when this represents query for table.
	 */
	public String tableName;
	/**
	 * Column informations.
	 */
	public List<ColumnInfo> columns;
	/**
	 * Column names.
	 */
	public List<String> columnNames;
	/**
	 * Query result with rowid that is for local operations.
	 */
	public List<List<String>> records;

	/**
	 * Count of insert or update or delete.
	 */
	public int updatedCount = 0;

	@Override
	public String toString() {
		return "ExecutionResult [query="
				+ query
				+ ", editable="
				+ editable
				+ ", tableName="
				+ tableName
				+ ", columns="
				+ columns
				+ ", columnNames="
				+ columnNames
				+ ", records="
				+ records
				+ ", updatedCount="
				+ updatedCount
				+ "]";
	}
}
