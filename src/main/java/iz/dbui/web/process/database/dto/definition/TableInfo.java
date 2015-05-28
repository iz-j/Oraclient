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
	public PrimaryKeyInfo primaryKey;
	public List<IndexInfo> indexes;
	public List<ForeignKeyInfo> foreignKeys;

	@Override
	public String toString() {
		return "TableInfo [tableName=" + tableName + ", comments=" + comments + ", description=" + description
				+ ", columns=" + columns + ", primaryKey=" + primaryKey + ", indexes=" + indexes + ", foreignKeys="
				+ foreignKeys + "]";
	}
}
