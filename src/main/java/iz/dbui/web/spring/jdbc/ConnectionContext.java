package iz.dbui.web.spring.jdbc;

import iz.dbui.web.process.connection.ConnectionService;
import iz.dbui.web.process.connection.dto.Connection;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Supports to switch the connection that will be used per thread.
 *
 * @author iz_j
 *
 */
@Component
public final class ConnectionContext implements ApplicationContextAware {
	private static ThreadLocal<Connection> holder = new InheritableThreadLocal<Connection>() {
		@Override
		protected Connection initialValue() {
			return null;
		}
	};

	private static BeanFactory beanFactory;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (BeanFactory.class.isAssignableFrom(applicationContext.getClass())) {
			beanFactory = applicationContext;
		} else {
			throw new IllegalStateException("Why applicationContext is'nt BeanFactory???");
		}
	}

	/**
	 * Set the connection.
	 *
	 * @param id
	 */
	public static void setId(String id) {
		final ConnectionService connectionService = beanFactory.getBean(ConnectionService.class);
		holder.set(connectionService.get(id));
	}

	/**
	 * Reset.
	 */
	public static void reset() {
		holder.remove();
	}

	/**
	 * Returns id of connection for this thread.
	 *
	 * @return id
	 */
	public static String getCurrentId() {
		return holder.get().id;
	}

	/**
	 * Returns user name.
	 * @return name
	 */
	public static String getUserName() {
		return holder.get().username;
	}

	private ConnectionContext() {
	}

}
