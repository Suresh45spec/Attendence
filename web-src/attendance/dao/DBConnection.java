package attendance.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Opens JDBC connections using settings from WEB-INF/db.properties.
 */
public class DBConnection {

    private static String url;
    private static String user;
    private static String password;
    private static boolean ready;

    private DBConnection() {
    }

    public static synchronized void init(Properties properties) {
        url = properties.getProperty("jdbc.url");
        user = properties.getProperty("jdbc.user");
        password = properties.getProperty("jdbc.password", "");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            ready = true;
        } catch (ClassNotFoundException e) {
            ready = false;
            throw new IllegalStateException("MySQL JDBC driver not found in WEB-INF/lib.", e);
        }
    }

    public static boolean isReady() {
        return ready;
    }

    public static Connection getConnection() throws SQLException {
        if (!ready) {
            throw new SQLException("Database is not configured. Check WEB-INF/db.properties.");
        }
        return DriverManager.getConnection(url, user, password);
    }
}
