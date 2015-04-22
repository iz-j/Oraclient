package iz.dbui.web.process.database.helper;

import static org.junit.Assert.*;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
public class RowidInjectorTest {
	private static final Logger logger = LoggerFactory.getLogger(RowidInjectorTest.class);

	@Test
	public void test_standard() {
		String sql = RowidInjector.inject("SELECT * FROM hoge");
		logger.debug(sql);
		assertTrue(StringUtils.equalsIgnoreCase("SELECT rowid AS rowid_, hoge.* FROM hoge", sql));
	}

	@Test
	public void test_withAlias() {
		String sql = RowidInjector.inject("SELECT t.* FROM hoge t");
		logger.debug(sql);
		assertTrue(StringUtils.equalsIgnoreCase("SELECT rowid AS rowid_, t.* FROM hoge t", sql));
	}

	@Test
	public void test_columns() {
		String sql = RowidInjector.inject("SELECT id, dt FROM hoge");
		logger.debug(sql);
		assertTrue(StringUtils.equalsIgnoreCase("SELECT rowid AS rowid_, id, dt FROM hoge", sql));
	}

}
