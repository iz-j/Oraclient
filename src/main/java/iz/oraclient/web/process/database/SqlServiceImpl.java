package iz.oraclient.web.process.database;

import iz.oraclient.web.process.database.dao.DatabaseInfoDao;
import iz.oraclient.web.process.database.dto.SqlTemplate;
import iz.oraclient.web.process.database.dto.SqlTemplate.TemplateType;
import iz.oraclient.web.spring.jdbc.ConnectionDeterminer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SqlServiceImpl implements SqlService {
	private static final Logger logger = LoggerFactory.getLogger(SqlServiceImpl.class);

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
			final SqlTemplate t = new SqlTemplate();
			t.id = UUID.randomUUID().toString();
			t.type = TemplateType.TABLE;
			t.name = StringUtils.upperCase(tableName);
			t.sentence = "SELECT * FROM " + StringUtils.upperCase(tableName);
			return t;
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

}
