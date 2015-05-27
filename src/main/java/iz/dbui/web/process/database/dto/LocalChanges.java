package iz.dbui.web.process.database.dto;

import java.util.List;
import java.util.Map;

public final class LocalChanges {
	/**
	 * Name of table that be edited.
	 */
	public String tableName;
	/**
	 * Column names of table that be edited.
	 */
	public List<Column> columns;
	/**
	 * Edited values.<br>
	 * [key = rowid for local, value = Map[key = column index, value = new value]]
	 */
	public Map<String, Map<Integer, String>> editedMap;
	/**
	 * Removed rowids.
	 */
	public List<String> removedRowids;

	@Override
	public String toString() {
		return "LocalChanges [tableName="
				+ tableName
				+ ", columns="
				+ columns
				+ ", editedMap="
				+ editedMap
				+ ", removedRowids="
				+ removedRowids
				+ "]";
	}
}
