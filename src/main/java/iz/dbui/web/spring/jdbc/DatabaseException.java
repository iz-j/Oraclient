package iz.dbui.web.spring.jdbc;

import java.sql.SQLException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataAccessException;

/**
 * All exceptions related with the database.
 *
 * @author izumi_j
 *
 */
@SuppressWarnings("serial")
public class DatabaseException extends Exception {

	private SQLException sqlException;
	private String rowid;

	/**
	 * For {@link SQLException}.
	 *
	 * @param msg
	 * @param e
	 */
	public DatabaseException(String msg, SQLException e) {
		super(msg, e);
		this.sqlException = e;
	}

	/**
	 * For {@link DataAccessException}.
	 *
	 * @param msg
	 * @param e
	 */
	public DatabaseException(String msg, DataAccessException e) {
		super(msg, e);
		Throwable cause = e.getCause();
		while (cause != null) {
			if (cause instanceof SQLException) {
				sqlException = (SQLException) cause;
				break;
			}
			cause = e.getCause();
		}
	}

	/**
	 * @return stack trace
	 */
	public String getStackTraceAsString() {
		return ExceptionUtils.getStackTrace(this.getCause());
	}

	public String getSqlExceptionMessage() {
		return sqlException != null ? sqlException.getMessage() : super.getMessage();
	}

	public String getRowid() {
		return rowid;
	}

	public void setRowid(String rowid) {
		this.rowid = rowid;
	}
}
