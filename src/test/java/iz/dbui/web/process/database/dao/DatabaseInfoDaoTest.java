package iz.dbui.web.process.database.dao;

import static org.junit.Assert.assertTrue;
import iz.dbui.AbstractSpringTest;
import iz.dbui.web.spring.jdbc.ConnectionContext;

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
		dao.findAllTableNames(ConnectionContext.getCurrentId());
		sw.stop();
		final long time1st = sw.getTime();

		sw.reset();
		sw.start();
		dao.findAllTableNames(ConnectionContext.getCurrentId());
		sw.stop();
		final long time2nd = sw.getTime();

		logger.debug("time = {} / {}", time1st, time2nd);

		// Check value & cache enabled
		assertTrue(time2nd <= time1st);
	}
}
