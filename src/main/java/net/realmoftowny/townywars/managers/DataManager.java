package net.realmoftowny.townywars.managers;

import org.bukkit.entity.*;

import net.realmoftowny.townywars.TownyWars;

import java.sql.*;
import java.util.*;
import org.bukkit.*;

public class DataManager
{
    public static void createTable() {
        try {
            final PreparedStatement ps = TownyWars.getInstance().getMySQL().getStatement("CREATE TABLE IF NOT EXISTS " + MinevoltGems.getInstance().getConfigInstance().table + " (playername VARCHAR(100), UUID VARCHAR(100), Gems INT(100))");
            ps.executeUpdate();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void register(final Player p, final int startAmount) {
        if (MinevoltGems.getInstance().getConfigInstance().method.equals(GemsConfig.StorageMethod.mysql)) {
            try {
                final PreparedStatement ps = MinevoltGems.getInstance().getMySQL().getStatement("INSERT INTO " + MinevoltGems.getInstance().getConfigInstance().table + " (Playername, UUID, Gems) VALUES (?, ?, ?)");
                ps.setString(1, p.getName());
                ps.setString(2, p.getUniqueId().toString());
                ps.setInt(3, startAmount);
                ps.executeUpdate();
                ps.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else {
            MinevoltGems.getInstance().getYMLFile().players.put(p.getUniqueId(), startAmount);
        }
    }
    
    public static boolean isRegistered(final Player p) {
        if (MinevoltGems.getInstance().getConfigInstance().method.equals(GemsConfig.StorageMethod.mysql)) {
            try {
                final PreparedStatement ps = MinevoltGems.getInstance().getMySQL().getStatement("SELECT * FROM " + MinevoltGems.getInstance().getConfigInstance().table + " WHERE UUID= ?");
                ps.setString(1, p.getUniqueId().toString());
                final ResultSet rs = ps.executeQuery();
                final boolean user = rs.next();
                rs.close();
                rs.close();
                return user;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
        for (final UUID id : MinevoltGems.getInstance().getYMLFile().players.keySet()) {
            if (id.toString().equals(p.getUniqueId().toString())) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isRegistered(final String name) {
        if (MinevoltGems.getInstance().getConfigInstance().method.equals(GemsConfig.StorageMethod.mysql)) {
            try {
                final PreparedStatement ps = MinevoltGems.getInstance().getMySQL().getStatement("SELECT * FROM " + MinevoltGems.getInstance().getConfigInstance().table + " WHERE Playername= ?");
                ps.setString(1, name);
                final ResultSet rs = ps.executeQuery();
                final boolean user = rs.next();
                rs.close();
                rs.close();
                return user;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
        for (final UUID id : MinevoltGems.getInstance().getYMLFile().players.keySet()) {
            if (getPlayerNamefromUUID(id).equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public static int getGems(final Player p) {
        if (MinevoltGems.getInstance().getConfigInstance().method.equals(GemsConfig.StorageMethod.mysql)) {
            try {
                final PreparedStatement ps = MinevoltGems.getInstance().getMySQL().getStatement("SELECT * FROM " + MinevoltGems.getInstance().getConfigInstance().table + " WHERE UUID= ?");
                ps.setString(1, p.getUniqueId().toString());
                final ResultSet rs = ps.executeQuery();
                rs.next();
                final int gems = rs.getInt("Gems");
                rs.close();
                ps.close();
                return gems;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return -1;
            }
        }
        for (final UUID id : MinevoltGems.getInstance().getYMLFile().players.keySet()) {
            if (id.toString().equals(p.getUniqueId().toString())) {
                return MinevoltGems.getInstance().getYMLFile().players.get(id);
            }
        }
        return -1;
    }
    
    public static boolean setGems(final Player p, final int gems) {
        if (gems < 0) {
            return false;
        }
        if (MinevoltGems.getInstance().getConfigInstance().method.equals(GemsConfig.StorageMethod.mysql)) {
            try {
                final PreparedStatement ps = MinevoltGems.getInstance().getMySQL().getStatement("UPDATE " + MinevoltGems.getInstance().getConfigInstance().table + " SET Gems= ? WHERE UUID= ?");
                ps.setInt(1, gems);
                ps.setString(2, p.getUniqueId().toString());
                ps.executeUpdate();
                ps.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else {
            MinevoltGems.getInstance().getYMLFile().players.put(p.getUniqueId(), gems);
        }
        return true;
    }
    
    public static void addGems(final Player p, final int gems) {
        if (MinevoltGems.getInstance().getConfigInstance().method.equals(GemsConfig.StorageMethod.mysql)) {
            try {
                final PreparedStatement ps = MinevoltGems.getInstance().getMySQL().getStatement("UPDATE " + MinevoltGems.getInstance().getConfigInstance().table + " SET Gems= ? WHERE UUID= ?");
                ps.setInt(1, getGems(p) + gems);
                ps.setString(2, p.getUniqueId().toString());
                ps.executeUpdate();
                ps.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else {
            MinevoltGems.getInstance().getYMLFile().players.put(p.getUniqueId(), getGems(p) + gems);
        }
    }
    
    public static boolean removeGems(final Player p, final int gems) {
        if (getGems(p) - gems < 0) {
            return false;
        }
        if (MinevoltGems.getInstance().getConfigInstance().method.equals(GemsConfig.StorageMethod.mysql)) {
            try {
                final PreparedStatement ps = MinevoltGems.getInstance().getMySQL().getStatement("UPDATE " + MinevoltGems.getInstance().getConfigInstance().table + " SET Gems= ? WHERE UUID= ?");
                ps.setInt(1, getGems(p) - gems);
                ps.setString(2, p.getUniqueId().toString());
                ps.executeUpdate();
                ps.close();
                return true;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
        MinevoltGems.getInstance().getYMLFile().players.put(p.getUniqueId(), getGems(p) - gems);
        return true;
    }
    
    public static int getGems(final String name) {
        if (MinevoltGems.getInstance().getConfigInstance().method.equals(GemsConfig.StorageMethod.mysql)) {
            try {
                final PreparedStatement ps = MinevoltGems.getInstance().getMySQL().getStatement("SELECT * FROM " + MinevoltGems.getInstance().getConfigInstance().table + " WHERE Playername= ?");
                ps.setString(1, name);
                final ResultSet rs = ps.executeQuery();
                rs.next();
                final int gems = rs.getInt("Gems");
                rs.close();
                ps.close();
                return gems;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return -1;
            }
        }
        for (final UUID id : MinevoltGems.getInstance().getYMLFile().players.keySet()) {
            if (getPlayerNamefromUUID(id).equals(name)) {
                return MinevoltGems.getInstance().getYMLFile().players.get(id);
            }
        }
        return -1;
    }
    
    public static boolean setGems(final String name, final int gems) {
        if (gems < 0) {
            return false;
        }
        if (MinevoltGems.getInstance().getConfigInstance().method.equals(GemsConfig.StorageMethod.mysql)) {
            try {
                final PreparedStatement ps = MinevoltGems.getInstance().getMySQL().getStatement("UPDATE " + MinevoltGems.getInstance().getConfigInstance().table + " SET Gems= ? WHERE Playername= ?");
                ps.setInt(1, gems);
                ps.setString(2, name);
                ps.executeUpdate();
                ps.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else {
            MinevoltGems.getInstance().getYMLFile().players.put(getUUIDfromPlayerName(name), gems);
        }
        return true;
    }
    
    public static void addGems(final String name, final int gems) {
        if (MinevoltGems.getInstance().getConfigInstance().method.equals(GemsConfig.StorageMethod.mysql)) {
            try {
                final PreparedStatement ps = MinevoltGems.getInstance().getMySQL().getStatement("UPDATE " + MinevoltGems.getInstance().getConfigInstance().table + " SET Gems= ? WHERE Playername= ?");
                ps.setInt(1, getGems(name) + gems);
                ps.setString(2, name);
                ps.executeUpdate();
                ps.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else {
            MinevoltGems.getInstance().getYMLFile().players.put(getUUIDfromPlayerName(name), getGems(name) + gems);
        }
    }
    
    public static boolean removeGems(final String name, final int gems) {
        if (getGems(name) - gems < 0) {
            return false;
        }
        if (MinevoltGems.getInstance().getConfigInstance().method.equals(GemsConfig.StorageMethod.mysql)) {
            try {
                final PreparedStatement ps = MinevoltGems.getInstance().getMySQL().getStatement("UPDATE " + MinevoltGems.getInstance().getConfigInstance().table + " SET Gems= ? WHERE Playername= ?");
                ps.setInt(1, getGems(name) - gems);
                ps.setString(2, name);
                ps.executeUpdate();
                ps.close();
                return true;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
        MinevoltGems.getInstance().getYMLFile().players.put(getUUIDfromPlayerName(name), getGems(name) - gems);
        return true;
    }
    
    public static UUID getUUIDfromPlayerName(final String playerName) {
        final OfflinePlayer op = Bukkit.getOfflinePlayer(playerName);
        if (!op.equals(null)) {
            return op.getUniqueId();
        }
        return null;
    }
    
    public static String getPlayerNamefromUUID(final UUID uuid) {
        final OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        if (!op.equals(null)) {
            return op.getName();
        }
        return "";
    }
}
