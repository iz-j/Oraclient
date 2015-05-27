package iz.dbui.web.process.connection.dto;

/**
 *
 * @author iz_j
 *
 */
public final class Connection {

	public String id;

	public String name;
	public String host;
	public int port;
	public String sid;
	public String username;
	public String password;

	@Override
	public String toString() {
		return "Connection [id="
				+ id
				+ ", name="
				+ name
				+ ", host="
				+ host
				+ ", port="
				+ port
				+ ", sid="
				+ sid
				+ ", username="
				+ username
				+ ", password="
				+ password
				+ "]";
	}
}
