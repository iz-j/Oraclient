package iz.dbui.web.process.database.helper;

import static org.junit.Assert.*;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RowidInjectorTest {
	private static final Logger logger = LoggerFactory.getLogger(RowidInjectorTest.class);

	@Test
	public void test() {
		String sql = null;

		sql = RowidInjector.inject("SELECT * FROM HOGE");
		logger.debug(sql);
		assertTrue(StringUtils.equalsIgnoreCase("SELECT rowid AS _rowid_, * FROM HOGE", sql));
	}

}
