package iz.oraclient.web.process.database.dto;

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

	public String owner;
	public String tableName;
	public String columnName;
	public DataType dataType;
	public long columnId;
	public String comments;

	@Override
	public String toString() {
		return "ColumnInfo [owner="
				+ owner
				+ ", tableName="
				+ tableName
				+ ", columnName="
				+ columnName
				+ ", dataType="
				+ dataType
				+ ", columnId="
				+ columnId
				+ ", comments="
				+ comments
				+ "]";
	}
}
