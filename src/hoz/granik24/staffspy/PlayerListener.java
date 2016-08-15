package hoz.granik24.staffspy;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Granik24 on 07.08.2016.
 */

public class PlayerListener implements Listener {
    private Main plugin;
    private long loginTime, currentTime;
    private HashMap<String, Long> m = new HashMap<>();
    private HashMap<String, Long> t = new HashMap<>();

    public PlayerListener(Main p) {
        this.plugin = p;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String UUID = e.getPlayer().getUniqueId().toString();
        String player = e.getPlayer().getName();

        if (Main.connection != null && e.getPlayer().hasPermission("staffspy.spy")) {
            try {
                ResultSet r = Main.statement.executeQuery("SELECT * FROM `" + Main.table + "` WHERE uuid = '" + UUID + "'"); //check if player is in db

                m.put(UUID, loginTime = System.currentTimeMillis()); // put current time and UUID to hashmap

                if (r.next()) {
                    Main.statement.execute("UPDATE `" + Main.table + "` SET logindate = NOW(), player = '" + player + "' WHERE uuid = '" + UUID + "'"); //update record for player
                    r.close();
                } else {
                    Main.statement.execute("INSERT INTO `" + Main.table + "` SET logindate = NOW(), uuid = '" + UUID + "', player = '" + player + "', alltime = '0'"); //create new record for player
                    r.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        String UUID = e.getPlayer().getUniqueId().toString();

        if (Main.connection != null && e.getPlayer().hasPermission("staffspy.spy")) {
            try {
                ResultSet r = Main.statement.executeQuery("SELECT * FROM `" + Main.table + "` WHERE uuid = '" + UUID + "'"); //check if player is in db
                r.next();

                t.put(UUID, currentTime = System.currentTimeMillis());
                long loginTime = m.get(UUID);
                long allTime = r.getLong("alltime");
                long currentTime = t.get(UUID) - loginTime;
                long resultTime = currentTime + allTime;

                Main.statement.execute("UPDATE `" + Main.table + "` SET alltime = '" + resultTime + "' WHERE uuid = '" + UUID + "'");
                r.close();
                m.remove(UUID);
                t.remove(UUID);

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}