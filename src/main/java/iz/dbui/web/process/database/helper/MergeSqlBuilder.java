package iz.dbui.web.process.database.helper;

import iz.dbui.web.process.database.dto.ColumnInfo;
import iz.dbui.web.process.database.dto.ColumnInfo.DataType;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

/**
 * TODO Resolve oracle dependencies!
 *
 * @author izumi_j
 *
 */
public final class MergeSqlBuilder {
	private MergeSqlBuilder() {
	}

	/**
	 * @param source
	 *            key = columnIndex, value = insert value
	 * @param tableName
	 * @param columns
	 * @return INSERT
	 */
	public static String insert(Map<Integer, String> source, String tableName, List<ColumnInfo> columns) {
		final StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ").append(tableName).append(" (");

		final int before1 = sql.length();
		columns.forEach((column) -> {
			if (before1 < sql.length()) {
				sql.append(", ");
			}
			sql.append(column.columnName);
		});

		sql.append(") VALUES (");

		final int before2 = sql.length();
		columns.forEach((column) -> {
			if (before2 < sql.length()) {
				sql.append(", ");
			}
			final int index = columns.indexOf(column);
			sql.append(toSqlVal(source.get(index), column.dataType));
		});

		sql.append(")");

		return sql.toString();
	}

	/**
	 *
	 * @param keys
	 * @param source
	 *            key = columnIndex, value = update value
	 * @param tableName
	 * @param columns
	 * @param pks
	 * @return UPDATE
	 */
	public static String update(List<String> keys, Map<Integer, String> source, String tableName,
			List<ColumnInfo> columns, List<String> pks) {
		final StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ").append(tableName).append(" SET ");

		final int before1 = sql.length();
		source.forEach((columnIndex, value) -> {
			if (before1 < sql.length()) {
				sql.append(", ");
			}
			final ColumnInfo column = columns.get(columnIndex);
			sql.append(column.columnName).append(" = ").append(toSqlVal(value, column.dataType));
		});

		sql.append(" WHERE ");

		final List<Integer> pkIndexes = ColumnInfoHelper.primaryKeyIndexes(columns, pks);
		final int before2 = sql.length();
		keys.forEach(key -> {
			if (before2 < sql.length()) {
				sql.append(" AND ");
			}

			// keys and pkIndexes are same order!
			final int columnIndex = pkIndexes.get(keys.indexOf(key));

			final ColumnInfo column = columns.get(columnIndex);
			sql.append(column.columnName).append(" = ").append(toSqlVal(key, column.dataType));
			});

		return sql.toString();
	}

	/**
	 * @param keys
	 * @param tableName
	 * @param columns
	 * @param pks
	 * @return DELETE
	 */
	public static String delete(List<String> keys, String tableName, List<ColumnInfo> columns, List<String> pks) {
		final StringBuilder sql = new StringBuilder().append("DELETE FROM ").append(tableName).append(" WHERE ");

		final List<Integer> pkIndexes = ColumnInfoHelper.primaryKeyIndexes(columns, pks);
		final int before = sql.length();
		keys.forEach(key -> {
			if (before < sql.length()) {
				sql.append(" AND ");
			}

			final int columnIndex = pkIndexes.get(keys.indexOf(key));

			final ColumnInfo column = columns.get(columnIndex);
			sql.append(column.columnName).append(" = ").append(toSqlVal(key, column.dataType));
		});

		return sql.toString();
	}

	private static String toSqlVal(String value, DataType dataType) {
		if (StringUtils.isEmpty(value)) {
			return "null";
		}

		switch (dataType) {
		case NUMBER:
			return StringUtils.trim(value);
		case DATE:
			final DateTime parsed = DateTimeValueParser.parse(value);
			if (parsed != null) {
				return "TO_DATE('" + parsed.toString("yyyy-MM-dd HH:mm:ss") + "', 'YYYY-MM-DD HH24:MI:SS')";
			} else {
				return "'" + value + "'";
			}
		case STRING:
		case OTHER:
		default:
			return "'" + value + "'";
		}
	}
}
