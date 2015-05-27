package iz.dbui.web.process.database.other;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author iz_j
 *
 */
public final class SqlCompletionWords {
	private SqlCompletionWords() {
	}

	public static final List<String> TYPICALS = Collections.unmodifiableList(Arrays.asList("AND", "ANY", "AS", "ASC",
			"BETWEEN", "BY", "DELETE FROM", "DESC", "DISTINCT", "ELSE", "EXISTS", "FROM", "GROUP BY", "HAVING", "IN",
			"IN ()", "INSERT INTO", "IS", "LIKE ''", "LIKE '%%'", "NOT", "NOWAIT", "NULL", "ON", "OR", "ORDER BY",
			"ROWID", "ROWNUM", "SELECT", "SELECT * FROM", "SET", "SYSDATE", "THEN", "TO_CHAR()", "TO_DATE()", "UNION",
			"UNION ALL",
			"UPDATE", "VALUES", "WHERE", "WITH", "YYYY-MM-DD", "YYYY-MM-DD HH24:MI:SS"));
}
