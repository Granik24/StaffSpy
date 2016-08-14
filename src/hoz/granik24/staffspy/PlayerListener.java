package hoz.granik24.staffspy;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

import static hoz.granik24.staffspy.Main.connection;
import static hoz.granik24.staffspy.Main.statement;

/**
 * Created by Granik24 on 07.08.2016.
 */

public class PlayerListener implements Listener {
    private Main plugin;

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

                if (r.next()) {
                    statement.execute("UPDATE `" + Main.table + "` SET logindate = NOW(), player = '" + player + "' WHERE uuid = '" + UUID + "'"); //update record for player
                    r.close();
                } else {
                    statement.execute("INSERT INTO `" + Main.table + "` SET logindate = NOW(), uuid = '" + UUID + "', player = '" + player + "', alltime = '0000-00-00 00:00:00'"); //create new record for player
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

        if (connection != null && e.getPlayer().hasPermission("staffspy.spy")) {
            try {

                statement.execute("UPDATE `" + Main.table + "` SET alltime = alltime + TIMEDIFF(NOW(), logindate) WHERE uuid = '" + UUID + "'");

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}