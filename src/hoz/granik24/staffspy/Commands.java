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
                r.next();

                long allTime = r.getLong("alltime");
                long allTimeSec = allTime / 1000;
                long allTimeMins = allTimeSec / 60;
                long allTimeHours = allTimeMins / 60;
                long allTimeDays = allTimeHours / 24;

                if (allTimeDays > 1) {
                    sender.sendMessage("Aktualne nahrany cas hrace " + args[0] + " je " + allTimeDays + " dnu.");
                } else if (allTimeMins > 60) {
                    sender.sendMessage("Aktualne nahrany cas hrace " + args[0] + " je " + allTimeHours + " hodin.");
                } else if (allTimeMins < 60) {
                    sender.sendMessage("Aktualne nahrany cas hrace " + args[0] + " je " + allTimeMins + " minut.");
                } else if (allTimeSec < 60) {
                    sender.sendMessage("Aktualne nahrany cas hrace " + args[0] + " je " + allTimeSec + " sekund.");
                }

                r.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
