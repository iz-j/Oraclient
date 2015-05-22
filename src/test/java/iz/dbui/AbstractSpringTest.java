package iz.dbui;

import iz.dbui.web.process.connection.ConnectionService;
import iz.dbui.web.process.connection.dto.Connection;
import iz.dbui.web.spring.AppConfig;
import iz.dbui.web.spring.WebMvcConfig;
import iz.dbui.web.spring.jdbc.ConnectionContext;
import iz.dbui.web.spring.jdbc.DataSourceRouter;
import iz.dbui.web.spring.jdbc.DatabaseException;

import javax.sql.DataSource;

import org.junit.After;
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
	private ConnectionService connectionService;
	@Autowired
	private DataSource dataSource;

	private final Connection connection = new Connection();

	@Before
	public void setup() throws DatabaseException {
		// MockMvc.
		mvc = MockMvcBuilders.webAppContextSetup(wac).build();

		// Use oracle to test.
		connection.host = "localhost";
		connection.port = 1521;
		connection.sid = "xe";
		connection.username = "test";
		connection.password = "test";
		connectionService.add(connection);

		final DataSourceRouter ds = (DataSourceRouter) dataSource;
		ds.addNewConnection(connection.id, connection.host, connection.port, connection.sid, connection.username,
				connection.password);
		ConnectionContext.setId(connection.id);

		logger.info("------------------------------ Start test! ------------------------------");
	}

	@After
	public void tearDown() {
		connectionService.remove(connection.id);
	}
}
