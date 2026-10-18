package attendance.listener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import attendance.dao.DBConnection;

public class AppContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent event) {
        Properties properties = new Properties();
        try (InputStream in = event.getServletContext().getResourceAsStream("/WEB-INF/db.properties")) {
            if (in == null) {
                LOGGER.severe("WEB-INF/db.properties was not found.");
                return;
            }
            properties.load(in);
            DBConnection.init(properties);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not read database settings.", e);
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Could not initialize JDBC driver.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // DriverManager connections are closed after each query.
    }
}
