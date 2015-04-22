package iz.dbui.web.spring.jdbc;

import org.springframework.stereotype.Component;

/**
 * Supports to switch the connection that will be used per thread.
 *
 * @author izumi_j
 *
 */
@Component
public final class ConnectionContext {
	private static ThreadLocal<String> idHolder = new InheritableThreadLocal<String>() {
		@Override
		protected String initialValue() {
			return null;
		}
	};

	/**
	 * Set identifier of the connection.
	 *
	 * @param id
	 */
	public static void setId(String id) {
		idHolder.set(id);
	}

	/**
	 * Reset id.
	 */
	public static void reset() {
		idHolder.remove();
	}

	/**
	 * Returns current id of this thread.
	 *
	 * @return id
	 */
	public static String getCurrentId() {
		return idHolder.get();
	}

	private ConnectionContext() {
	}
}
