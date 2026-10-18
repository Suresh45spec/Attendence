package attendance.gui.panels;

/**
 * Panels that show live attendance data implement this
 * so the dashboard can refresh them after every change.
 */
public interface RefreshablePanel {
    void refreshData();
}
