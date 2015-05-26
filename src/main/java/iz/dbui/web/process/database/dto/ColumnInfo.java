package iz.dbui.web.process.database.dto;

/**
 *
 * @author izumi_j
 *
 */
public final class ColumnInfo {
	/**
	 * Broad category!
	 */
	public enum DataType {
		STRING, NUMBER, DATE, OTHER;
	}

	public String tableName;
	public String columnName;
	public DataType dataType;
	public String comments;

	@Override
	public String toString() {
		return "ColumnInfo [tableName=" + tableName + ", columnName=" + columnName + ", dataType=" + dataType
				+ ", comments=" + comments + "]";
	}
}
