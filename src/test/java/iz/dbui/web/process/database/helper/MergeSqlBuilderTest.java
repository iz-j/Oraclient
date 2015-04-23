package iz.dbui.web.process.database.helper;

import static org.junit.Assert.*;
import iz.dbui.web.process.database.dto.ColumnInfo;
import iz.dbui.web.process.database.dto.ColumnInfo.DataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MergeSqlBuilderTest {
	private static final Logger logger = LoggerFactory.getLogger(MergeSqlBuilderTest.class);

	private static final String TABLE_NAME = "HOGE";

	private static final List<ColumnInfo> COLUMNS;
	static {
		COLUMNS = new ArrayList<>();
		ColumnInfo c = new ColumnInfo();
		c.columnName = "ID";
		c.dataType = DataType.NUMBER;
		COLUMNS.add(c);
		c = new ColumnInfo();
		c.columnName = "TXT";
		c.dataType = DataType.STRING;
		COLUMNS.add(c);
		c = new ColumnInfo();
		c.columnName = "DT";
		c.dataType = DataType.DATE;
		COLUMNS.add(c);
		c = new ColumnInfo();
		c.columnName = "NA";
		c.dataType = DataType.OTHER;
		COLUMNS.add(c);
	}

	private static final List<String> PKS = Arrays.asList("ID");

	@Test
	public void test_insert() {
		final Map<Integer, String> source = new HashMap<>();
		source.put(0, "1");
		source.put(1, "test");
		source.put(2, new DateTime(0).toString(DateTimeFormat.mediumDate()));

		final String sql = MergeSqlBuilder.insert(source, TABLE_NAME, COLUMNS);
		logger.debug(sql);
		assertEquals(
				"INSERT INTO HOGE (ID, TXT, DT, NA) VALUES (1, 'test', TO_DATE('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), null)",
				sql);
	}

	@Test
	public void test_update() {
		final Map<Integer, String> source = new HashMap<>();
		source.put(0, "1");
		source.put(1, "test");

		final String sql = MergeSqlBuilder
				.update(RowidHelper.rowidToPrimaryKeys("1"), source, TABLE_NAME, COLUMNS, PKS);
		logger.debug(sql);
		assertEquals("UPDATE HOGE SET ID = 1, TXT = 'test' WHERE ID = 1", sql);
	}
}
