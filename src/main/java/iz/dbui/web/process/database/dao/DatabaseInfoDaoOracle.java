package iz.dbui.web.process.database.dao;

import iz.dbui.web.process.ProcessConstants;
import iz.dbui.web.process.database.dto.Column;
import iz.dbui.web.process.database.dto.Column.DataType;
import iz.dbui.web.process.database.dto.definition.ColumnInfo;
import iz.dbui.web.process.database.dto.definition.TableInfo;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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

	private static final String SEL_USR_TABLES;
	static {
		SEL_USR_TABLES = "SELECT * FROM USER_TABLES ORDER BY TABLE_NAME";
	}

	private static final String SEL_USR_TABLE;
	static {
		SEL_USR_TABLE = "SELECT A.*, B.COMMENTS FROM"
				+ " USER_TABLES A, USER_TAB_COMMENTS B"
				+ " WHERE A.TABLE_NAME = B.TABLE_NAME"
				+ " AND A.TABLE_NAME = ?";
	}

	private static final String SEL_USR_TAB_COLUMNS;
	static {
		SEL_USR_TAB_COLUMNS = "SELECT A.*, B.COMMENTS FROM"
				+ " USER_TAB_COLUMNS A, USER_COL_COMMENTS B"
				+ " WHERE A.TABLE_NAME = B.TABLE_NAME"
				+ " AND A.COLUMN_NAME = B.COLUMN_NAME"
				+ " AND A.TABLE_NAME = ?"
				+ " ORDER BY A.COLUMN_ID";
	}

	private static final String SEL_PRIMARY_KEYS;
	static {
		SEL_PRIMARY_KEYS = "SELECT T.COLUMN_NAME FROM USER_CONS_COLUMNS T"
				+ " WHERE T.TABLE_NAME = ?"
				+ " AND T.CONSTRAINT_NAME IN ("
				+ " SELECT CONSTRAINT_NAME FROM USER_CONSTRAINTS"
				+ " WHERE TABLE_NAME = T.TABLE_NAME"
				+ " AND CONSTRAINT_TYPE = 'P')";
	}

	@Autowired
	private JdbcTemplate jdbc;

	@Override
	@Cacheable(value = ProcessConstants.CACHE_DATABASE, key = "#connectionId")
	public List<String> findAllTableNames(String connectionId) {
		logger.trace("#findAllTableNames @Cacheable");
		return jdbc.query(SEL_USR_TABLES, new RowMapper<String>() {

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("TABLE_NAME");
			}
		});
	}

	@Override
	public TableInfo findTableBy(String tableName) {
		logger.trace("#findTableBy");
		return jdbc.queryForObject(SEL_USR_TABLE, new RowMapper<TableInfo>() {

			@Override
			public TableInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
				final TableInfo t = new TableInfo();
				t.tableName = rs.getString("TABLE_NAME");
				t.comments = rs.getString("COMMENTS");
				t.description = makeTableDescription(rs);
				return t;
			}
		}, tableName);
	}

	private String makeTableDescription(ResultSet rsTblInfo) throws SQLException {
		return "TABLE SPACE is " + rsTblInfo.getString("TABLESPACE_NAME");
	}

	@Override
	@Cacheable(value = ProcessConstants.CACHE_DATABASE, key = "#connectionId.concat(':cols:').concat(#tableName)")
	public List<ColumnInfo> findColumnsBy(String connectionId, String tableName) {
		logger.trace("#findColumnsBy @Cacheable");
		return findColumnsBy(tableName);
	}

	@Override
	public List<ColumnInfo> findColumnsBy(String tableName) {
		logger.trace("#findColumnsBy");
		return jdbc.query(SEL_USR_TAB_COLUMNS, new RowMapper<ColumnInfo>() {

			@Override
			public ColumnInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
				final ColumnInfo c = new ColumnInfo();
				c.description = makeColumnDescription(rs);

				c.tableName = rs.getString("TABLE_NAME");
				c.columnName = rs.getString("COLUMN_NAME");
				c.dataType = toDataType(rs.getString("DATA_TYPE"));
				c.comments = rs.getString("COMMENTS");

				return c;
			}
		}, StringUtils.upperCase(tableName));
	}

	@Override
	public List<String> findPrimaryKeysBy(String tableName) {
		logger.trace("#findPrimaryKeysBy");
		return jdbc.query(SEL_PRIMARY_KEYS, new RowMapper<String>() {

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		}, StringUtils.upperCase(tableName));
	}

	private DataType toDataType(String oracleDataType) {
		switch (oracleDataType) {
		case "VARCHAR2":
		case "NVARCHAR2":
		case "CHAR":
		case "NCHAR":
			return DataType.STRING;
		case "NUMBER":
		case "BINARY_FLOAT":
		case "BINARY_DOUBLE":
			return DataType.NUMBER;
		case "DATE":
		case "TIMESTAMP":
		case "TIMESTAMP WITH TIMEZONE":
		case "TIMESTAMP WITH LOCAL TIMEZONE":
			return DataType.DATE;
		default:
			logger.debug("Unsupported type -> {}", oracleDataType);
			return DataType.OTHER;
		}
	}

	private String makeColumnDescription(ResultSet rsColInfo) throws SQLException {
		final StringBuilder description = new StringBuilder();

		final String dataDefault = rsColInfo.getString("DATA_DEFAULT");// Read at first to avoid oracle bug spec!

		final String oracleDataType = rsColInfo.getString("DATA_TYPE");
		description.append(oracleDataType);
		switch (toDataType(oracleDataType)) {
		case STRING:
			description.append("(").append(rsColInfo.getInt("DATA_LENGTH")).append(")");
			break;
		case NUMBER:
			description.append("(").append(rsColInfo.getInt("DATA_PRECISION")).append(",")
			.append(rsColInfo.getInt("DATA_SCALE")).append(")");
			break;
		case DATE:
		case OTHER:
		default:
			break;
		}

		if ("N".equals(rsColInfo.getString("NULLABLE"))) {
			description.append(" NOT NULL");
		}

		if (StringUtils.isNotEmpty(dataDefault)) {
			description.append(" DEFAULT ").append(dataDefault);
		}

		return description.toString();
	}

	@Override
	public Pair<List<Column>, List<List<String>>> executeQuery(String sqlSentence) {
		logger.trace("#executeQuery {}", sqlSentence);

		return jdbc.query(sqlSentence, new ResultSetExtractor<Pair<List<Column>, List<List<String>>>>() {

			@Override
			public Pair<List<Column>, List<List<String>>> extractData(ResultSet rs) throws SQLException,
			DataAccessException {
				final List<Column> columns = new ArrayList<>();
				final List<List<String>> records = new ArrayList<>();

				final ResultSetMetaData rsmd = rs.getMetaData();
				final int columnCount = rsmd.getColumnCount();

				// Extract columns.
				for (int i = 1; i <= columnCount; i++) {
					final Column c = new Column();
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
					columns.add(c);
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
					records.add(values);
				}

				return new ImmutablePair<List<Column>, List<List<String>>>(columns, records);
			}

		});
	}

	@Override
	public int executeUpdate(String sqlSentence) {
		logger.trace("#executeUpdate {}", sqlSentence);
		return jdbc.update(sqlSentence);
	}

	@Override
	public void execute(String sqlSentence) {
		logger.trace("#execute {}", sqlSentence);
		jdbc.execute(sqlSentence);
	}

}
