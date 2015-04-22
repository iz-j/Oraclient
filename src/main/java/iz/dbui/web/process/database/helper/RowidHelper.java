package iz.dbui.web.process.database.helper;

import iz.dbui.web.process.database.dto.ColumnInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author izumi_j
 *
 */
public final class RowidHelper {
	private RowidHelper() {
	}

	private static final String PK_DELIMITER = "$$$";
	private static final String NONE_ROWID = "none";

	/**
	 * @param records
	 * @param columns
	 * @param pks
	 */
	public static void allocateRowid(List<List<String>> records, List<ColumnInfo> columns, List<String> pks) {
		final List<Integer> pkIndexes = ColumnInfoHelper.primaryKeyIndexes(columns, pks);

		records.forEach(rec -> {
			final String rowid = pkIndexes.stream().map(pkIndex -> {
				return rec.get(pkIndex);
			}).collect(Collectors.joining(PK_DELIMITER));

			rec.add(0, StringUtils.isNotEmpty(rowid) ? rowid : NONE_ROWID);
		});
	}

	/**
	 * @param rowid
	 * @return delimited keys
	 */
	public static List<String> rowidToPrimaryKeys(String rowid) {
		return Arrays.asList(StringUtils.split(rowid, PK_DELIMITER));
	}

	/**
	 * @param source
	 *            key = columnIndex, value = record value
	 * @param columns
	 * @param pks
	 * @param defaultRowid
	 * @return rowid
	 */
	public static String createRowid(Map<Integer, String> source, List<ColumnInfo> columns, List<String> pks,
			String defaultRowid) {
		final List<Integer> pkIndexes = ColumnInfoHelper.primaryKeyIndexes(columns, pks);

		final List<String> pkValues = pkIndexes.stream().map(pkIndex -> {
			return source.get(pkIndex);
		}).collect(Collectors.toList());

		if (!pkValues.stream().anyMatch(pkValue -> {
			return StringUtils.isEmpty(pkValue);
		})) {
			// New rowid.
			return StringUtils.join(pkValues, PK_DELIMITER);
		} else {
			// Rowid was not changed.
			return defaultRowid;
		}
	}
}
