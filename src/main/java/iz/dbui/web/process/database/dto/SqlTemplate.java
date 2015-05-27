package iz.dbui.web.process.database.dto;

import java.util.Comparator;
import java.util.UUID;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author iz_j
 *
 */
public class SqlTemplate {
	public enum TemplateType {
		TABLE, FREE;
	}

	public static final Comparator<SqlTemplate> COMPARATOR = ((t1, t2) -> {
		int ret = ObjectUtils.compare(t1.name, t2.name);
		if (ret == 0) {
			ret = t1.type.ordinal() - t2.type.ordinal();
		}
		if (ret == 0) {
			ret = ObjectUtils.compare(t1.id, t2.id);
		}
		return ret;
	});

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
