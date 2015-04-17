package iz.dbui.web.process.connection.dto;

import iz.dbui.base.AppData;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author izumi_j
 *
 */
@XmlRootElement(name = "connections")
public final class Connections implements AppData {

	private List<Connection> list = new ArrayList<>();

	public List<Connection> getList() {
		return list;
	}

	public void setList(List<Connection> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "Connections [list=" + list + "]";
	}

}
