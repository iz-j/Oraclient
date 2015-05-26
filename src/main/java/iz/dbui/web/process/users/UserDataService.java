package iz.dbui.web.process.users;

import iz.dbui.web.process.database.dto.SqlComposite;
import iz.dbui.web.process.database.dto.SqlTemplate;

import java.util.List;

/**
 *
 * @author izumi_j
 *
 */
public interface UserDataService {

	List<SqlTemplate> getSqlTemplates();

	void removeSqlTemplate(String id);

	void save(SqlTemplate sql);

	List<SqlComposite> getSqlComposites();

	SqlComposite getSqlComposite(String id);

	void save(SqlComposite composite);

	void removeSqlComposite(String id);
}
