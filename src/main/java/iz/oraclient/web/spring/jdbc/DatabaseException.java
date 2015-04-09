package iz.oraclient.web.spring.jdbc;

import java.sql.SQLException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataAccessException;

/**
 * All exceptions related with the database.
 *
 * @author izumi_j
 *
 */
public class DatabaseException extends Exception {

	/**
	 * For {@link SQLException}.
	 *
	 * @param msg
	 * @param e
	 */
	public DatabaseException(String msg, SQLException e) {
		super(msg, e);
	}

	/**
	 * For {@link DataAccessException}.
	 *
	 * @param msg
	 * @param e
	 */
	public DatabaseException(String msg, DataAccessException e) {
		super(msg, e);
	}

	/**
	 * @return stack trace
	 */
	public String getStackTraceAsString() {
		return ExceptionUtils.getStackTrace(this.getCause());
	}
}
