package moviecritics;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class MovieDetailsDialog extends JDialog {

    public MovieDetailsDialog(JFrame parent, int movieId, User currentUser) {
        super(parent, "Movie Details", true);
        setSize(500, 550);
        setLocationRelativeTo(parent);
        initComponents(movieId);
    }

    private void initComponents(int movieId) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                SELECT m.*, 
                       d.FirstName || ' ' || d.LastName AS Director,
                       la.FirstName || ' ' || la.LastName AS LeadActor,
                       sa.FirstName || ' ' || sa.LastName AS SupportActor
                FROM Movie m
                LEFT JOIN Person d ON m.DirectorId = d.PersonID
                LEFT JOIN Person la ON m.LeadActorId = la.PersonID
                LEFT JOIN Person sa ON m.SupportActorId = sa.PersonID
                WHERE m.MovieID = ?
             """)) {
            ps.setInt(1, movieId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int row = 0;
                addRow(panel, gbc, row++, "Title:", rs.getString("Title"));
                addRow(panel, gbc, row++, "Year:", String.valueOf(rs.getInt("ReleaseYear")));
                addRow(panel, gbc, row++, "Language:", rs.getString("Language"));
                addRow(panel, gbc, row++, "Country:", rs.getString("Country"));
                addRow(panel, gbc, row++, "Genre:", rs.getString("Genre"));
                addRow(panel, gbc, row++, "Director:", rs.getString("Director"));
                addRow(panel, gbc, row++, "Lead Actor:", rs.getString("LeadActor"));
                addRow(panel, gbc, row++, "Support Actor:", rs.getString("SupportActor"));
                addRow(panel, gbc, row++, "Rating:", String.valueOf(rs.getInt("Rating")));
                addRow(panel, gbc, row++, "Watched:", rs.getBoolean("Watched") ? "Yes" : "No");
                addRow(panel, gbc, row++, "Parental:", rs.getBoolean("ParentalRestriction") ? "Restricted" : "All Ages");

                JLabel aboutLabel = new JLabel("About:");
                aboutLabel.setFont(aboutLabel.getFont().deriveFont(Font.BOLD));
                gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
                panel.add(aboutLabel, gbc);

                JTextArea aboutArea = new JTextArea(rs.getString("About"), 4, 25);
                aboutArea.setLineWrap(true);
                aboutArea.setWrapStyleWord(true);
                aboutArea.setEditable(false);
                gbc.gridx = 1; gbc.gridy = row++;
                panel.add(new JScrollPane(aboutArea), gbc);

                JLabel commLabel = new JLabel("Comments:");
                commLabel.setFont(commLabel.getFont().deriveFont(Font.BOLD));
                gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
                panel.add(commLabel, gbc);

                JTextArea commArea = new JTextArea(rs.getString("Comments"), 3, 25);
                commArea.setLineWrap(true);
                commArea.setWrapStyleWord(true);
                commArea.setEditable(false);
                gbc.gridx = 1; gbc.gridy = row++;
                panel.add(new JScrollPane(commArea), gbc);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        add(new JScrollPane(panel), BorderLayout.CENTER);
        JPanel btnPanel = new JPanel();
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, String value) {
        JLabel label = new JLabel(labelText);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(label, gbc);

        JLabel val = new JLabel(value != null ? value : "-");
        gbc.gridx = 1; gbc.gridy = row;
        panel.add(val, gbc);
    }
}
