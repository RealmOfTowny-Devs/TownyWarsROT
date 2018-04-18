package net.realmoftowny.townywars.storage;

import org.bukkit.plugin.*;

import net.realmoftowny.townywars.utils.ChatUtils;

import org.bukkit.configuration.file.*;
import java.util.*;
import java.io.*;

public class YMLFile
{
    private File usersFile;
    private File dataFolder;
    private FileConfiguration users;
    public HashMap<UUID, Integer> players;
    private Plugin plugin;
    
    public YMLFile(final Plugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(this.plugin.getDataFolder().toString() + "/data");
    }
    
    public void initLists() {
        this.saveDefaultUserList();
        this.loadPlayers();
        ChatUtils.sendColoredLog("&aFile storage successfully initialized!");
    }
    
    public void saveDefaultUserList() {
        if (!this.dataFolder.exists()) {
            this.dataFolder.mkdir();
        }
        if (this.usersFile == null) {
            this.usersFile = new File(this.dataFolder, "players.yml");
        }
        if (!this.usersFile.exists()) {
            this.plugin.saveResource("data/players.yml", false);
        }
    }
    
    public void loadPlayers() {
        this.players = new HashMap<UUID, Integer>();
        this.users = (FileConfiguration)YamlConfiguration.loadConfiguration(this.usersFile);
        if (this.users.getKeys(false) != null) {
            for (final String s : this.users.getKeys(false)) {
                this.players.put(UUID.fromString(s), this.users.getInt(s + ".gems"));
            }
        }
    }
    
    public void saveUserList() {
        if (this.players != null) {
            for (final UUID id : this.players.keySet()) {
                this.users.set(id.toString() + ".gems", (Object)this.players.get(id));
            }
        }
        if (this.usersFile.exists()) {
            this.usersFile.delete();
        }
        try {
            this.users.save(this.usersFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.usersFile.createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
