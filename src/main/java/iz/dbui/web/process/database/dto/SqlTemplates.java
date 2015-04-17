package iz.dbui.web.process.database.dto;

import iz.dbui.base.AppData;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author izumi_j
 *
 */
@XmlRootElement(name = "templates")
public final class SqlTemplates implements AppData {

	private List<SqlTemplate> list = new ArrayList<>();

	public List<SqlTemplate> getList() {
		return list;
	}

	public void setList(List<SqlTemplate> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "SqlTemplates [list=" + list + "]";
	}

}
