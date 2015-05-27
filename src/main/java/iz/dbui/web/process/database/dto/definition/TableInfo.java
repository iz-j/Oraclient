package iz.dbui.web.process.database.dto.definition;

import java.util.List;

/**
 *
 * @author iz_j
 *
 */
public final class TableInfo {
	public String tableName;
	public String comments;
	public String description;
	public List<ColumnInfo> columns;

	@Override
	public String toString() {
		return "TableInfo [tableName=" + tableName + ", comments=" + comments + ", description=" + description
				+ ", columns=" + columns + "]";
	}
}
