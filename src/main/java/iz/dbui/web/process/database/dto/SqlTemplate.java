package iz.dbui.web.process.database.dto;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author izumi_j
 *
 */
public class SqlTemplate {
	public enum TemplateType {
		TABLE, FREE;
	}

	public static SqlTemplate forFreeSql() {
		final SqlTemplate t = new SqlTemplate();
		t.id = UUID.randomUUID().toString();
		t.type = TemplateType.FREE;
		t.tableName = "";
		t.name = "Free SQL";
		t.sentence = "";
		return t;
	}

	public static SqlTemplate forTableSql(String tableName) {
		final SqlTemplate t = new SqlTemplate();
		t.id = UUID.randomUUID().toString();
		t.type = TemplateType.TABLE;
		t.tableName = StringUtils.upperCase(tableName);
		t.name = StringUtils.upperCase(tableName);
		t.sentence = "SELECT * FROM " + StringUtils.upperCase(tableName) + System.lineSeparator();
		return t;
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
