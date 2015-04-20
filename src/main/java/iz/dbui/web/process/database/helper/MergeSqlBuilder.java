package iz.dbui.web.process.database.helper;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * TODO Resolve oracle dependencies!
 *
 * @author izumi_j
 *
 */
public final class MergeSqlBuilder {
	private MergeSqlBuilder() {
	}

	public static String insert(Map<Integer, String> source, String tableName, List<String> columnNames) {
		final StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ").append(tableName).append(" (");

		final int before1 = sql.length();
		source.forEach((columnIndex, value) -> {
			if (before1 < sql.length()) {
				sql.append(", ");
			}
			sql.append(columnNames.get(columnIndex));
		});

		sql.append(") VALUES (");

		final int before2 = sql.length();
		source.forEach((columnIndex, value) -> {
			if (before2 < sql.length()) {
				sql.append(", ");
			}
			sql.append(toSqlVal(value));
		});

		sql.append(")");

		return sql.toString();
	}

	public static String update(String rowid, Map<Integer, String> source, String tableName, List<String> columnNames) {
		final StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ").append(tableName).append(" SET ");

		final int before = sql.length();
		source.forEach((columnIndex, value) -> {
			if (before < sql.length()) {
				sql.append(", ");
			}
			sql.append(columnNames.get(columnIndex)).append(" = ").append(toSqlVal(value));
		});

		sql.append(" WHERE ROWID = ").append(toSqlVal(rowid));

		return sql.toString();
	}

	public static String delete(String rowid, String tableName) {
		return new StringBuilder()
		.append("DELETE FROM ")
		.append(tableName)
		.append(" WHERE ROWID = ")
		.append(toSqlVal(rowid))
		.toString();
	}

	private static String toSqlVal(String value) {
		if (StringUtils.isEmpty(value)) {
			return "null";
		} else {
			return "'" + value + "'";
		}
	}
}
