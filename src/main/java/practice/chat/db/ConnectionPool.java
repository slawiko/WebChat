package practice.chat.db;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionPool {
    private static final String URL = "jdbc:mysql://localhost:3306/chat";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private static Logger logger = Logger.getLogger(ConnectionPool.class.getName());

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}
