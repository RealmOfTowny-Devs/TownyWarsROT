package net.realmoftowny.townywars;

import me.drkmatr1984.MinevoltGems.GemsAPI;
import net.realmoftowny.townywars.events.TownyWarsEvents;
import net.realmoftowny.townywars.managers.ConfigManager;
import net.realmoftowny.townywars.storage.MySQL;
import net.realmoftowny.townywars.storage.YMLFile;
import net.realmoftowny.townywars.tasks.FileSaveTask;
import net.realmoftowny.townywars.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TownyWars extends JavaPlugin
{
	private static TownyWars plugin;
	private ConfigManager configManager;
	private MySQL sql;
	private YMLFile file;

	public static float pPlayer = 2f, pPlot = 1f, pKill = 1f, pMayorKill = 3f, pKingKill = 10f;
	
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

	    PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new TownyWarsEvents(), this);

	}
	
	@Override
	public void onDisable()
	{
		
	}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	    if (command.getAliases().contains("tw")) {
	        if (sender instanceof Player) {

            } else if (sender instanceof ConsoleCommandSender) {

            }
        }
        return super.onCommand(sender, command, label, args);
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