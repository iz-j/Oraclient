package iz.dbui.web.process.database.other;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author izumi_j
 *
 */
public final class SqlCompletionWords {
	private SqlCompletionWords() {
	}

	public static final List<String> RESERVED = Collections.unmodifiableList(Arrays.asList("ACCESS",
			"ADD",
			"ALL",
			"ALTER",
			"AND",
			"ANY",
			"AS",
			"ASC",
			"AUDIT",
			"BETWEEN",
			"BY",
			"CHAR",
			"CHECK",
			"CLUSTER",
			"COLUMN",
			"COLUMN_VALUE",
			"COMMENT",
			"COMPRESS",
			"CONNECT",
			"CREATE",
			"CURRENT",
			"DATE",
			"DECIMAL",
			"DEFAULT",
			"DELETE",
			"DESC",
			"DISTINCT",
			"DROP",
			"ELSE",
			"EXCLUSIVE",
			"EXISTS",
			"FILE",
			"FLOAT",
			"FOR",
			"FROM",
			"GRANT",
			"GROUP",
			"HAVING",
			"IDENTIFIED",
			"IMMEDIATE",
			"IN",
			"INCREMENT",
			"INDEX",
			"INITIAL",
			"INSERT",
			"INTEGER",
			"INTERSECT",
			"INTO",
			"IS",
			"LEVEL",
			"LIKE",
			"LOCK",
			"LONG",
			"MAXEXTENTS",
			"MINUS",
			"MLSLABEL",
			"MODE",
			"MODIFY",
			"NESTED_TABLE_ID",
			"NOAUDIT",
			"NOCOMPRESS",
			"NOT",
			"NOWAIT",
			"NULL",
			"NUMBER",
			"OF",
			"OFFLINE",
			"ON",
			"ONLINE",
			"OPTION",
			"OR",
			"ORDER",
			"PCTFREE",
			"PRIOR",
			"PRIVILEGES",
			"PUBLIC",
			"RAW",
			"RENAME",
			"RESOURCE",
			"REVOKE",
			"ROW",
			"ROWID",
			"ROWNUM",
			"ROWS",
			"SELECT",
			"SESSION",
			"SET",
			"SHARE",
			"SIZE",
			"SMALLINT",
			"START",
			"SUCCESSFUL",
			"SYNONYM",
			"SYSDATE",
			"TABLE",
			"THEN",
			"TO",
			"TRIGGER",
			"UID",
			"UNION",
			"UNIQUE",
			"UPDATE",
			"USER",
			"VALIDATE",
			"VALUES",
			"VARCHAR",
			"VARCHAR2",
			"VIEW",
			"WHENEVER",
			"WHERE",
			"WITH"
			));

	public static final List<String> UTILITIES = Collections.unmodifiableList(Arrays.asList("LIKE '%'", "LIKE '%%'",
			"BETWEEN AND "));

	public static final List<String> ALL;
	static {
		final List<String> all = new ArrayList<>();
		all.addAll(RESERVED);
		all.addAll(UTILITIES);
		ALL = Collections.unmodifiableList(all);
	}
}
