package iz.oraclient.web.process.database;

import iz.oraclient.web.process.database.dto.SqlTemplate;

import java.util.List;

/**
 *
 * @author izumi_j
 *
 */
public interface SqlService {

	List<SqlTemplate> getMatchedTemplates(String term);

}
