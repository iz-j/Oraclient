package iz.dbui.web.process.database.dto.definition;

import java.util.List;

/**
 *
 * @author iz-j
 *
 */
public final class PrimaryKeyInfo {

	public String name;
	public List<String> columns;
	public List<String> referers;

	@Override
	public String toString() {
		return "PrimaryKeyInfo [name=" + name + ", columns=" + columns + ", referers=" + referers + "]";
	}
}
