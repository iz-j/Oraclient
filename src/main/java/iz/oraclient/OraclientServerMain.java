package iz.oraclient;

import iz.oraclient.web.ViaJetty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author izumi_j
 *
 */
public final class OraclientServerMain {
	private static final Logger logger = LoggerFactory.getLogger(OraclientServerMain.class);

	private OraclientServerMain() {
	}

	/**
	 * Call this to launch your project.
	 *
	 * @param args
	 */
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
