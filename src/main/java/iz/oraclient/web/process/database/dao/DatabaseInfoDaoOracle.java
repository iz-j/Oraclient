package iz.oraclient.web.process.database.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = "databaseInfo")
public class DatabaseInfoDaoOracle implements DatabaseInfoDao {
	private static final Logger logger = LoggerFactory.getLogger(DatabaseInfoDaoOracle.class);

	private static final String SEL_ALL_TABLES = "SELECT * FROM ALL_TABLES ORDER BY OWNER, TABLE_NAME";

	@Autowired
	private JdbcTemplate jdbc;

	@Override
	@Cacheable(key = "#connectionId")
	public List<String> findAllTableNames(String connectionId) {
		logger.trace("#findAllTableNames");
		return jdbc.query(SEL_ALL_TABLES, new RowMapper<String>() {

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("TABLE_NAME");
			}
		});
	}

}
