package moviecritics;

import java.sql.*;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:moviecritics.db";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {

        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Person (
                    PersonID INTEGER PRIMARY KEY AUTOINCREMENT,
                    FirstName VARCHAR(100) NOT NULL,
                    LastName VARCHAR(100) NOT NULL,
                    DateOfBirth DATE,
                    Nationality VARCHAR(100)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Users (
                    UserId INTEGER PRIMARY KEY AUTOINCREMENT,
                    Username VARCHAR(100) NOT NULL UNIQUE,
                    Password VARCHAR(100) NOT NULL,
                    UserType INTEGER NOT NULL,
                    Email VARCHAR(255)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Movie (
                    MovieID INTEGER PRIMARY KEY AUTOINCREMENT,
                    Title VARCHAR(255) NOT NULL,
                    ReleaseYear INTEGER,
                    Language VARCHAR(100),
                    Country VARCHAR(100),
                    Genre VARCHAR(100),
                    DirectorId INTEGER,
                    Watched BOOLEAN DEFAULT 0,
                    LeadActorId INTEGER,
                    SupportActorId INTEGER,
                    About TEXT,
                    Rating INTEGER,
                    Comments TEXT,
                    Poster VARCHAR(255),
                    ParentalRestriction BOOLEAN DEFAULT 0,
                    FOREIGN KEY (DirectorId) REFERENCES Person(PersonID),
                    FOREIGN KEY (LeadActorId) REFERENCES Person(PersonID),
                    FOREIGN KEY (SupportActorId) REFERENCES Person(PersonID)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Watchlist (
                    WatchlistID INTEGER PRIMARY KEY AUTOINCREMENT,
                    UserId INTEGER,
                    MovieID INTEGER,
                    FOREIGN KEY (UserId) REFERENCES Users(UserId),
                    FOREIGN KEY (MovieID) REFERENCES Movie(MovieID)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS UserRating (
                    RatingID INTEGER PRIMARY KEY AUTOINCREMENT,
                    UserId INTEGER,
                    MovieID INTEGER,
                    Score INTEGER,
                    Comment TEXT,
                    FOREIGN KEY (UserId) REFERENCES Users(UserId),
                    FOREIGN KEY (MovieID) REFERENCES Movie(MovieID)
                )
            """);

            insertSampleData(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertSampleData(Connection conn) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM Users");
        if (rs.next() && rs.getInt(1) > 0) return;

        conn.createStatement().execute("""
            INSERT INTO Person (FirstName, LastName, DateOfBirth, Nationality) VALUES
            ('Christopher', 'Nolan', '1970-07-30', 'British'),
            ('Steven', 'Spielberg', '1946-12-18', 'American'),
            ('Leonardo', 'DiCaprio', '1974-11-11', 'American'),
            ('Scarlett', 'Johansson', '1984-11-22', 'American'),
            ('Cillian', 'Murphy', '1976-05-25', 'Irish'),
            ('Tom', 'Hanks', '1956-07-09', 'American'),
            ('Ridley', 'Scott', '1937-11-30', 'British'),
            ('Morgan', 'Freeman', '1937-06-01', 'American')
        """);

        conn.createStatement().execute("""
            INSERT INTO Movie (Title, ReleaseYear, Language, Country, Genre, DirectorId, Watched, LeadActorId, SupportActorId, About, Rating, Comments, Poster, ParentalRestriction) VALUES
            ('Inception', 2010, 'English', 'USA', 'Sci-Fi', 1, 1, 3, 5, 'A thief who steals corporate secrets through dream-sharing technology.', 9, 'Masterpiece!', '', 0),
            ('Interstellar', 2014, 'English', 'USA', 'Sci-Fi', 1, 0, 5, 6, 'A team of explorers travel through a wormhole in space.', 9, 'Stunning visuals.', '', 0),
            ('Schindler''s List', 1993, 'English', 'USA', 'Drama', 2, 1, 6, 8, 'A businessman saves Jewish lives during the Holocaust.', 10, 'Deeply moving.', '', 1),
            ('The Martian', 2015, 'English', 'USA', 'Sci-Fi', 7, 0, 3, 4, 'An astronaut becomes stranded on Mars and must survive.', 8, 'Great story!', '', 0),
            ('Saving Private Ryan', 1998, 'English', 'USA', 'War', 2, 1, 6, 8, 'A WWII soldier is rescued by a group of soldiers.', 9, 'Very emotional.', '', 1)
        """);

        conn.createStatement().execute("""
            INSERT INTO Users (Username, Password, UserType, Email) VALUES
            ('admin', 'admin123', 1, 'admin@family.com'),
            ('parent2', 'pass123', 1, 'parent2@family.com'),
            ('child1', 'child123', 2, 'child1@family.com'),
            ('child2', 'child456', 2, 'child2@family.com'),
            ('child3', 'child789', 2, 'child3@family.com')
        """);
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
