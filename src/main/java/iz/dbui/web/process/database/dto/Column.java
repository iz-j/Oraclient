package iz.dbui.web.process.database.dto;

/**
 *
 * @author iz_j
 *
 */
public class Column {
	/**
	 * Broad category!
	 */
	public enum DataType {
		STRING, NUMBER, DATE, OTHER;
	}

	public String columnName;
	public DataType dataType;

	@Override
	public String toString() {
		return "ColumnInfo [columnName=" + columnName + ", dataType=" + dataType + "]";
	}
}
