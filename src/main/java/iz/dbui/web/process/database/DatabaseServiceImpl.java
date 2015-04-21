package iz.dbui.web.process.database;

import iz.dbui.web.process.database.dao.DatabaseInfoDao;
import iz.dbui.web.process.database.dto.ColumnInfo;
import iz.dbui.web.process.database.dto.ExecutionResult;
import iz.dbui.web.process.database.dto.LocalChanges;
import iz.dbui.web.process.database.dto.SqlTemplate;
import iz.dbui.web.process.database.dto.SqlTemplate.TemplateType;
import iz.dbui.web.process.database.helper.ColumnInfoHelper;
import iz.dbui.web.process.database.helper.MergeSqlBuilder;
import iz.dbui.web.process.database.helper.RowidHelper;
import iz.dbui.web.process.database.helper.SqlAnalyzer;
import iz.dbui.web.process.database.helper.SqlAnalyzer.AnalysisResult;
import iz.dbui.web.spring.jdbc.ConnectionDeterminer;
import iz.dbui.web.spring.jdbc.DatabaseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseServiceImpl implements DatabaseService {
	private static final Logger logger = LoggerFactory.getLogger(DatabaseServiceImpl.class);

	private static final String NEW_REC_ROWID_PREFIX = "new$";

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
		logger.trace("SQL analized -> {}", analysisResult);

		final ExecutionResult result = new ExecutionResult();

		try {
			switch (analysisResult) {
			case QUERY:
				final Pair<List<ColumnInfo>, List<List<String>>> queryResult = dbDao.executeQuery(sql.sentence);
				result.query = true;
				result.columns = queryResult.getLeft();
				result.records = queryResult.getRight();

				// Determine editable or not.
				final List<String> pks = dbDao.findPrimaryKeysBy(ConnectionDeterminer.getCurrentId(), sql.tableName);
				if (sql.type == TemplateType.TABLE) {
					result.tableName = sql.tableName;
					result.editable = ColumnInfoHelper.containsAll(result.columns, pks);
					logger.trace("editable = {}", result.editable);
				}

				// Arrange for client.
				result.columnNames = ColumnInfoHelper.toColumnNames(result.columns);
				RowidHelper.allocateRowid(result.records, result.columns, pks);
				break;
			case EXECUTABLE:
				result.updatedCount = dbDao.executeUpdate(sql.sentence);
				break;
			case OTHER:
				dbDao.execute(sql.sentence);
				break;
			default:
				throw new IllegalStateException("Should not reach here!");
			}
		} catch (DataAccessException e) {
			throw new DatabaseException(e.getMessage(), e);
		}

		return result;
	}

	@Override
	public Map<String, String> save(LocalChanges changes) throws DatabaseException {
		final Map<String, String> retval = new HashMap<>();

		final MutableObject<String> rowidHolder = new MutableObject<>();
		try {
			final String tableName = changes.tableName;
			final List<ColumnInfo> columns = changes.columns;

			final List<String> pks = dbDao.findPrimaryKeysBy(ConnectionDeterminer.getCurrentId(), changes.tableName);

			// Handle insert and update.
			changes.editedMap.forEach((rowid, values) -> {
				rowidHolder.setValue(rowid);
				String sql;
				if (StringUtils.startsWith(rowid, NEW_REC_ROWID_PREFIX)) {
					sql = MergeSqlBuilder.insert(values, tableName, columns);
				} else {
					final List<String> keys = RowidHelper.rowidToPrimaryKeys(rowid);
					sql = MergeSqlBuilder.update(keys, values, tableName, columns, pks);
				}
				dbDao.executeUpdate(sql);

				// Set new rowid for client.
					retval.put(rowid, RowidHelper.createRowid(values, columns, pks, rowid));
				});

			// Handle delete.
			changes.removedRowids.forEach(rowid -> {
				rowidHolder.setValue(rowid);
				final List<String> keys = RowidHelper.rowidToPrimaryKeys(rowid);
				dbDao.executeUpdate(MergeSqlBuilder.delete(keys, tableName, columns, pks));
			});

			logger.trace(retval.toString());
			return retval;

		} catch (DataAccessException e) {
			final DatabaseException ex = new DatabaseException(e.getMessage(), e);
			ex.setRowid(rowidHolder.getValue());
			throw ex;
		}
	}
}
