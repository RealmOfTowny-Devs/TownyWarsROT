package net.realmoftowny.townywars;

import org.bukkit.plugin.java.JavaPlugin;

public class TownyWars extends JavaPlugin
{
	private static TownyWars plugin;
	
	@Override
	public void onEnable()
	{
		
	}
	
	@Override
	public void onDisable()
	{
		
	}
	
	
	
	public static TownyWars getInstance() {
		return plugin;
	}
}