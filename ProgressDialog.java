package moviecritics;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ProgressDialog extends JDialog {

    private final User currentUser;

    public ProgressDialog(JFrame parent, User user) {
        super(parent, "Progress Tracker", true);
        this.currentUser = user;
        setSize(550, 400);
        setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        int totalMovies = getCount("SELECT COUNT(*) FROM Movie WHERE ParentalRestriction = 0");
        int myWatched = getCount("SELECT COUNT(*) FROM Movie WHERE Watched = 1 AND ParentalRestriction = 0");

        JLabel summaryLabel = new JLabel(
                "Total available movies: " + totalMovies + "   |   You watched: " + myWatched
                        + " (" + (totalMovies > 0 ? (myWatched * 100 / totalMovies) : 0) + "%)");
        summaryLabel.setFont(summaryLabel.getFont().deriveFont(Font.BOLD, 13f));
        panel.add(summaryLabel, BorderLayout.NORTH);

        String[] cols = {"Username", "Ratings Given", "Comments Given"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                SELECT u.Username,
                       COUNT(CASE WHEN ur.Score > 0 THEN 1 END) AS Ratings,
                       COUNT(CASE WHEN ur.Comment IS NOT NULL AND ur.Comment != '' THEN 1 END) AS Comments
                FROM Users u
                LEFT JOIN UserRating ur ON u.UserId = ur.UserId
                WHERE u.UserType = 2
                GROUP BY u.UserId
                ORDER BY Ratings DESC
             """)) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getInt(2), rs.getInt(3)});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(closeBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private int getCount(String sql) {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
