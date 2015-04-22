package iz.dbui.web.process.database.dto;

import iz.dbui.base.AppData;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author izumi_j
 *
 */
@XmlRootElement
public final class SqlTemplates implements AppData {

	private HashMap<String, SqlTemplate> templates = new HashMap<>();

	public HashMap<String, SqlTemplate> getTemplates() {
		return templates;
	}

	public void setTemplates(HashMap<String, SqlTemplate> templates) {
		this.templates = templates;
	}

	@Override
	public String toString() {
		return "SqlTemplates [templates=" + templates + "]";
	}

}
