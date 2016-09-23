package hoz.ceph.staffspy;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static hoz.ceph.staffspy.Database.closeSQL;
import static hoz.ceph.staffspy.Database.connectSQL;

/**
 * Created by Ceph on 07.08.2016.
 */

public class Main extends JavaPlugin {
    public static String pluginPrefix = "&8[&aStaff&cSpy&8]&r ";
    public static String tableUsers = "SS_USERS";
    public static String tableTimes = "SS_TIMES";
    public static final Logger logger = Logger.getLogger("Minecraft");

    private String host, database, username, password;
    private int port;
    private boolean isConfigured = false;

    public void onEnable() {
        //Register listener
        PlayerListener playerListener = new PlayerListener(this);
        getServer().getPluginManager().registerEvents(playerListener, this);

        //Load config
        loadConfig();

        //Open SQL connect
        if (isConfigured) {
            connectSQL(host, database, username, password, port);
        } else {
            logger.warning("You don't have configured your MySQL yet! Set 'isConfigured' to true if everything is ready!");
            this.setEnabled(false);
        }

        //Register commands
        Commands e = new Commands();
        getCommand("staffspy").setExecutor(e);

        logger.info("Plugin was successfully enabled!");
    }

    public void onDisable() {
        //Close SQL connect
        closeSQL();

        logger.info("Plugin was successfully disabled! Goodbye.");
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
        logger.info("Config loaded");
    }
}
