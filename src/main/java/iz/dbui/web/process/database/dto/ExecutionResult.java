package iz.dbui.web.process.database.dto;

import java.util.List;

/**
 *
 * @author izumi_j
 *
 */
public class ExecutionResult {
	public boolean query = true;

	public List<ColumnInfo> columns;
	public List<String> columnIds;
	public List<List<String>> records;

	public int updatedCount = 0;

	@Override
	public String toString() {
		return "ExecutionResult [query="
				+ query
				+ ", columns="
				+ columns
				+ ", columnIds="
				+ columnIds
				+ ", records="
				+ records
				+ ", updatedCount="
				+ updatedCount
				+ "]";
	}
}
