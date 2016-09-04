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

import static hoz.ceph.staffspy.Database.*;

/**
 * Created by Ceph on 07.08.2016.
 */

public class PlayerListener implements Listener {
    private Main plugin;
    private long loginTime, currentTime;
    public HashMap<String, Long> players = new HashMap<>();
    public HashMap<String, Long> times = new HashMap<>();

    PlayerListener(Main p) {
        this.plugin = p;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String UUID = e.getPlayer().getUniqueId().toString();
        String player = e.getPlayer().getName();

        if (Database.checkConnection() && e.getPlayer().hasPermission("staffspy.spy")) {
            try {
                statement = connection.createStatement();
                ResultSet rGetUsers = statement.executeQuery(getUsers(UUID)); // Gets player from DB (Users)

                players.put(UUID, loginTime = System.currentTimeMillis());

                if (rGetUsers.next()) {
                    int id = rGetUsers.getInt("id");
                    statement.execute(updatePlayerName(player, UUID)); // Updates playerName on login
                    ResultSet rGetTimes = statement.executeQuery(getTimes(id)); // Gets player from DB (Times)

                    if (rGetTimes.last()) {
                        String date = rGetTimes.getString("date");
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date rawDate = new Date();
                        String currentTime = dateFormat.format(rawDate);

                        if (!currentTime.equals(date)) {
                            statement.execute(createPlayerTimes(id)); // Creates player if doesn't exist in this date (Times)
                        }
                    } else {
                        statement.execute(createPlayerTimes(id)); // Creates player if doesn't exist in DB (Times)
                    }
                } else {
                    statement.execute(createPlayer(player, UUID)); // Creates player if doesn't exist in DB (Users)
                }
                statement.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        String UUID = e.getPlayer().getUniqueId().toString();
        if (Database.checkConnection() && e.getPlayer().hasPermission("staffspy.spy")) {
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