package net.realmoftowny.townywars.objects;

public class MySQLConnectionInfo{
	
	private String host = "127.0.0.1";
	private Integer port = 3306;
	private String database = "TownyWars";
	private String table = "minecraft";
	private String username = "root";
	private String password = "example";
	private Boolean useSSL = false;
	
	
	public MySQLConnectionInfo(String host, Integer port, String database, String table, String username, String password, Boolean useSSL) {
		this.host = host;
		this.port = port;
		this.database = database;
		this.table = table;
		this.username = username;
		this.password = password;
		this.useSSL = useSSL;
	}
	
	public String getHost() {
		return this.host;
	}
	
	public Integer getPort() {
		return this.port;
	}
	
	public String getDatabase() {
		return this.database;
	}
	
	public String getTable() {
		return this.table;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public Boolean isUseSSL() {
		return this.useSSL;
	}
}