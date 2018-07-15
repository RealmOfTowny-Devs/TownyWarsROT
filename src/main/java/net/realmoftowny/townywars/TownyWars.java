package net.realmoftowny.townywars;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.db.TownyDatabaseHandler;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.regen.TownyRegenAPI;
import me.drkmatr1984.MinevoltGems.GemsAPI;
import net.realmoftowny.townywars.events.TownyWarsEvents;
import net.realmoftowny.townywars.managers.ConfigManager;
import net.realmoftowny.townywars.managers.WarManager;
import net.realmoftowny.townywars.storage.MySQL;
import net.realmoftowny.townywars.storage.YMLFile;
import net.realmoftowny.townywars.tasks.FileSaveTask;
import net.realmoftowny.townywars.utils.ChatUtils;
import org.bukkit.ChatColor;
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
				if (args[0] == "start" && (args[1] != null || !args[1].equals(""))) {
					boolean sendNotAuthorized = true;
					try {
						Resident r = TownyUniverse.getDataSource().getResident(sender.getName());
						for (Nation n : TownyUniverse.getDataSource().getNations()) {
							if (!n.isKing(r) && !n.getAssistants().contains(r)) continue;
							else {
								sendNotAuthorized = false;
								Nation enemy = TownyUniverse.getDataSource().getNation(args[1]);
								if (enemy == null) {
								    sender.sendMessage(ChatColor.RED + "That is not a valid nation!");
                                } else if (n.hasAlly(enemy)) {
								    sender.sendMessage(ChatColor.RED + "You cannot declare war on an ally! Remove them as an ally first.");
                                } else {
                                    WarManager.createWar(n, enemy);
                                    sender.sendMessage(ChatColor.DARK_PURPLE + "Declared war on nation " + n.getName() + "!");

                                    // TODO configure war attack message
                                    for (Player p : TownyUniverse.getOnlinePlayers(enemy)) {
                                        p.sendMessage(ChatColor.DARK_PURPLE + "Nation " + n.getName() + " has declared war on your nation!");
                                    }
                                }
								break;
							}
						}
                        if (sendNotAuthorized) {
                            sender.sendMessage(ChatColor.DARK_PURPLE + "You do not have the authorization to declare war!");
                        }
					} catch (NotRegisteredException e) {
						e.printStackTrace();
					}
				}
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