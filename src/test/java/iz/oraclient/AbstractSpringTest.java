package iz.oraclient;

import iz.oraclient.web.spring.AppConfig;
import iz.oraclient.web.spring.WebMvcConfig;
import iz.oraclient.web.spring.jdbc.ConnectionDeterminer;
import iz.oraclient.web.spring.jdbc.DataSourceRouter;
import iz.oraclient.web.spring.jdbc.DatabaseException;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { AppConfig.class, WebMvcConfig.class })
abstract public class AbstractSpringTest {
	private static final Logger logger = LoggerFactory.getLogger(AbstractSpringTest.class);

	protected MockMvc mvc;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private DataSource dataSource;

	@Before
	public void setup() throws DatabaseException {
		// MockMvc.
		mvc = MockMvcBuilders.webAppContextSetup(wac).build();

		// Use oracle to test.
		final DataSourceRouter ds = (DataSourceRouter)dataSource;
		ds.addNewConnection("test", "172.27.12.60", 1521, "ac2", "account", "account");
		ConnectionDeterminer.setId("test");

		logger.info("------------------------------ Start test! ------------------------------");
	}
}
