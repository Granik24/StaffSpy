package hoz.ceph.staffspy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import static hoz.ceph.staffspy.Main.logger;

/**
 * Created by Ceph on 15.08.2016.
 */

public class Database {
    public static Statement statement;
    public static Connection connection;

    public static void connectSQL(String host, String database, String username, String password, int port) {
        try {
            if (connection != null && !connection.isClosed()) {
                return;

            } else {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            }

            if (connection != null) {
                statement = connection.createStatement();
                statement.execute(createUserTable);
                statement.execute(createTimeTable);
                statement.close();
            } else {
                logger.log(Level.SEVERE, "Can't connect to the MySQL!");
            }
        } catch (ClassNotFoundException | SQLException e) {
            logger.log(Level.SEVERE, "Check if all SQL values are right writed. If yes, please, contact developer.");
            e.printStackTrace();
        }
    }

    public static void closeSQL() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean checkConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Can't connect to the database!");
            e.printStackTrace();
        }
        return false;
    }

    public static String getUsers(String uuid) {
        return "SELECT * FROM `" + Main.tableUsers + "` WHERE uuid = '" + uuid + "'";
    }

    public static String getTimes(int id) {
        return "SELECT * FROM `" + Main.tableTimes + "` WHERE playerID = '" + id + "'";
    }

    public static String getFinalTime(String args) {
        return "SELECT * FROM `" + Main.tableUsers + "` WHERE playerName = '" + args + "'";
    }

    public static String updatePlayerName(String player, String uuid) {
        return "UPDATE `" + Main.tableUsers + "` SET playerName = '" + player + "' WHERE UUID = '" + uuid + "'";
    }

    public static String updatePlayerTime(long time, String dbDate, int id) {
        return "UPDATE `" + Main.tableTimes + "` SET totalPlayed = '" + time + "' WHERE  date = '" + dbDate + "' AND playerID = '" + id + "'";
    }

    public static String updateFinalTime(long time, String uuid) {
        return "UPDATE `" + Main.tableUsers + "` SET totalPlayed = '" + time + "' WHERE UUID = '" + uuid + "'";
    }

    public static String createPlayerTimes(int id) {
        return "INSERT INTO `" + Main.tableTimes + "` SET date = NOW(), playerID = '" + id + "', totalPlayed = '0'";
    }

    public static String createPlayer(String player, String uuid) {
        return "INSERT INTO `" + Main.tableUsers + "` SET playerName = '" + player + "', UUID = '" + uuid + "', totalPlayed = '0'";
    }

    public static String sumTotalTime(int id) {
        return "SELECT SUM(totalPlayed) as totalPlayed FROM `" + Main.tableTimes + "` WHERE playerID = '" + id + "'";
    }

    private final static String createUserTable = "CREATE TABLE IF NOT EXISTS `" + Main.tableUsers + "` ("
            + "`id` int(11) NOT NULL AUTO_INCREMENT,"
            + "`playerName` varchar(30) NOT NULL,"
            + "`UUID` varchar(100) NOT NULL,"
            + "`totalPlayed` bigint(20) NOT NULL,"

            + "INDEX(id)" + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";

    private final static String createTimeTable = "CREATE TABLE IF NOT EXISTS `" + Main.tableTimes + "` ("
            + "`playerID` int(11) NOT NULL,"
            + "`date` DATE NOT NULL,"
            + "`totalPlayed` bigint(20) NOT NULL,"

            + "INDEX(playerID)" + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
}
