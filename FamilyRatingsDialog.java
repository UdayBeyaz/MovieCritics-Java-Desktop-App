package moviecritics;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FamilyRatingsDialog extends JDialog {

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> movieCombo;

    public FamilyRatingsDialog(JFrame parent) {
        super(parent, "Family Ratings", true);
        setSize(650, 430);
        setLocationRelativeTo(parent);
        initComponents();
        loadMovieCombo();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Movie:"));
        movieCombo = new JComboBox<>();
        movieCombo.setPreferredSize(new Dimension(250, 25));
        topPanel.add(movieCombo);
        JButton showBtn = new JButton("Show Ratings");
        topPanel.add(showBtn);

        String[] cols = {"Username", "User Type", "Score", "Comment"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        showBtn.addActionListener(e -> loadRatings());
    }

    private void loadMovieCombo() {
        movieCombo.addItem("-- Select Movie --");
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Title FROM Movie ORDER BY Title")) {
            while (rs.next()) movieCombo.addItem(rs.getString(1));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void loadRatings() {
        String selectedTitle = (String) movieCombo.getSelectedItem();
        if (selectedTitle == null || selectedTitle.startsWith("--")) return;
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                SELECT u.Username,
                       CASE WHEN u.UserType = 1 THEN 'Parent' ELSE 'Child' END,
                       ur.Score, ur.Comment
                FROM UserRating ur
                JOIN Users u ON ur.UserId = u.UserId
                JOIN Movie m ON ur.MovieID = m.MovieID
                WHERE m.Title = ?
                ORDER BY ur.Score DESC
             """)) {
            ps.setString(1, selectedTitle);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString(1), rs.getString(2),
                        rs.getInt(3) > 0 ? rs.getInt(3) : "-",
                        rs.getString(4)
                });
            }
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No ratings yet for this movie.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
