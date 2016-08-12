package hoz.granik24.staffspy;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Granik24 on 07.08.2016.
 */

public class Main extends JavaPlugin {
    private static String host = "host", database = "db", username = "user", password = "pass";
    private static int port = 3306;
    public static String table = "StaffSpy";
    public static Connection connection;
    public static Statement statement;

    public void onEnable() {
        getLogger().info("Enabled");

        //Register listener
        PlayerListener playerListener = new PlayerListener(this);
        getServer().getPluginManager().registerEvents(playerListener, this);

        //Load config
        loadConfig();

        //Open SQL connect
        connectSQL();
    }

    public void onDisable() {
        getLogger().info("Disabled");

        //Close SQL connect
        closeSQL();
    }

    private void connectSQL() {
        try {
            if (connection != null && !connection.isClosed()) {
                return;

            } else {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
                getLogger().info("connecting to the mysql");
            }

            if (connection != null) {
                statement = connection.createStatement();
                getLogger().info("connected");
                statement.executeUpdate(createTable);
            } else {
                getLogger().warning("Can't connect to the MySQL!");
                this.setEnabled(false);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            getLogger().warning("Check if all SQL values are right writed. If yes, please, contact developer.");
        }
    }

    private void closeSQL() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private final static String createTable = "CREATE TABLE IF NOT EXISTS `" + Main.table + "` ("
            + "`player` varchar(30) NOT NULL,"
            + "`UUID` varchar(100) NOT NULL,"
            + "`logindate` timestamp NOT NULL,"
            + "`alltime` varchar(30) NOT NULL,"

            + "INDEX(UUID)" + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";

    private void loadConfig() {
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }
        reloadConfig();
        host = getConfig().getString("host");
        port = getConfig().getInt("port");
        database = getConfig().getString("database");
        table = getConfig().getString("table");
        username = getConfig().getString("username");
        password = getConfig().getString("password");
        getLogger().info("Config loaded");
    }

}
