package iz.oraclient.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import iz.oraclient.AbstractSpringTest;
import iz.oraclient.base.Jacksons;
import iz.oraclient.web.process.connection.dto.Connection;

import org.junit.Test;
import org.springframework.http.MediaType;

/**
 *
 * @author izumi_j
 *
 */
public class ConnectionsControllerTest extends AbstractSpringTest {

	@Test
	public void test_addNewConnection() throws Exception {
		final Connection post = new Connection();
		post.name = "Hoge";
		post.port = 1521;

		mvc.perform(post("/connections/new").contentType(MediaType.APPLICATION_JSON).content(Jacksons.toJson(post)))
		.andExpect(status().isOk());
	}
}
