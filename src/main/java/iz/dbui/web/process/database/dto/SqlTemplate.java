package iz.dbui.web.process.database.dto;

/**
 *
 * @author izumi_j
 *
 */
public class SqlTemplate {
	public enum TemplateType {
		TABLE, CUSTOM;
	}

	public String id;
	public TemplateType type;
	public String tableName;
	public String name;
	public String sentence;

	@Override
	public String toString() {
		return "SqlTemplate [id="
				+ id
				+ ", type="
				+ type
				+ ", tableName="
				+ tableName
				+ ", name="
				+ name
				+ ", sentence="
				+ sentence
				+ "]";
	}
}
