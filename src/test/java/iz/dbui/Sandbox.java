package iz.dbui;

import iz.dbui.base.AppDataManager;
import iz.dbui.web.process.connection.dto.Connection;
import iz.dbui.web.process.connection.dto.Connections;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Sandbox {
	private static final Logger logger = LoggerFactory.getLogger(Sandbox.class);

	@Test
	public void test() {
		final Connection connection = new Connection();
		connection.name = "hoge";

		Connections connections = new Connections();
		connections.getList().add(connection);
		logger.debug(connections.toString());
		AppDataManager.save(connections);
		connections = AppDataManager.load(Connections.class);
		logger.debug(connections.toString());
	}

	@Test
	public void deleteConnections() {
		// AppDataManager.delete(Connections.class);
	}

	@Test
	public void pairToJson() throws JsonProcessingException {
		final Pair<String, String> pair = new ImmutablePair<>("1", "2");
		final ObjectMapper mapper = new ObjectMapper();
		logger.debug(mapper.writeValueAsString(pair));
	}

}
