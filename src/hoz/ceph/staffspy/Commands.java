package hoz.ceph.staffspy;

/**
 * Created by Ceph on 14.08.2016.


public class Commands implements CommandExecutor {
    /public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (args.length == 1 && connection != null) {
            try {
                ResultSet r = statement.executeQuery("SELECT alltime FROM `" + Main.table + "` WHERE player = '" + args[0] + "'");
                String playerName = args[0];

                if (r.next()) {
                    long allTime = r.getLong("alltime");
                    long allTimeInSec = allTime / 1000;
                    long allTimeMins = allTimeInSec / 60;
                    long allTimeHours = allTimeMins / 60;
                    long allTimeDays = allTimeHours / 24;

                    sender.sendMessage(Main.pluginPrefix + "Aktualne nahrany cas hrace " + playerName + " je " + allTimeDays + " dnu, " + allTimeHours + " hodin a " + allTimeMins + " minut.");
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
*/