package iz.oraclient.web.process.database;

import iz.oraclient.AbstractSpringTest;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SqlServiceTest extends AbstractSpringTest {
	private static final Logger logger = LoggerFactory.getLogger(SqlServiceTest.class);

	@Autowired
	private DatabaseService service;

	@Test
	public void test_getMatchedTemplates() {
		final StopWatch sw = new StopWatch();
		sw.start();
		logger.debug(service.getMatchedTemplates("AC_COM_ITEM").toString());
		sw.stop();

		logger.debug("Time = {}ms", sw.getTime());
	}

}
