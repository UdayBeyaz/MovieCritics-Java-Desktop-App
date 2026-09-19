package moviecritics;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class Type1MainFrame extends JFrame {

    private final User currentUser;
    private JTable movieTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterGenre;

    public Type1MainFrame(User user) {
        this.currentUser = user;
        setTitle("MovieCritics - Parent Panel [" + user.getUsername() + "]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        initComponents();
        loadMovies();
    }

    private void initComponents() {
        JMenuBar menuBar = new JMenuBar();
        JMenu movieMenu = new JMenu("Movies");
        JMenu userMenu = new JMenu("Users");
        JMenu analyticsMenu = new JMenu("Analytics");
        JMenu accountMenu = new JMenu("Account");

        JMenuItem addMovie = new JMenuItem("Add Movie");
        JMenuItem removeMovie = new JMenuItem("Remove Movie");
        JMenuItem editMovie = new JMenuItem("Edit Movie");
        JMenuItem setParentalRestriction = new JMenuItem("Set Parental Restriction");
        JMenuItem moderateContent = new JMenuItem("Moderate Comments/Ratings");
        movieMenu.add(addMovie);
        movieMenu.add(removeMovie);
        movieMenu.add(editMovie);
        movieMenu.add(setParentalRestriction);
        movieMenu.add(moderateContent);

        JMenuItem manageUsers = new JMenuItem("Manage User Accounts");
        userMenu.add(manageUsers);

        JMenuItem viewAnalytics = new JMenuItem("View Family Analytics");
        analyticsMenu.add(viewAnalytics);

        JMenuItem viewFamilyRatings = new JMenuItem("View Family Ratings");
        analyticsMenu.add(viewFamilyRatings);

        JMenuItem logout = new JMenuItem("Logout");
        accountMenu.add(logout);

        menuBar.add(movieMenu);
        menuBar.add(userMenu);
        menuBar.add(analyticsMenu);
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

        String[] columns = {"ID", "Title", "Year", "Genre", "Director", "Watched", "Rating", "Parental"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        movieTable = new JTable(tableModel);
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movieTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        movieTable.getColumnModel().getColumn(1).setPreferredWidth(200);

        JScrollPane scrollPane = new JScrollPane(movieTable);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton viewDetailsBtn = new JButton("View Details");
        bottomPanel.add(viewDetailsBtn);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        addMovie.addActionListener(e -> openAddMovieDialog());
        removeMovie.addActionListener(e -> removeSelectedMovie());
        editMovie.addActionListener(e -> openEditMovieDialog());
        setParentalRestriction.addActionListener(e -> toggleParentalRestriction());
        moderateContent.addActionListener(e -> openModerateDialog());
        manageUsers.addActionListener(e -> openManageUsersDialog());
        viewAnalytics.addActionListener(e -> openAnalyticsDialog());
        viewFamilyRatings.addActionListener(e -> openFamilyRatingsDialog());
        searchBtn.addActionListener(e -> searchMovies());
        refreshBtn.addActionListener(e -> loadMovies());
        viewDetailsBtn.addActionListener(e -> openMovieDetails());
        logout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
    }

    private void loadMovies() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                SELECT m.MovieID, m.Title, m.ReleaseYear, m.Genre,
                       p.FirstName || ' ' || p.LastName AS Director,
                       m.Watched, m.Rating, m.ParentalRestriction
                FROM Movie m LEFT JOIN Person p ON m.DirectorId = p.PersonID
                ORDER BY m.Title
             """)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4),
                        rs.getString(5), rs.getBoolean(6) ? "Yes" : "No",
                        rs.getInt(7), rs.getBoolean(8) ? "Yes" : "No"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading movies: " + e.getMessage());
        }
    }

    private void searchMovies() {
        String keyword = searchField.getText().trim();
        String genre = (String) filterGenre.getSelectedItem();
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = """
                SELECT m.MovieID, m.Title, m.ReleaseYear, m.Genre,
                       p.FirstName || ' ' || p.LastName AS Director,
                       m.Watched, m.Rating, m.ParentalRestriction
                FROM Movie m LEFT JOIN Person p ON m.DirectorId = p.PersonID
                WHERE (m.Title LIKE ? OR p.FirstName || ' ' || p.LastName LIKE ?)
            """;
            if (!genre.equals("All")) sql += " AND m.Genre = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            if (!genre.equals("All")) ps.setString(3, genre);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4),
                        rs.getString(5), rs.getBoolean(6) ? "Yes" : "No",
                        rs.getInt(7), rs.getBoolean(8) ? "Yes" : "No"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage());
        }
    }

    private void openAddMovieDialog() {
        AddEditMovieDialog dialog = new AddEditMovieDialog(this, null);
        dialog.setVisible(true);
        loadMovies();
    }

    private void openEditMovieDialog() {
        int row = movieTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Please select a movie."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        Movie movie = MovieDAO.getMovieById(id);
        if (movie != null) {
            new AddEditMovieDialog(this, movie).setVisible(true);
            loadMovies();
        }
    }

    private void removeSelectedMovie() {
        int row = movieTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Please select a movie."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete '" + title + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM Movie WHERE MovieID = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
                loadMovies();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void toggleParentalRestriction() {
        int row = movieTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Please select a movie."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        String current = (String) tableModel.getValueAt(row, 7);
        int newVal = current.equals("Yes") ? 0 : 1;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE Movie SET ParentalRestriction = ? WHERE MovieID = ?")) {
            ps.setInt(1, newVal);
            ps.setInt(2, id);
            ps.executeUpdate();
            loadMovies();
            JOptionPane.showMessageDialog(this, "Parental restriction updated.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void openModerateDialog() {
        new ModerateContentDialog(this).setVisible(true);
        loadMovies();
    }

    private void openManageUsersDialog() {
        new ManageUsersDialog(this).setVisible(true);
    }

    private void openAnalyticsDialog() {
        new AnalyticsDialog(this).setVisible(true);
    }

    private void openFamilyRatingsDialog() {
        new FamilyRatingsDialog(this).setVisible(true);
    }

    private void openMovieDetails() {
        int row = movieTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Please select a movie."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        new MovieDetailsDialog(this, id, currentUser).setVisible(true);
    }
}
