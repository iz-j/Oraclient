package iz.dbui.web.process.database.dto;

import java.util.List;
import java.util.Map;

public class LocalChanges {
	public String tableName;
	public List<String> columnNames;
	public Map<String, Map<Integer, String>> editedMap;
	public List<String> removedRowids;

	@Override
	public String toString() {
		return "LocalChanges [tableName="
				+ tableName
				+ ", columnNames="
				+ columnNames
				+ ", editedMap="
				+ editedMap
				+ ", removedRowids="
				+ removedRowids
				+ "]";
	}
}
