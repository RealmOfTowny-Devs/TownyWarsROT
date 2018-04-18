package net.realmoftowny.townywars.storage;

import org.bukkit.*;
import java.sql.*;

public class MySQL
{
    public static Connection con;
    private String host;
    private String port;
    private String database;
    private String username;
    private String password;
    private Boolean useSSL;
    
    public MySQL() {
        this.host = "127.0.0.1";
        this.port = "3306";
        this.database = "MinevoltGems";
        this.username = "root";
        this.password = "example";
        this.useSSL = false;
        this.host = MinevoltGems.getInstance().getConfigInstance().host;
        this.port = MinevoltGems.getInstance().getConfigInstance().port;
        this.database = MinevoltGems.getInstance().getConfigInstance().database;
        this.username = MinevoltGems.getInstance().getConfigInstance().username;
        this.password = MinevoltGems.getInstance().getConfigInstance().password;
        this.useSSL = MinevoltGems.getInstance().getConfigInstance().useSSL;
    }
    
    public boolean isConnected() {
        return MySQL.con != null;
    }
    
    public void connect() {
        if (!this.isConnected()) {
            try {
                final String connection = "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true&useSSL=" + this.useSSL.toString().toLowerCase();
                MySQL.con = DriverManager.getConnection(connection, this.username, this.password);
                Bukkit.getConsoleSender().sendMessage(GemsCommandExecutor.getColoredMessage(MinevoltGems.getInstance().getConfigInstance().pr + " &ahas successfully connected to MySQL Database!"));
            }
            catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage(GemsCommandExecutor.getColoredMessage(MinevoltGems.getInstance().getConfigInstance().pr + " &c cannot connect to MySQL Database..."));
            }
        }
    }
    
    public void disconnect() {
        try {
            MySQL.con.close();
            Bukkit.getConsoleSender().sendMessage(GemsCommandExecutor.getColoredMessage(MinevoltGems.getInstance().getConfigInstance().pr + " &ahas successfully disconnected from MySQL Database!"));
        }
        catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(GemsCommandExecutor.getColoredMessage(MinevoltGems.getInstance().getConfigInstance().pr + " &ccould not disconnect from MySQL Database..."));
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
}
