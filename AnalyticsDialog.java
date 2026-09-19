package moviecritics;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AnalyticsDialog extends JDialog {

    public AnalyticsDialog(JFrame parent) {
        super(parent, "Family Analytics", true);
        setSize(700, 500);
        setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Most Watched", buildMostWatchedPanel());
        tabs.addTab("Average Ratings", buildAvgRatingsPanel());
        tabs.addTab("Genre Statistics", buildGenreStatsPanel());
        tabs.addTab("User Activity", buildUserActivityPanel());

        add(tabs);
    }

    private JPanel buildMostWatchedPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"Title", "Year", "Genre", "Overall Rating"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                SELECT Title, ReleaseYear, Genre, Rating
                FROM Movie WHERE Watched = 1
                ORDER BY Rating DESC LIMIT 10
             """)) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getInt(2), rs.getString(3), rs.getInt(4)});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
        panel.add(new JScrollPane(table));
        return panel;
    }

    private JPanel buildAvgRatingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"Movie Title", "Average Rating", "Number of Ratings"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                SELECT m.Title, ROUND(AVG(ur.Score), 2) AS AvgScore, COUNT(ur.RatingID) AS NumRatings
                FROM Movie m
                JOIN UserRating ur ON m.MovieID = ur.MovieID
                GROUP BY m.MovieID
                ORDER BY AvgScore DESC
             """)) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getDouble(2), rs.getInt(3)});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
        panel.add(new JScrollPane(table));
        return panel;
    }

    private JPanel buildGenreStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"Genre", "Total Movies", "Watched"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                SELECT Genre, COUNT(*) AS Total,
                       SUM(CASE WHEN Watched = 1 THEN 1 ELSE 0 END) AS WatchedCount
                FROM Movie GROUP BY Genre ORDER BY Total DESC
             """)) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getInt(2), rs.getInt(3)});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
        panel.add(new JScrollPane(table));
        return panel;
    }

    private JPanel buildUserActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"Username", "Type", "Ratings Given", "Comments Given"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                SELECT u.Username,
                       CASE WHEN u.UserType = 1 THEN 'Parent' ELSE 'Child' END AS Type,
                       COUNT(CASE WHEN ur.Score > 0 THEN 1 END) AS Ratings,
                       COUNT(CASE WHEN ur.Comment IS NOT NULL AND ur.Comment != '' THEN 1 END) AS Comments
                FROM Users u
                LEFT JOIN UserRating ur ON u.UserId = ur.UserId
                GROUP BY u.UserId
                ORDER BY Ratings DESC
             """)) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getInt(3), rs.getInt(4)});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
        panel.add(new JScrollPane(table));
        return panel;
    }
}
