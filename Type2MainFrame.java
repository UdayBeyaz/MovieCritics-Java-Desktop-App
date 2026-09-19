package moviecritics;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Type2MainFrame extends JFrame {

    private final User currentUser;
    private JTable movieTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterGenre;

    public Type2MainFrame(User user) {
        this.currentUser = user;
        setTitle("MovieCritics - [" + user.getUsername() + "]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        initComponents();
        loadMovies();
    }

    private void initComponents() {
        JMenuBar menuBar = new JMenuBar();
        JMenu moviesMenu = new JMenu("Movies");
        JMenu myMenu = new JMenu("My Activities");
        JMenu accountMenu = new JMenu("Account");

        JMenuItem browseMovies = new JMenuItem("Browse Movies");
        JMenuItem viewFamilyRatings = new JMenuItem("View Family Ratings");
        moviesMenu.add(browseMovies);
        moviesMenu.add(viewFamilyRatings);

        JMenuItem myWatchlist = new JMenuItem("My Watchlist");
        JMenuItem myProgress = new JMenuItem("Track Progress");
        myMenu.add(myWatchlist);
        myMenu.add(myProgress);

        JMenuItem logout = new JMenuItem("Logout");
        accountMenu.add(logout);

        menuBar.add(moviesMenu);
        menuBar.add(myMenu);
        menuBar.add(accountMenu);
        setJMenuBar(menuBar);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        topPanel.add(searchField);
        topPanel.add(new JLabel("Genre:"));
        filterGenre = new JComboBox<>(new String[]{"All", "Sci-Fi", "Drama", "Comedy", "Action", "Thriller", "War", "Animation"});
        topPanel.add(filterGenre);
        JButton searchBtn = new JButton("Search");
        topPanel.add(searchBtn);
        JButton refreshBtn = new JButton("Show All");
        topPanel.add(refreshBtn);

        String[] columns = {"ID", "Title", "Year", "Genre", "Director", "Watched", "Rating"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        movieTable = new JTable(tableModel);
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(movieTable);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton viewDetailsBtn = new JButton("View Details");
        JButton markWatchedBtn = new JButton("Mark as Watched");
        JButton rateBtn = new JButton("Rate Movie");
        JButton addCommentBtn = new JButton("Add Comment");
        JButton addWatchlistBtn = new JButton("Add to Watchlist");
        bottomPanel.add(viewDetailsBtn);
        bottomPanel.add(markWatchedBtn);
        bottomPanel.add(rateBtn);
        bottomPanel.add(addCommentBtn);
        bottomPanel.add(addWatchlistBtn);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> searchMovies());
        refreshBtn.addActionListener(e -> loadMovies());
        viewDetailsBtn.addActionListener(e -> openMovieDetails());
        markWatchedBtn.addActionListener(e -> markWatched());
        rateBtn.addActionListener(e -> openRateDialog());
        addCommentBtn.addActionListener(e -> openAddCommentDialog());
        addWatchlistBtn.addActionListener(e -> addToWatchlist());
        myWatchlist.addActionListener(e -> openWatchlistDialog());
        myProgress.addActionListener(e -> openProgressDialog());
        viewFamilyRatings.addActionListener(e -> new FamilyRatingsDialog(this).setVisible(true));
        logout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
    }

    private void loadMovies() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                SELECT m.MovieID, m.Title, m.ReleaseYear, m.Genre,
                       p.FirstName || ' ' || p.LastName AS Director,
                       ur.Score
                FROM Movie m
                LEFT JOIN Person p ON m.DirectorId = p.PersonID
                LEFT JOIN UserRating ur ON ur.MovieID = m.MovieID AND ur.UserId = ?
                WHERE m.ParentalRestriction = 0
                ORDER BY m.Title
             """)) {
            ps.setInt(1, currentUser.getUserId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4),
                        rs.getString(5), isWatched(rs.getInt(1)) ? "Yes" : "No",
                        rs.getInt(6) > 0 ? rs.getInt(6) : "-"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private boolean isWatched(int movieId) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT Watched FROM Movie WHERE MovieID = ?")) {
            ps.setInt(1, movieId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getBoolean(1);
        } catch (SQLException ignored) {}
        return false;
    }

    private void searchMovies() {
        String keyword = searchField.getText().trim();
        String genre = (String) filterGenre.getSelectedItem();
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = """
                SELECT m.MovieID, m.Title, m.ReleaseYear, m.Genre,
                       p.FirstName || ' ' || p.LastName AS Director,
                       ur.Score
                FROM Movie m
                LEFT JOIN Person p ON m.DirectorId = p.PersonID
                LEFT JOIN UserRating ur ON ur.MovieID = m.MovieID AND ur.UserId = ?
                WHERE m.ParentalRestriction = 0
                AND (m.Title LIKE ? OR p.FirstName || ' ' || p.LastName LIKE ?)
            """;
            if (!genre.equals("All")) sql += " AND m.Genre = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, currentUser.getUserId());
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, "%" + keyword + "%");
            if (!genre.equals("All")) ps.setString(4, genre);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4),
                        rs.getString(5), isWatched(rs.getInt(1)) ? "Yes" : "No",
                        rs.getInt(6) > 0 ? rs.getInt(6) : "-"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage());
        }
    }

    private void markWatched() {
        int row = movieTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Please select a movie."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE Movie SET Watched = 1 WHERE MovieID = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            loadMovies();
            JOptionPane.showMessageDialog(this, "Movie marked as watched.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void openRateDialog() {
        int row = movieTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Please select a movie."); return; }
        int movieId = (int) tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 1);
        String input = JOptionPane.showInputDialog(this, "Rate '" + title + "' (1-10):");
        if (input == null) return;
        try {
            int score = Integer.parseInt(input.trim());
            if (score < 1 || score > 10) { JOptionPane.showMessageDialog(this, "Score must be 1-10."); return; }
            try (Connection conn = DatabaseManager.getConnection()) {
                PreparedStatement check = conn.prepareStatement(
                        "SELECT RatingID FROM UserRating WHERE UserId = ? AND MovieID = ?");
                check.setInt(1, currentUser.getUserId());
                check.setInt(2, movieId);
                ResultSet rs = check.executeQuery();
                if (rs.next()) {
                    PreparedStatement update = conn.prepareStatement(
                            "UPDATE UserRating SET Score = ? WHERE UserId = ? AND MovieID = ?");
                    update.setInt(1, score);
                    update.setInt(2, currentUser.getUserId());
                    update.setInt(3, movieId);
                    update.executeUpdate();
                } else {
                    PreparedStatement insert = conn.prepareStatement(
                            "INSERT INTO UserRating (UserId, MovieID, Score) VALUES (?, ?, ?)");
                    insert.setInt(1, currentUser.getUserId());
                    insert.setInt(2, movieId);
                    insert.setInt(3, score);
                    insert.executeUpdate();
                }
            }
            loadMovies();
            JOptionPane.showMessageDialog(this, "Rating saved.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void openAddCommentDialog() {
        int row = movieTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Please select a movie."); return; }
        int movieId = (int) tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 1);
        String comment = JOptionPane.showInputDialog(this, "Write a comment for '" + title + "':");
        if (comment == null || comment.trim().isEmpty()) return;
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement check = conn.prepareStatement(
                    "SELECT RatingID FROM UserRating WHERE UserId = ? AND MovieID = ?");
            check.setInt(1, currentUser.getUserId());
            check.setInt(2, movieId);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                PreparedStatement update = conn.prepareStatement(
                        "UPDATE UserRating SET Comment = ? WHERE UserId = ? AND MovieID = ?");
                update.setString(1, comment);
                update.setInt(2, currentUser.getUserId());
                update.setInt(3, movieId);
                update.executeUpdate();
            } else {
                PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO UserRating (UserId, MovieID, Comment) VALUES (?, ?, ?)");
                insert.setInt(1, currentUser.getUserId());
                insert.setInt(2, movieId);
                insert.setString(3, comment);
                insert.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Comment saved.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void addToWatchlist() {
        int row = movieTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Please select a movie."); return; }
        int movieId = (int) tableModel.getValueAt(row, 0);
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement check = conn.prepareStatement(
                    "SELECT WatchlistID FROM Watchlist WHERE UserId = ? AND MovieID = ?");
            check.setInt(1, currentUser.getUserId());
            check.setInt(2, movieId);
            if (check.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, "Already in your watchlist.");
                return;
            }
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Watchlist (UserId, MovieID) VALUES (?, ?)");
            ps.setInt(1, currentUser.getUserId());
            ps.setInt(2, movieId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Added to watchlist.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void openWatchlistDialog() {
        new WatchlistDialog(this, currentUser).setVisible(true);
    }

    private void openProgressDialog() {
        new ProgressDialog(this, currentUser).setVisible(true);
    }

    private void openMovieDetails() {
        int row = movieTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Please select a movie."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        new MovieDetailsDialog(this, id, currentUser).setVisible(true);
    }
}
