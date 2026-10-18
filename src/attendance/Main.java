package attendance;

import javax.swing.SwingUtilities;

import attendance.gui.WelcomeFrame;
import attendance.service.AttendanceService;
import attendance.service.AuthService;
import attendance.service.FileStorage;
import attendance.util.UITheme;

/**
 * Starting point of the application.
 * Run this class after compiling all files.
 */
public class Main {

    public static void main(String[] args) {
        UITheme.applyLookAndFeel();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                FileStorage storage = new FileStorage();
                AuthService authService = new AuthService(storage);
                AttendanceService attendanceService = new AttendanceService(storage);
                new WelcomeFrame(authService, attendanceService).setVisible(true);
            }
        });
    }
}
