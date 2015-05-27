package iz.dbui.web.process.database.dto.definition;

import iz.dbui.web.process.database.dto.Column;

/**
 *
 * @author iz_j
 *
 */
public final class ColumnInfo extends Column {

	public String tableName;
	public String comments;
	public String description;
	public boolean isKey;

	@Override
	public String toString() {
		return "ColumnInfo [tableName=" + tableName + ", comments=" + comments + ", description=" + description
				+ ", isKey=" + isKey + ", columnName=" + columnName + ", dataType=" + dataType + "]";
	}
}
