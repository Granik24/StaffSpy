package hoz.ceph.staffspy;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static hoz.ceph.staffspy.Database.*;
import static hoz.ceph.staffspy.DateUtils.getCurrentTime;

/**
 * Created by Ceph on 07.08.2016.
 */

public class PlayerListener implements Listener {
    private Main plugin;
    private long loginTime, currentTime;
    private HashMap<String, Long> players = new HashMap<>();
    private HashMap<String, Long> times = new HashMap<>();

    PlayerListener(Main p) {
        plugin = p;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String UUID = p.getUniqueId().toString();
        String playerName = p.getName();

        if (checkConnection() && p.hasPermission("staffspy.spy")) {
            try {
                statement = connection.createStatement();
                ResultSet rGetUsers = statement.executeQuery(getUsers(UUID)); // Gets player from DB (Users)

                players.put(UUID, loginTime = System.currentTimeMillis());

                if (rGetUsers.next()) {
                    int id = rGetUsers.getInt("id");
                    statement.execute(updatePlayerName(playerName, UUID)); // Updates playerName on login
                    ResultSet rGetTimes = statement.executeQuery(getTimes(id)); // Gets player from DB (Times)

                    if (rGetTimes.last()) {
                        String date = rGetTimes.getString("date");

                        if (!getCurrentTime().equals(date)) {
                            statement.execute(createPlayerTimes(id)); // Creates player if doesn't exist in this date (Times)
                        }
                    } else {
                        statement.execute(createPlayerTimes(id)); // Creates player if doesn't exist in DB (Times)
                    }
                } else {
                    statement.execute(createPlayer(playerName, UUID)); // Creates player if doesn't exist in DB (Users)
                }
                statement.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String UUID = p.getUniqueId().toString();
        if (checkConnection() && p.hasPermission("staffspy.spy")) {
            try {
                statement = connection.createStatement();
                ResultSet rGetUsers = statement.executeQuery(getUsers(UUID)); // Gets player from DB (Users)
                if (rGetUsers.next()) {
                    int id = rGetUsers.getInt("id");
                    ResultSet rGetTimes = statement.executeQuery(getTimes(id)); // Gets player from DB (Times)

                    if (rGetTimes.last()) {

                        times.put(UUID, currentTime = System.currentTimeMillis());

                        long loginTime = players.get(UUID);
                        long currentTime = times.get(UUID) - loginTime;
                        long allPlayed = rGetTimes.getLong("totalPlayed");
                        long finalTime = currentTime + allPlayed;
                        String dbDate = rGetTimes.getString("date");

                        statement.execute(updatePlayerTime(finalTime, dbDate, id)); // Update playerTime (Times)

                        ResultSet rGetFinalTimes = statement.executeQuery(sumTotalTime(id)); // Sum all playerTimes
                        if (rGetFinalTimes.next()) {
                            long finalAllPlayed = rGetFinalTimes.getLong("totalPlayed");

                            statement.execute(updateFinalTime(finalAllPlayed, UUID)); // Update playerTime (Users)

                            players.remove(UUID);
                            times.remove(UUID);
                        }
                    }
                }
                statement.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}