package hoz.ceph.staffspy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                Logger.getLogger("Minecraft").log(Level.INFO, "connecting to the mysql");
            }

            if (connection != null) {
                Logger.getLogger("Minecraft").log(Level.INFO, "connected");
                statement = connection.createStatement();
                statement.execute(createUserTable);
                statement.execute(createTimeTable);
            } else {
                Logger.getLogger("Minecraft").log(Level.SEVERE, "Can't connect to the MySQL!");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Minecraft").log(Level.SEVERE, "Check if all SQL values are right writed. If yes, please, contact developer.");
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
