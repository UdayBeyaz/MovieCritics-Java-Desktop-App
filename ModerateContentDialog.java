package moviecritics;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ModerateContentDialog extends JDialog {

    private JTable table;
    private DefaultTableModel tableModel;

    public ModerateContentDialog(JFrame parent) {
        super(parent, "Moderate Content", true);
        setSize(750, 450);
        setLocationRelativeTo(parent);
        initComponents();
        loadContent();
    }

    private void initComponents() {
        String[] cols = {"RatingID", "User", "Movie", "Score", "Comment"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton deleteRatingBtn = new JButton("Remove Rating");
        JButton deleteCommentBtn = new JButton("Remove Comment");
        JButton refreshBtn = new JButton("Refresh");
        btnPanel.add(deleteRatingBtn);
        btnPanel.add(deleteCommentBtn);
        btnPanel.add(refreshBtn);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        deleteRatingBtn.addActionListener(e -> deleteRating());
        deleteCommentBtn.addActionListener(e -> deleteComment());
        refreshBtn.addActionListener(e -> loadContent());
    }

    private void loadContent() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                SELECT ur.RatingID, u.Username, m.Title, ur.Score, ur.Comment
                FROM UserRating ur
                JOIN Users u ON ur.UserId = u.UserId
                JOIN Movie m ON ur.MovieID = m.MovieID
                ORDER BY ur.RatingID DESC
             """)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getInt(4), rs.getString(5)
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteRating() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a row."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Remove this rating?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE UserRating SET Score = 0 WHERE RatingID = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
                loadContent();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void deleteComment() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a row."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Remove this comment?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE UserRating SET Comment = '' WHERE RatingID = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
                loadContent();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}
