package hoz.ceph.staffspy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.ResultSet;
import java.sql.SQLException;

import static hoz.ceph.staffspy.Database.connection;
import static hoz.ceph.staffspy.Database.statement;

/**
 * Created by Ceph on 14.08.2016.
*/

public class Commands implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (args.length == 1 && connection != null) {
            try {
                ResultSet r = statement.executeQuery("SELECT * FROM `" + Main.tableUsers + "` WHERE playerName = '" + args[0] + "'");
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
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}