package moviecritics;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class WatchlistDialog extends JDialog {

    private final User currentUser;
    private JTable table;
    private DefaultTableModel tableModel;

    public WatchlistDialog(JFrame parent, User user) {
        super(parent, "My Watchlist - " + user.getUsername(), true);
        this.currentUser = user;
        setSize(600, 400);
        setLocationRelativeTo(parent);
        initComponents();
        loadWatchlist();
    }

    private void initComponents() {
        String[] cols = {"ID", "Title", "Year", "Genre"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton removeBtn = new JButton("Remove from Watchlist");
        btnPanel.add(removeBtn);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        removeBtn.addActionListener(e -> removeFromWatchlist());
    }

    private void loadWatchlist() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                SELECT m.MovieID, m.Title, m.ReleaseYear, m.Genre
                FROM Watchlist wl
                JOIN Movie m ON wl.MovieID = m.MovieID
                WHERE wl.UserId = ?
                ORDER BY m.Title
             """)) {
            ps.setInt(1, currentUser.getUserId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4)});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void removeFromWatchlist() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a movie."); return; }
        int movieId = (int) tableModel.getValueAt(row, 0);
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM Watchlist WHERE UserId = ? AND MovieID = ?")) {
            ps.setInt(1, currentUser.getUserId());
            ps.setInt(2, movieId);
            ps.executeUpdate();
            loadWatchlist();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
