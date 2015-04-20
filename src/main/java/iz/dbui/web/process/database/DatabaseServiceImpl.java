package iz.dbui.web.process.database;

import iz.dbui.web.process.database.dao.DatabaseInfoDao;
import iz.dbui.web.process.database.dto.ExecutionResult;
import iz.dbui.web.process.database.dto.LocalChanges;
import iz.dbui.web.process.database.dto.SqlTemplate;
import iz.dbui.web.process.database.dto.SqlTemplate.TemplateType;
import iz.dbui.web.process.database.helper.MergeSqlBuilder;
import iz.dbui.web.process.database.helper.RowidInjector;
import iz.dbui.web.process.database.helper.SqlAnalyzer;
import iz.dbui.web.process.database.helper.SqlAnalyzer.AnalysisResult;
import iz.dbui.web.spring.jdbc.ConnectionDeterminer;
import iz.dbui.web.spring.jdbc.DatabaseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseServiceImpl implements DatabaseService {
	private static final Logger logger = LoggerFactory.getLogger(DatabaseServiceImpl.class);

	@Autowired
	private DatabaseInfoDao dbDao;

	@Override
	public List<SqlTemplate> getMatchedTemplates(String term) {
		if (StringUtils.isEmpty(term)) {
			return Collections.emptyList();
		}

		List<SqlTemplate> templates = new ArrayList<>();

		// Retrieve table names.
		final List<String> tableNames = dbDao.findAllTableNames(ConnectionDeterminer.getCurrentId());
		logger.trace("{} tables found.", tableNames.size());
		templates.addAll(tableNames.stream().map(tableName -> {
			return SqlTemplate.forTableSql(tableName);
		}).collect(Collectors.toList()));

		// Retrieve custom registered.

		// Filter and sort.
		templates = templates.stream().filter(t -> {
			return StringUtils.containsIgnoreCase(t.name, term);
		}).sorted((t1, t2) -> {
			return ObjectUtils.compare(t1.name, t2.name);
		}).limit(20).collect(Collectors.toList());

		logger.trace("{} templates found.", templates.size());
		return templates;
	}

	@Override
	public ExecutionResult executeSql(SqlTemplate sql) throws DatabaseException {
		final AnalysisResult analysisResult = SqlAnalyzer.analyze(sql.sentence);

		try {
			switch (analysisResult) {
			case QUERY:
				if (sql.type == TemplateType.TABLE) {
					final ExecutionResult retval = dbDao.executeQuery(RowidInjector.inject(sql.sentence));
					retval.hasRowid = true;
					retval.tableName = sql.tableName;
					return retval;
				} else {
					return dbDao.executeQuery(sql.sentence);
				}
			case EXECUTABLE:
				return dbDao.executeUpdate(sql.sentence);
			case OTHER:
				return dbDao.execute(sql.sentence);
			default:
				throw new IllegalStateException("Should not reach here!");
			}
		} catch (DataAccessException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	@Override
	public void save(LocalChanges changes) throws DatabaseException {
		final MutableObject<String> rowidHolder = new MutableObject<>();
		try {
			final String tableName = changes.tableName;
			final List<String> columnNames = changes.columnNames;

			// Handle insert and update.
			changes.editedMap.forEach((rowid, values) -> {
				rowidHolder.setValue(rowid);
				String sql;
				if (StringUtils.startsWith(rowid, "new$")) {
					sql = MergeSqlBuilder.insert(values, tableName, columnNames);
				} else {
					sql = MergeSqlBuilder.update(rowid, values, tableName, columnNames);
				}
				dbDao.executeUpdate(sql);
			});

			// Handle delete.
			changes.removedRowids.forEach(rowid -> {
				rowidHolder.setValue(rowid);
				dbDao.executeUpdate(MergeSqlBuilder.delete(rowid, tableName));
			});
		} catch (DataAccessException e) {
			final DatabaseException ex = new DatabaseException(e.getMessage(), e);
			ex.setRowid(rowidHolder.getValue());
			throw ex;
		}
	}
}
