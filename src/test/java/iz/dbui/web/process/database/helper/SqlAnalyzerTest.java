package iz.dbui.web.process.database.helper;

import static org.junit.Assert.*;
import iz.dbui.web.process.database.helper.SqlAnalyzer.AnalysisResult;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlAnalyzerTest {
	private static final Logger logger = LoggerFactory.getLogger(SqlAnalyzerTest.class);

	@Test
	public void test_analyze() {
		AnalysisResult result = null;

		result = SqlAnalyzer.analyze("SELECT * FROM HOGE");
		logger.debug(result.toString());
		assertEquals(result, AnalysisResult.QUERY);

		result = SqlAnalyzer.analyze("UPDATE HOGE SET FUGA = ?");
		logger.debug(result.toString());
		assertEquals(result, AnalysisResult.EXECUTABLE);

		result = SqlAnalyzer.analyze("CREATE TABLE HOGE (\"FUGA\" TEXT)");
		logger.debug(result.toString());
		assertEquals(result, AnalysisResult.EXECUTABLE);

		result = SqlAnalyzer.analyze("faskl;fjasdkl;j");
		logger.debug(result.toString());
		assertEquals(result, AnalysisResult.OTHER);
	}

}
