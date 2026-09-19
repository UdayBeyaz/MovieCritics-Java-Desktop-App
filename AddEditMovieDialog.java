package moviecritics;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AddEditMovieDialog extends JDialog {

    private final Movie movie;
    private JTextField titleField, yearField, languageField, countryField, posterField;
    private JComboBox<String> genreCombo;
    private JComboBox<PersonItem> directorCombo, leadActorCombo, supportActorCombo;
    private JTextArea aboutArea, commentsArea;
    private JSpinner ratingSpinner;
    private JCheckBox watchedCheck, parentalCheck;

    public AddEditMovieDialog(JFrame parent, Movie movie) {
        super(parent, movie == null ? "Add Movie" : "Edit Movie", true);
        this.movie = movie;
        setSize(550, 620);
        setLocationRelativeTo(parent);
        initComponents();
        if (movie != null) populateFields();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        List<Person> persons = MovieDAO.getAllPersons();
        PersonItem[] personItems = new PersonItem[persons.size() + 1];
        personItems[0] = new PersonItem(0, "-- Select --");
        for (int i = 0; i < persons.size(); i++) {
            personItems[i + 1] = new PersonItem(persons.get(i).getPersonID(), persons.get(i).getFullName());
        }

        String[] genres = {"Sci-Fi", "Drama", "Comedy", "Action", "Thriller", "War", "Animation", "Horror", "Romance", "Documentary"};

        int row = 0;
        panel.add(new JLabel("Title:"), label(gbc, 0, row));
        titleField = new JTextField(20);
        panel.add(titleField, field(gbc, 1, row++));

        panel.add(new JLabel("Release Year:"), label(gbc, 0, row));
        yearField = new JTextField();
        panel.add(yearField, field(gbc, 1, row++));

        panel.add(new JLabel("Language:"), label(gbc, 0, row));
        languageField = new JTextField();
        panel.add(languageField, field(gbc, 1, row++));

        panel.add(new JLabel("Country:"), label(gbc, 0, row));
        countryField = new JTextField();
        panel.add(countryField, field(gbc, 1, row++));

        panel.add(new JLabel("Genre:"), label(gbc, 0, row));
        genreCombo = new JComboBox<>(genres);
        panel.add(genreCombo, field(gbc, 1, row++));

        panel.add(new JLabel("Director:"), label(gbc, 0, row));
        directorCombo = new JComboBox<>(personItems);
        panel.add(directorCombo, field(gbc, 1, row++));

        panel.add(new JLabel("Lead Actor:"), label(gbc, 0, row));
        leadActorCombo = new JComboBox<>(personItems);
        panel.add(leadActorCombo, field(gbc, 1, row++));

        panel.add(new JLabel("Support Actor:"), label(gbc, 0, row));
        supportActorCombo = new JComboBox<>(personItems);
        panel.add(supportActorCombo, field(gbc, 1, row++));

        panel.add(new JLabel("Rating (1-10):"), label(gbc, 0, row));
        ratingSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
        panel.add(ratingSpinner, field(gbc, 1, row++));

        panel.add(new JLabel("Poster URL:"), label(gbc, 0, row));
        posterField = new JTextField();
        panel.add(posterField, field(gbc, 1, row++));

        panel.add(new JLabel("About:"), label(gbc, 0, row));
        aboutArea = new JTextArea(3, 20);
        aboutArea.setLineWrap(true);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(new JScrollPane(aboutArea), gbc);

        panel.add(new JLabel("Comments:"), label(gbc, 0, row));
        commentsArea = new JTextArea(2, 20);
        commentsArea.setLineWrap(true);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(new JScrollPane(commentsArea), gbc);

        watchedCheck = new JCheckBox("Watched");
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(watchedCheck, gbc);

        parentalCheck = new JCheckBox("Parental Restriction");
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(parentalCheck, gbc);

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        add(new JScrollPane(panel), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> save());
        cancelBtn.addActionListener(e -> dispose());
    }

    private GridBagConstraints label(GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x; gbc.gridy = y; gbc.gridwidth = 1;
        return (GridBagConstraints) gbc.clone();
    }

    private GridBagConstraints field(GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x; gbc.gridy = y; gbc.gridwidth = 1;
        return (GridBagConstraints) gbc.clone();
    }

    private void populateFields() {
        titleField.setText(movie.getTitle());
        yearField.setText(String.valueOf(movie.getReleaseYear()));
        languageField.setText(movie.getLanguage());
        countryField.setText(movie.getCountry());
        genreCombo.setSelectedItem(movie.getGenre());
        selectPersonInCombo(directorCombo, movie.getDirectorId());
        selectPersonInCombo(leadActorCombo, movie.getLeadActorId());
        selectPersonInCombo(supportActorCombo, movie.getSupportActorId());
        ratingSpinner.setValue(movie.getRating() > 0 ? movie.getRating() : 5);
        posterField.setText(movie.getPoster() != null ? movie.getPoster() : "");
        aboutArea.setText(movie.getAbout() != null ? movie.getAbout() : "");
        commentsArea.setText(movie.getComments() != null ? movie.getComments() : "");
        watchedCheck.setSelected(movie.isWatched());
        parentalCheck.setSelected(movie.isParentalRestriction());
    }

    private void selectPersonInCombo(JComboBox<PersonItem> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).id == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void save() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) { JOptionPane.showMessageDialog(this, "Title is required."); return; }
        int year;
        try { year = Integer.parseInt(yearField.getText().trim()); }
        catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "Invalid year."); return; }

        Movie m = movie != null ? movie : new Movie();
        m.setTitle(title);
        m.setReleaseYear(year);
        m.setLanguage(languageField.getText().trim());
        m.setCountry(countryField.getText().trim());
        m.setGenre((String) genreCombo.getSelectedItem());
        m.setDirectorId(((PersonItem) directorCombo.getSelectedItem()).id);
        m.setLeadActorId(((PersonItem) leadActorCombo.getSelectedItem()).id);
        m.setSupportActorId(((PersonItem) supportActorCombo.getSelectedItem()).id);
        m.setRating((int) ratingSpinner.getValue());
        m.setPoster(posterField.getText().trim());
        m.setAbout(aboutArea.getText().trim());
        m.setComments(commentsArea.getText().trim());
        m.setWatched(watchedCheck.isSelected());
        m.setParentalRestriction(parentalCheck.isSelected());

        boolean success = movie == null ? MovieDAO.saveMovie(m) : MovieDAO.updateMovie(m);
        if (success) {
            JOptionPane.showMessageDialog(this, "Movie saved successfully.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save movie.");
        }
    }

    static class PersonItem {
        int id;
        String name;
        PersonItem(int id, String name) { this.id = id; this.name = name; }
        @Override public String toString() { return name; }
    }
}
