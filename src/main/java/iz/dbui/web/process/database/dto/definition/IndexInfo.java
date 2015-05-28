package iz.dbui.web.process.database.dto.definition;

import java.util.List;

/**
 *
 * @author iz-j
 *
 */
public final class IndexInfo {

	public String name;
	public List<String> columns;

	@Override
	public String toString() {
		return "IndexInfo [name=" + name + ", columns=" + columns + "]";
	}
}
