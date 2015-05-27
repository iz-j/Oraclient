package iz.dbui.web.process.database.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foundationdb.sql.StandardException;
import com.foundationdb.sql.parser.SQLParser;
import com.foundationdb.sql.parser.StatementNode;

/**
 *
 * @author iz_j
 *
 */
public final class SqlAnalyzer {

	public enum AnalysisResult {
		QUERY, EXECUTABLE, OTHER;
	}

	private static final Logger logger = LoggerFactory.getLogger(SqlAnalyzer.class);

	private SqlAnalyzer() {
	}

	public static AnalysisResult analyze(String sentence) {
		final SQLParser parser = new SQLParser();

		StatementNode stmt = null;
		try {
			stmt = parser.parseStatement(sentence);

			logger.trace("statement = {}", stmt.statementToString());
			switch (stmt.statementToString()) {
			case "SELECT":
				return AnalysisResult.QUERY;
			default:
				return AnalysisResult.EXECUTABLE;
			}

		} catch (StandardException e) {
			logger.debug("Wrong statement -> {}", e.getMessage());
			return AnalysisResult.OTHER;
		}
	}
}
