package hoz.ceph.staffspy;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static hoz.ceph.staffspy.Database.connection;
import static hoz.ceph.staffspy.Database.statement;

/**
 * Created by Ceph on 07.08.2016.
 */

public class PlayerListener implements Listener {
    private Main plugin;
    private long loginTime, currentTime;
    private HashMap<String, Long> players = new HashMap<>();
    private HashMap<String, Long> times = new HashMap<>();

    PlayerListener(Main p) {
        this.plugin = p;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String UUID = e.getPlayer().getUniqueId().toString();
        String player = e.getPlayer().getName();

        if (connection != null && e.getPlayer().hasPermission("staffspy.spy")) {
            try {
                ResultSet users = statement.executeQuery("SELECT * FROM `" + Main.tableUsers + "` WHERE uuid = '" + UUID + "'");
                players.put(UUID, loginTime = System.currentTimeMillis());

                if (users.next()) {
                    int id = users.getInt("id");
                    statement.execute("UPDATE `" + Main.tableUsers + "` SET playerName = '" + player + "' WHERE UUID = '" + UUID + "'");
                    ResultSet timesRes = statement.executeQuery("SELECT * FROM `" + Main.tableTimes + "` WHERE playerID = '" + id + "'");

                    if (timesRes.last()) {
                        String date = timesRes.getString("date");
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date rawDate = new Date();
                        String currentTime = dateFormat.format(rawDate);

                        if (!currentTime.equals(date)) {
                            statement.execute("INSERT INTO `" + Main.tableTimes + "` SET date = NOW(), playerID = '" + id + "', totalPlayed = '0'");
                        }
                    } else {
                        statement.execute("INSERT INTO `" + Main.tableTimes + "` SET playerID = '" + id + "', date = NOW(), totalPlayed = '0'");
                    }
                } else {
                    statement.execute("INSERT INTO `" + Main.tableUsers + "` SET playerName = '" + player + "', UUID = '" + UUID + "', totalPlayed = '0'");
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
                ResultSet userRes = statement.executeQuery("SELECT * FROM `" + Main.tableUsers + "` WHERE uuid = '" + UUID + "'");
                if (userRes.next()) {
                    int id = userRes.getInt("id");
                    ResultSet dateRes = statement.executeQuery("SELECT * FROM `" + Main.tableTimes + "` WHERE playerID = '" + id + "'");

                    if (dateRes.last()) {
                        times.put(UUID, currentTime = System.currentTimeMillis());
                        long loginTime = players.get(UUID);
                        long currentTime = times.get(UUID) - loginTime;
                        long allPlayed = dateRes.getLong("totalPlayed");
                        long finalTime = currentTime + allPlayed;
                        String dbDate = dateRes.getString("date");
                        statement.execute("UPDATE `" + Main.tableTimes + "` SET totalPlayed = '" + finalTime + "' WHERE  date = '" + dbDate + "' AND playerID = '" + id + "'");

                        ResultSet finalRes = statement.executeQuery("SELECT SUM(totalPlayed) as totalPlayed FROM `" + Main.tableTimes + "` WHERE playerID = '" + id + "'");
                        if (finalRes.next()) {
                            long finalAllPlayed = finalRes.getLong("totalPlayed");
                            statement.execute("UPDATE `" + Main.tableUsers + "` SET totalPlayed = '" + finalAllPlayed + "' WHERE UUID = '" + UUID + "'");

                            players.remove(UUID);
                            times.remove(UUID);
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}