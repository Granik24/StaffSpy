package hoz.granik24.staffspy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Granik24 on 15.08.2016.
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
                statement = connection.createStatement();
                Logger.getLogger("Minecraft").log(Level.INFO, "connected");
                statement.executeUpdate(createTable);
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

    private final static String createTable = "CREATE TABLE IF NOT EXISTS `" + Main.table + "` ("
            + "`player` varchar(30) NOT NULL,"
            + "`UUID` varchar(100) NOT NULL,"
            + "`logindate` bigint(20) NOT NULL,"
            + "`alltime` varchar(30) NOT NULL,"

            + "INDEX(UUID)" + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
}
