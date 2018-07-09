package net.realmoftowny.townywars.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class ChatUtils
{	
	public static void sendColoredLog(String log) {
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', log));
	}
	
	public static void sendColoredLog(List<String> logs) {
		for(String log : logs) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', log));
		}		
	}
	
	public static void wrapText(String text, int maxLength, Collection<String> list)
    {
        while (text.length() > maxLength)
        {
            int spaceIndex = text.lastIndexOf(' ', maxLength);
            if (spaceIndex <= 0) {
                list.add(text);
                return;
            }
            list.add(text.substring(0, spaceIndex));
            text = text.substring(spaceIndex);
        }

        list.add(text);
    }
	
	public static void sendColoredMessage(Player p, String message) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
}
