package hoz.granik24.staffspy;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static hoz.granik24.staffspy.Database.statement;
import static hoz.granik24.staffspy.Database.connection;

/**
 * Created by Granik24 on 07.08.2016.
 */

public class PlayerListener implements Listener {
    private Main plugin;
    private long loginTime, currentTime;
    private HashMap<String, Long> players = new HashMap<>();
    private HashMap<String, Long> times = new HashMap<>();

    public PlayerListener(Main p) {
        this.plugin = p;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String UUID = e.getPlayer().getUniqueId().toString();
        String player = e.getPlayer().getName();

        if (connection != null && e.getPlayer().hasPermission("staffspy.spy")) {
            try {
                ResultSet r = statement.executeQuery("SELECT * FROM `" + Main.table + "` WHERE uuid = '" + UUID + "'"); //check if player is in db

                players.put(UUID, loginTime = System.currentTimeMillis()); // put current time and UUID to hashmap

                if (r.next()) {
                    statement.execute("UPDATE `" + Main.table + "` SET logindate = NOW(), player = '" + player + "' WHERE uuid = '" + UUID + "'"); //update record for player
                } else {
                    statement.execute("INSERT INTO `" + Main.table + "` SET logindate = NOW(), uuid = '" + UUID + "', player = '" + player + "', alltime = '0'"); //create new record for player
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        String UUID = e.getPlayer().getUniqueId().toString();

        if (connection != null && e.getPlayer().hasPermission("staffspy.spy")) {
            try {
                ResultSet r = statement.executeQuery("SELECT * FROM `" + Main.table + "` WHERE uuid = '" + UUID + "'"); //check if player is in db
                r.next();

                times.put(UUID, currentTime = System.currentTimeMillis());
                long loginTime = players.get(UUID);
                long allTime = r.getLong("alltime");
                long currentTime = times.get(UUID) - loginTime;
                long resultTime = currentTime + allTime;

                statement.execute("UPDATE `" + Main.table + "` SET alltime = '" + resultTime + "' WHERE uuid = '" + UUID + "'");
                players.remove(UUID);
                times.remove(UUID);

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}