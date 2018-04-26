package net.realmoftowny.townywars.managers;

import net.realmoftowny.townywars.TownyWars;
import net.realmoftowny.townywars.config.TownyWarsConfig;
import net.realmoftowny.townywars.objects.MySQLConnectionInfo;
import net.realmoftowny.townywars.objects.StorageMethod;

public class ConfigManager 
{
	
	private TownyWarsConfig config;
	private MySQLConnectionInfo connectionInfo;
	
	public ConfigManager() {
		this.config = new TownyWarsConfig(TownyWars.getInstance());
		this.connectionInfo = config.connectionInfo;
	}
	
	public String getPluginPrefix() {
		return this.config.prefix;
	}
	
	public StorageMethod getStorageMethod() {
		return this.config.method;
	}
	
	public int getSaveInterval() {
		return this.config.interval;
	}
	
	public String getHost() {
		if(this.connectionInfo.getHost()!=null) {
			return connectionInfo.getHost();
		}else {
			return "127.0.0.1";
		}	
	}
	
	public int getPort() {
		if(connectionInfo.getPort()!=null) {
			return connectionInfo.getPort();
		}else{
			return 3306;
		}		
	}
	
	public String getDatabase() {
		if(this.connectionInfo.getDatabase()!=null) {
			return this.connectionInfo.getDatabase();
		}else {
			return "TownyWars";
		}
	}
	
	public String getTable() {
		if(this.connectionInfo.getTable()!=null) {
			return this.connectionInfo.getTable();
		}else {
			return "minecraft";
		}
	}
	
	public String getUsername() {
		if(this.connectionInfo.getUsername()!=null) {
			return this.connectionInfo.getUsername();
		}else {
			return "root";
		}
	}
	
	public String getPassword() {
		if(this.connectionInfo.getPassword()!=null) {
			return this.connectionInfo.getPassword();
		}else {
			return "example";
		}
	}
	
	public boolean isUseSSL() {
		if(this.connectionInfo.isUseSSL()!=null) {
			return this.connectionInfo.isUseSSL();
		}else {
			return false;
		}
	}
}