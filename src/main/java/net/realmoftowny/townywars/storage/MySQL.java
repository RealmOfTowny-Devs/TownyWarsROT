package net.realmoftowny.townywars.storage;

import net.realmoftowny.townywars.TownyWars;
import net.realmoftowny.townywars.utils.ChatUtils;

import java.sql.*;

public class MySQL
{
    public static Connection con;
    
    public MySQL() {
    }
    
    public boolean isConnected() {
        return MySQL.con != null;
    }
    
    public void connect() {
        if (!this.isConnected()) {
            try {
                final String connection = "jdbc:mysql://" + TownyWars.getInstance().getConfigManager().getHost() + ":" + TownyWars.getInstance().getConfigManager().getPort() + "/" + TownyWars.getInstance().getConfigManager().getDatabase() + "?autoReconnect=true&useSSL=" + Boolean.valueOf(TownyWars.getInstance().getConfigManager().isUseSSL()).toString().toLowerCase();
                MySQL.con = DriverManager.getConnection(connection, TownyWars.getInstance().getConfigManager().getUsername(), TownyWars.getInstance().getConfigManager().getPassword());
                ChatUtils.sendColoredLog(TownyWars.getInstance().getConfigManager().getPluginPrefix() + " &ahas successfully connected to MySQL Database!");
            }
            catch (SQLException e) {
                ChatUtils.sendColoredLog(TownyWars.getInstance().getConfigManager().getPluginPrefix() + " &c cannot connect to MySQL Database...");
            }
        }
    }
    
    public void disconnect() {
        try {
            MySQL.con.close();
            ChatUtils.sendColoredLog(TownyWars.getInstance().getConfigManager().getPluginPrefix() + " &ahas successfully disconnected from MySQL Database!");
        }
        catch (SQLException e) {
            ChatUtils.sendColoredLog(TownyWars.getInstance().getConfigManager().getPluginPrefix() + " &ccould not disconnect from MySQL Database...");
        }
    }
    
    public PreparedStatement getStatement(final String sql) {
        if (this.isConnected()) {
            try {
                final PreparedStatement ps = MySQL.con.prepareStatement(sql);
                return ps;
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public ResultSet getResult(final String sql) {
        if (this.isConnected()) {
            try {
                final PreparedStatement ps = this.getStatement(sql);
                final ResultSet rs = ps.executeQuery();
                return rs;
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public static void createTable()
    {
      try
      {
        PreparedStatement ps = TownyWars.getInstance().getMySQL().getStatement("CREATE TABLE IF NOT EXISTS " + TownyWars.getInstance().getConfigManager().getTable() + " (playername VARCHAR(100), UUID VARCHAR(100), Gems INT(100))");
        ps.executeUpdate();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

}
