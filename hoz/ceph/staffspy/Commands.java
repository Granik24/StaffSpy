package hoz.ceph.staffspy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.ResultSet;
import java.sql.SQLException;

import static hoz.ceph.staffspy.Database.*;

/**
 * Created by Ceph on 14.08.2016.
 */

public class Commands implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 1 && checkConnection()) {
            try {
                statement = connection.createStatement();
                ResultSet r = statement.executeQuery(getFinalTime(args[0]));
                String playerName = args[0];

                if (r.next()) {
                    long totalPlayed = r.getLong("totalPlayed");
                    long totalPlayedInSec = totalPlayed / 1000;
                    long totalPlayedMins = totalPlayedInSec / 60;
                    long totalPlayedHours = totalPlayedMins / 60;
                    long totalPlayedDays = totalPlayedHours / 24;

                    sender.sendMessage(Main.pluginPrefix + "Aktualne nahrany cas hrace " + playerName + " je " + totalPlayedDays + " dnu, " + totalPlayedHours + " hodin a " + totalPlayedMins + " minut.");
                } else {
                    sender.sendMessage(Main.pluginPrefix + "Hrac " + playerName + " nebyl nalezen.");
                }
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        } else if(!checkConnection()) {
            sender.sendMessage(Main.pluginPrefix + "Can't connect to the database!");
        }
        return false;
    }
}