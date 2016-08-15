package hoz.granik24.staffspy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.ResultSet;
import java.sql.SQLException;

import static hoz.granik24.staffspy.Main.statement;

/**
 * Created by Granik24 on 14.08.2016.
 */

public class Commands implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (args.length == 1) {
            try {
                ResultSet r = statement.executeQuery("SELECT alltime FROM `" + Main.table + "` WHERE player = '" + args[0] + "'");
                String playerName = args[0];

                if (r.next()) {
                    long allTime = r.getLong("alltime");
                    long allTimeSec = allTime / 1000;
                    long allTimeMins = allTimeSec / 60;
                    long allTimeHours = allTimeMins / 60;
                    long allTimeDays = allTimeHours / 24;

                    sender.sendMessage("Aktualne nahrany cas hrace " + playerName + " je " + allTimeDays + " dnu, " + allTimeHours + " hodin, " + allTimeMins + " minut a " + allTimeSec + " sekund.");
                } else {
                    sender.sendMessage("Hrac " + playerName + " nebyl nalezen.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}
