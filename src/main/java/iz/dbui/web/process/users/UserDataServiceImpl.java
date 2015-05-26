package iz.dbui.web.process.users;

import iz.dbui.base.AppDataManager;
import iz.dbui.web.process.database.dto.SqlComposite;
import iz.dbui.web.process.database.dto.SqlComposites;
import iz.dbui.web.process.database.dto.SqlTemplate;
import iz.dbui.web.process.database.dto.SqlTemplates;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserDataServiceImpl implements UserDataService {
	private static final Logger logger = LoggerFactory.getLogger(UserDataServiceImpl.class);

	@Override
	public List<SqlTemplate> getSqlTemplates() {
		final SqlTemplates templates = AppDataManager.load(SqlTemplates.class);
		return templates.getTemplates().values().stream().sorted(SqlTemplate.COMPARATOR).collect(Collectors.toList());
	}

	@Override
	public void removeSqlTemplate(String id) {
		final SqlTemplates templates = AppDataManager.load(SqlTemplates.class);
		templates.getTemplates().remove(id);
		AppDataManager.save(templates);
	}

	@Override
	public void save(SqlTemplate sql) {
		final SqlTemplates all = AppDataManager.load(SqlTemplates.class);
		if (all.getTemplates().containsKey(sql.id)) {
			logger.debug("Overwrite SqlTemplate. id = {}", sql.id);
		}
		all.getTemplates().put(sql.id, sql);
		AppDataManager.save(all);
		logger.trace("SqlTemplate saved. total = {}", all.getTemplates().size());
	}

	@Override
	public List<SqlComposite> getSqlComposites() {
		return AppDataManager.load(SqlComposites.class).getComposites().values().stream().sorted((c1, c2) -> {
			return ObjectUtils.compare(c1.name, c2.name);
		}).collect(Collectors.toList());
	}

	@Override
	public SqlComposite getSqlComposite(String id) {
		return AppDataManager.load(SqlComposites.class).getComposites().get(id);
	}

	@Override
	public void save(SqlComposite composite) {
		if (StringUtils.isEmpty(composite.id)) {
			composite.id = UUID.randomUUID().toString();
		}

		final SqlComposites all = AppDataManager.load(SqlComposites.class);
		if (all.getComposites().containsKey(composite.id)) {
			logger.debug("Overwrite SqlComposite. id = {}", composite.id);
		}
		all.getComposites().put(composite.id, composite);
		AppDataManager.save(all);
		logger.trace("SqlTemplate saved. total = {}", all.getComposites().size());
	}

	@Override
	public void removeSqlComposite(String id) {
		final SqlComposites composites = AppDataManager.load(SqlComposites.class);
		composites.getComposites().remove(id);
		AppDataManager.save(composites);
	}

}
