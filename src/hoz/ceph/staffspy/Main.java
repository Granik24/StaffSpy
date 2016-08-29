package hoz.ceph.staffspy;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Created by Ceph on 07.08.2016.
 */

public class Main extends JavaPlugin {
    public static String pluginPrefix = "&8[&aStaff&cSpy&8]&r ";
    public static String tableUsers = "SS_USERS";
    public static String tableTimes = "SS_TIMES";

    private String host, database, username, password;
    private int port;
    private boolean isConfigured = false;

    public void onEnable() {
        getLogger().info("Plugin was successfully enabled!");

        //Register listener
        PlayerListener playerListener = new PlayerListener(this);
        getServer().getPluginManager().registerEvents(playerListener, this);

        //Register commands
        Commands e = new Commands();
        getCommand("staffspy").setExecutor(e);

        //Load config
        loadConfig();

        //Open SQL connect
        if (isConfigured) {
            Database.connectSQL(host, database, username, password, port);
        } else {
            getLogger().warning("You don't have configured your MySQL yet! Set 'isConfigured' to true if everything is ready!");
            this.setEnabled(false);
        }

        //Register commands
        /*Commands e = new Commands();
        getCommand("staffspy").setExecutor(e);*/
    }

    public void onDisable() {
        getLogger().info("Plugin was successfully disabled! Goodbye.");

        //Close SQL connect
        Database.closeSQL();
    }

    private void loadConfig() {
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }
        reloadConfig();
        pluginPrefix = getConfig().getString("pluginPrefix").replace("&", "§");
        isConfigured = getConfig().getBoolean("isConfigured");
        host = getConfig().getString("host");
        port = getConfig().getInt("port");
        database = getConfig().getString("database");
        tableUsers = getConfig().getString("tableUsers");
        tableTimes = getConfig().getString("tableTimes");
        username = getConfig().getString("username");
        password = getConfig().getString("password");
        getLogger().info("Config loaded");
    }

}