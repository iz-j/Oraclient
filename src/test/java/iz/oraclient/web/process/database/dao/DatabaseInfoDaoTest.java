package iz.oraclient.web.process.database.dao;

import static org.junit.Assert.*;
import iz.oraclient.AbstractSpringTest;
import iz.oraclient.web.spring.jdbc.ConnectionDeterminer;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DatabaseInfoDaoTest extends AbstractSpringTest {
	private static final Logger logger = LoggerFactory.getLogger(DatabaseInfoDaoTest.class);

	@Autowired
	private DatabaseInfoDao dao;

	@Test
	public void test_findAllTableNames() {
		final StopWatch sw = new StopWatch();

		sw.start();
		final String tableName1st = dao
				.findAllTableNames(ConnectionDeterminer.getCurrentId())
				.stream()
				.findFirst()
				.get();
		sw.stop();
		final long time1st = sw.getTime();

		sw.reset();
		sw.start();
		final String tableName2nd = dao
				.findAllTableNames(ConnectionDeterminer.getCurrentId())
				.stream()
				.findFirst()
				.get();
		sw.stop();
		final long time2nd = sw.getTime();

		logger.debug("tableName = {} / {}", tableName1st, tableName2nd);
		logger.debug("time = {} / {}", time1st, time2nd);

		// Check value & cache enabled
		assertEquals(tableName1st, tableName2nd);
		assertTrue(time2nd <= time1st);
	}
}
