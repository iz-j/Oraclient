package iz.dbui;

import iz.dbui.web.ViaJetty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Launch server directly.
 *
 * @author izumi_j
 *
 */
public final class DbUiServerMain {
	private static final Logger logger = LoggerFactory.getLogger(DbUiServerMain.class);

	private DbUiServerMain() {
	}

	public static void main(String[] args) {
		logger.info("Start via Jetty!");
		final ViaJetty jetty = new ViaJetty(8888);
		try {
			jetty.start();
			jetty.join();
		} catch (Throwable e) {
			throw e;
		} finally {
			jetty.stop();
		}
	}
}
