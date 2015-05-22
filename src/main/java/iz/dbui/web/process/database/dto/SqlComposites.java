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
public final class SqlComposites implements AppData {

	private HashMap<String, SqlComposite> composites = new HashMap<>();

	public HashMap<String, SqlComposite> getComposites() {
		return composites;
	}

	public void setComposites(HashMap<String, SqlComposite> composites) {
		this.composites = composites;
	}

	@Override
	public String toString() {
		return "SqlComposites [composites=" + composites + "]";
	}

}
