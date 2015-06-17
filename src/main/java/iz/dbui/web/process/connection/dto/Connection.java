package iz.dbui.web.process.connection.dto;

import iz.dbui.web.spring.jdbc.JdbcConnectionInfo;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author iz_j
 *
 */
public final class Connection implements JdbcConnectionInfo {

	public String id;

	public String name;
	public String host;
	public int port;
	public String sid;
	public String username;
	public String password;

	public String freeFormat;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getOracleUrl() {
		if (StringUtils.isEmpty(freeFormat)) {
			return "jdbc:oracle:thin:@" + host + ":" + port + ":" + sid;
		} else {
			return freeFormat;
		}
	}

	@Override
	public String toString() {
		return "Connection [id=" + id + ", name=" + name + ", host=" + host + ", port=" + port + ", sid=" + sid
				+ ", username=" + username + ", password=" + password + ", freeFormat=" + freeFormat + "]";
	}

}
