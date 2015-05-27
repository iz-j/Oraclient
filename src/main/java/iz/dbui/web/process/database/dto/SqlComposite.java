package iz.dbui.web.process.database.dto;

import java.util.ArrayList;

/**
 *
 * @author iz_j
 *
 */
public final class SqlComposite {

	public String id;
	public String name;
	public ArrayList<SqlTemplate> templates = new ArrayList<>();

	@Override
	public String toString() {
		return "SqlComposite [id=" + id + ", name=" + name + ", templates=" + templates + "]";
	}
}
