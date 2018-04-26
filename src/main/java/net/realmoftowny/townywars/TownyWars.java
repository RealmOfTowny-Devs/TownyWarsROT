package net.realmoftowny.townywars;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.realmoftowny.townywars.managers.ConfigManager;
import net.realmoftowny.townywars.storage.MySQL;
import net.realmoftowny.townywars.storage.YMLFile;
import net.realmoftowny.townywars.tasks.FileSaveTask;
import net.realmoftowny.townywars.utils.ChatUtils;

public class TownyWars extends JavaPlugin
{
	private static TownyWars plugin;
	private ConfigManager configManager;
	private MySQL sql;
	private YMLFile file;

	
	@Override
	public void onEnable()
	{
		FileSaveTask fileSave;
	    
	    switch (this.configManager.getStorageMethod()) {
	    case FILE: 
	    	fileSave = new FileSaveTask(this);
	    	this.file = new YMLFile(this);
	    	this.file.initLists();
	    	fileSave.runTaskTimer(this, this.configManager.getSaveInterval() * 60 * 20, this.configManager.getSaveInterval() * 60 * 20);
	    	ChatUtils.sendColoredLog(this.configManager.getPluginPrefix() + " &aStorageMethod: &eFile");
	    	ChatUtils.sendColoredLog(this.configManager.getPluginPrefix() + " &bInterval: &e" + this.configManager.getSaveInterval());
	    	break;
	    case MYSQL: 
	    	this.sql = new MySQL();
	    	this.sql.connect();
	    	GemsAPI.createTable();
	    	ChatUtils.sendColoredLog(this.configManager.getPluginPrefix() + " &aStorageMethod: &eMySQL");
	    	ChatUtils.sendColoredLog(this.configManager.getPluginPrefix() + " &bMySQL Successfully Connected");
	      	break;
	    default:
	    	fileSave = new FileSaveTask(this);
	    	ChatUtils.sendColoredLog(this.configManager.getPluginPrefix() + " &cStorageMethod must be file or mysql");
	    	ChatUtils.sendColoredLog(this.configManager.getPluginPrefix() + " &aDefaulting to &eFile");
	     	this.file = new YMLFile(this);
	     	this.file.initLists();
	     	fileSave.runTaskTimer(this, this.configManager.getSaveInterval() * 60 * 20, this.configManager.getSaveInterval() * 60 * 20);
	     	ChatUtils.sendColoredLog(this.configManager.getPluginPrefix() + " &bInterval: &e" + this.configManager.getSaveInterval());
	    }

	}
	
	@Override
	public void onDisable()
	{
		
	}
	
	public MySQL getMySQL() {
		return this.sql;	
	}
	
	public static TownyWars getInstance() {
		return plugin;
	}
	
	public ConfigManager getConfigManager() {
		return this.configManager;		
	}
}