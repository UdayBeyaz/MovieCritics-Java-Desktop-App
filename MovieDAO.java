package moviecritics;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    public static Movie getMovieById(int id) {
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Movie WHERE MovieID = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Movie m = new Movie(
                        rs.getInt("MovieID"), rs.getString("Title"), rs.getInt("ReleaseYear"),
                        rs.getString("Language"), rs.getString("Country"), rs.getString("Genre"),
                        rs.getInt("DirectorId"), rs.getBoolean("Watched"),
                        rs.getInt("LeadActorId"), rs.getInt("SupportActorId"),
                        rs.getString("About"), rs.getInt("Rating"),
                        rs.getString("Comments"), rs.getString("Poster"),
                        rs.getBoolean("ParentalRestriction")
                );
                rs.close(); ps.close();
                return m;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Movie> getAllMovies(int userType) {
        List<Movie> list = new ArrayList<>();
        String sql = (userType == 2) ? "SELECT * FROM Movie WHERE ParentalRestriction = 0" : "SELECT * FROM Movie";

        try {
            Connection conn = DatabaseManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                list.add(new Movie(
                        rs.getInt("MovieID"), rs.getString("Title"), rs.getInt("ReleaseYear"),
                        rs.getString("Language"), rs.getString("Country"), rs.getString("Genre"),
                        rs.getInt("DirectorId"), rs.getBoolean("Watched"),
                        rs.getInt("LeadActorId"), rs.getInt("SupportActorId"),
                        rs.getString("About"), rs.getInt("Rating"),
                        rs.getString("Comments"), rs.getString("Poster"),
                        rs.getBoolean("ParentalRestriction")
                ));
            }
            rs.close(); stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean saveMovie(Movie movie) {
        String sql = "INSERT INTO Movie (Title, ReleaseYear, Language, Country, Genre, DirectorId, Watched, LeadActorId, SupportActorId, About, Rating, Comments, Poster, ParentalRestriction) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, movie.getTitle());
            ps.setInt(2, movie.getReleaseYear());
            ps.setString(3, movie.getLanguage());
            ps.setString(4, movie.getCountry());
            ps.setString(5, movie.getGenre());
            ps.setInt(6, movie.getDirectorId());
            ps.setBoolean(7, movie.isWatched());
            ps.setInt(8, movie.getLeadActorId());
            ps.setInt(9, movie.getSupportActorId());
            ps.setString(10, movie.getAbout());
            ps.setInt(11, movie.getRating());
            ps.setString(12, movie.getComments());
            ps.setString(13, movie.getPoster());
            ps.setBoolean(14, movie.isParentalRestriction());
            boolean result = ps.executeUpdate() > 0;
            ps.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Person> getAllPersons() {
        List<Person> list = new ArrayList<>();
        try {
            Connection conn = DatabaseManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Person ORDER BY FirstName");
            while (rs.next()) {
                list.add(new Person(rs.getInt("PersonID"), rs.getString("FirstName"),
                        rs.getString("LastName"), rs.getString("DateOfBirth"), rs.getString("Nationality")));
            }
            rs.close(); stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String getPersonName(int id) {
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT FirstName || ' ' || LastName FROM Person WHERE PersonID = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String name = rs.getString(1);
                rs.close(); ps.close();
                return name;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }
    public static boolean updateMovie(Movie movie) {
        String sql = """
            UPDATE Movie SET Title=?, ReleaseYear=?, Language=?, Country=?, Genre=?,
                DirectorId=?, Watched=?, LeadActorId=?, SupportActorId=?, About=?,
                Rating=?, Comments=?, Poster=?, ParentalRestriction=?
            WHERE MovieID=?
        """;
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, movie.getTitle());
            ps.setInt(2, movie.getReleaseYear());
            ps.setString(3, movie.getLanguage());
            ps.setString(4, movie.getCountry());
            ps.setString(5, movie.getGenre());
            ps.setInt(6, movie.getDirectorId());
            ps.setBoolean(7, movie.isWatched());
            ps.setInt(8, movie.getLeadActorId());
            ps.setInt(9, movie.getSupportActorId());
            ps.setString(10, movie.getAbout());
            ps.setInt(11, movie.getRating());
            ps.setString(12, movie.getComments());
            ps.setString(13, movie.getPoster());
            ps.setBoolean(14, movie.isParentalRestriction());
            ps.setInt(15, movie.getMovieID());
            boolean result = ps.executeUpdate() > 0;
            ps.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
