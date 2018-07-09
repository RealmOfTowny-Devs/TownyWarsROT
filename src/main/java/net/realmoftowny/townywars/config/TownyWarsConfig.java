package net.realmoftowny.townywars.config;

import net.realmoftowny.townywars.TownyWars;
import net.realmoftowny.townywars.objects.MySQLConnectionInfo;
import net.realmoftowny.townywars.objects.StorageMethod;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class TownyWarsConfig
{
	private File configFile;
	private FileConfiguration config;
	private TownyWars plugin;
	
	public String prefix = "&7[&eTowny&cWars&7]";
	public StorageMethod method;
	public MySQLConnectionInfo connectionInfo;	
	public int interval = 10;

	
	public TownyWarsConfig(TownyWars plugin) {
		this.plugin = plugin;
		init();
		load();
	}
	
	private void load() {
		this.prefix = config.getString("Prefix");
		if(StorageMethod.valueOf(config.getString("StorageMethod").toUpperCase())!=null) {
			this.method = StorageMethod.valueOf(config.getString("Storage.StorageMethod").toUpperCase());
		}else {
			this.method = StorageMethod.FILE;
		}
		if (this.method.equals(StorageMethod.MYSQL)) {
			connectionInfo = new MySQLConnectionInfo(this.config.getString("Storage.mysql.host"), Integer.valueOf(this.config.getString("Storage.mysql.port")), this.config.getString("Storage.mysql.database"), this.config.getString("Storage.mysql.table"), this.config.getString("Storage.mysql.username"), this.config.getString("Storage.mysql.password"), Boolean.valueOf(this.config.getString("Storage.mysql.useSSL").toUpperCase()));
		} else {
			this.interval = this.config.getInt("Storage.file.save-interval");
		}
		
	}
	
	private void init() {
		if (configFile == null) {
			configFile = new File(plugin.getDataFolder(), "config.yml");
	    }
		if (!configFile.exists()) {           
	    	plugin.saveResource("config.yml", false);
	    }
		config = YamlConfiguration.loadConfiguration(configFile);
	}
}