package iz.dbui.web.process.database.dao;

import iz.dbui.web.process.database.dto.ColumnInfo;
import iz.dbui.web.process.database.dto.ColumnInfo.DataType;
import iz.dbui.web.process.database.dto.ExecutionResult;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

@Repository
public class DatabaseInfoDaoOracle implements DatabaseInfoDao {
	private static final Logger logger = LoggerFactory.getLogger(DatabaseInfoDaoOracle.class);

	private static final String SEL_ALL_TABLES;
	static {
		SEL_ALL_TABLES = "SELECT * FROM ALL_TABLES ORDER BY OWNER, TABLE_NAME";
	}

	private static final String SEL_ALL_TAB_COLUMNS;
	static {
		SEL_ALL_TAB_COLUMNS = "SELECT A.*, B.COMMENTS FROM"
				+ " ALL_TAB_COLUMNS A, USER_COL_COMMENTS B"
				+ " WHERE A.TABLE_NAME = B.TABLE_NAME"
				+ " AND A.COLUMN_NAME = B.COLUMN_NAME"
				+ " AND A.TABLE_NAME = ?"
				+ " ORDER BY A.COLUMN_ID";
	}

	@Autowired
	private JdbcTemplate jdbc;

	@Override
	@Cacheable(value = "tableNames", key = "#connectionId")
	public List<String> findAllTableNames(String connectionId) {
		logger.trace("#findAllTableNames");
		return jdbc.query(SEL_ALL_TABLES, new RowMapper<String>() {

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("TABLE_NAME");
			}
		});
	}

	@Override
	public List<ColumnInfo> findColumnsBy(String connectionId, String tableName) {
		logger.trace("#findColumnsBy");
		return jdbc.query(SEL_ALL_TAB_COLUMNS, new RowMapper<ColumnInfo>() {

			@Override
			public ColumnInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
				final ColumnInfo c = new ColumnInfo();
				c.owner = rs.getString("OWNER");
				c.tableName = rs.getString("TABLE_NAME");
				c.columnName = rs.getString("COLUMN_NAME");
				c.dataType = DataType.STRING;// TODO
				c.columnId = rs.getLong("COLUMN_ID");
				c.comments = rs.getString("COMMENTS");
				return c;
			}
		}, tableName);
	}

	@Override
	public ExecutionResult executeQuery(String sqlSentence) {
		logger.trace("#executeQuery {}", sqlSentence);

		return jdbc.query(sqlSentence, new ResultSetExtractor<ExecutionResult>() {

			@Override
			public ExecutionResult extractData(ResultSet rs) throws SQLException, DataAccessException {
				final ExecutionResult result = new ExecutionResult();
				result.columns = new ArrayList<>();
				result.columnIds = new ArrayList<>();
				result.records = new ArrayList<>();

				final ResultSetMetaData rsmd = rs.getMetaData();
				final int columnCount = rsmd.getColumnCount();

				// Extract columns.
				for (int i = 1; i <= columnCount; i++) {
					final ColumnInfo c = new ColumnInfo();
					c.columnName = rsmd.getColumnName(i);
					switch (rsmd.getColumnType(i)) {
					case Types.CHAR:
					case Types.NCHAR:
					case Types.VARCHAR:
					case Types.NVARCHAR:
					case Types.LONGVARCHAR:
					case Types.LONGNVARCHAR:
						c.dataType = DataType.STRING;
						break;
					case Types.TINYINT:
					case Types.SMALLINT:
					case Types.INTEGER:
					case Types.BIGINT:
					case Types.NUMERIC:
					case Types.FLOAT:
					case Types.DOUBLE:
					case Types.DECIMAL:
						c.dataType = DataType.NUMBER;
						break;
					case Types.DATE:
					case Types.TIME:
					case Types.TIMESTAMP:
					case Types.TIMESTAMP_WITH_TIMEZONE:
						c.dataType = DataType.DATE;
						break;
					default:
						c.dataType = DataType.OTHER;
						break;
					}
					result.columns.add(c);
					result.columnIds.add(c.columnName);
				}

				// Extract values.
				while (rs.next()) {
					final List<String> values = new ArrayList<>();
					for (int i = 1; i <= columnCount; i++) {
						final Object value = JdbcUtils.getResultSetValue(rs, i);
						if (value == null) {
							values.add(StringUtils.EMPTY);
						} else if (value instanceof java.sql.Date) {
							final DateTime dt = new DateTime(((java.sql.Date) value).getTime());
							values.add(dt.toString(DateTimeFormat.mediumDate()));
						} else if (value instanceof java.sql.Timestamp) {
							final DateTime dt = new DateTime(((java.sql.Timestamp) value).getTime());
							// Omit when time is 00:00:00.
							if (dt.equals(dt.withTime(0, 0, 0, 0))) {
								values.add(dt.toString(DateTimeFormat.mediumDate()));
							} else {
								values.add(dt.toString(DateTimeFormat.mediumDateTime()));
							}
						} else {
							values.add(value.toString());
						}
					}
					result.records.add(values);
				}

				return result;
			}

		});
	}

	@Override
	public ExecutionResult executeUpdate(String sqlSentence) {
		logger.trace("#executeUpdate {}", sqlSentence);
		final ExecutionResult result = new ExecutionResult();
		result.query = false;
		result.updatedCount = jdbc.update(sqlSentence);
		return result;
	}

	@Override
	public ExecutionResult execute(String sqlSentence) {
		logger.trace("#execute {}", sqlSentence);
		final ExecutionResult result = new ExecutionResult();
		result.query = false;
		jdbc.execute(sqlSentence);
		return result;
	}

}
