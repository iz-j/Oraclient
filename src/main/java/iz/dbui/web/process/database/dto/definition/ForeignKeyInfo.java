package iz.dbui.web.process.database.dto.definition;

import java.util.List;

/**
 *
 * @author iz-j
 *
 */
public final class ForeignKeyInfo {

	public String name;
	public List<String> columns;
	public String reference;

	@Override
	public String toString() {
		return "ForeignKeyInfo [name=" + name + ", columns=" + columns + ", reference=" + reference + "]";
	}
}
