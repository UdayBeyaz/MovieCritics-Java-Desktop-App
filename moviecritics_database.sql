-- MovieCritics Database Schema and Sample Data
-- Run this file in DB Browser for SQLite or any SQLite client

-- ============================================================
-- TABLE CREATION
-- ============================================================

CREATE TABLE IF NOT EXISTS Person (
    PersonID   INTEGER PRIMARY KEY AUTOINCREMENT,
    FirstName  VARCHAR(100) NOT NULL,
    LastName   VARCHAR(100) NOT NULL,
    DateOfBirth DATE,
    Nationality VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS Users (
    UserId    INTEGER PRIMARY KEY AUTOINCREMENT,
    Username  VARCHAR(100) NOT NULL UNIQUE,
    Password  VARCHAR(100) NOT NULL,
    UserType  INTEGER NOT NULL,
    Email     VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Movie (
    MovieID            INTEGER PRIMARY KEY AUTOINCREMENT,
    Title              VARCHAR(255) NOT NULL,
    ReleaseYear        INTEGER,
    Language           VARCHAR(100),
    Country            VARCHAR(100),
    Genre              VARCHAR(100),
    DirectorId         INTEGER,
    Watched            BOOLEAN DEFAULT 0,
    LeadActorId        INTEGER,
    SupportActorId     INTEGER,
    About              TEXT,
    Rating             INTEGER,
    Comments           TEXT,
    Poster             VARCHAR(255),
    ParentalRestriction BOOLEAN DEFAULT 0,
    FOREIGN KEY (DirectorId)     REFERENCES Person(PersonID),
    FOREIGN KEY (LeadActorId)    REFERENCES Person(PersonID),
    FOREIGN KEY (SupportActorId) REFERENCES Person(PersonID)
);

CREATE TABLE IF NOT EXISTS Watchlist (
    WatchlistID INTEGER PRIMARY KEY AUTOINCREMENT,
    UserId      INTEGER,
    MovieID     INTEGER,
    FOREIGN KEY (UserId)  REFERENCES Users(UserId),
    FOREIGN KEY (MovieID) REFERENCES Movie(MovieID)
);

CREATE TABLE IF NOT EXISTS UserRating (
    RatingID INTEGER PRIMARY KEY AUTOINCREMENT,
    UserId   INTEGER,
    MovieID  INTEGER,
    Score    INTEGER,
    Comment  TEXT,
    FOREIGN KEY (UserId)  REFERENCES Users(UserId),
    FOREIGN KEY (MovieID) REFERENCES Movie(MovieID)
);

-- ============================================================
-- SAMPLE DATA: Person (8 entries)
-- ============================================================

INSERT INTO Person (FirstName, LastName, DateOfBirth, Nationality) VALUES
('Christopher', 'Nolan',     '1970-07-30', 'British'),
('Steven',      'Spielberg', '1946-12-18', 'American'),
('Leonardo',    'DiCaprio',  '1974-11-11', 'American'),
('Scarlett',    'Johansson', '1984-11-22', 'American'),
('Cillian',     'Murphy',    '1976-05-25', 'Irish'),
('Tom',         'Hanks',     '1956-07-09', 'American'),
('Ridley',      'Scott',     '1937-11-30', 'British'),
('Morgan',      'Freeman',   '1937-06-01', 'American');

-- ============================================================
-- SAMPLE DATA: Users (5 entries)
-- UserType: 1 = Parent, 2 = Child
-- ============================================================

INSERT INTO Users (Username, Password, UserType, Email) VALUES
('admin',   'admin123', 1, 'admin@family.com'),
('parent2', 'pass123',  1, 'parent2@family.com'),
('child1',  'child123', 2, 'child1@family.com'),
('child2',  'child456', 2, 'child2@family.com'),
('child3',  'child789', 2, 'child3@family.com');

-- ============================================================
-- SAMPLE DATA: Movie (5 entries)
-- DirectorId / LeadActorId / SupportActorId refer to Person.PersonID
-- ============================================================

INSERT INTO Movie (Title, ReleaseYear, Language, Country, Genre, DirectorId, Watched, LeadActorId, SupportActorId, About, Rating, Comments, Poster, ParentalRestriction) VALUES
(
    'Inception', 2010, 'English', 'USA', 'Sci-Fi',
    1, 1, 3, 5,
    'A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.',
    9, 'A mind-bending masterpiece!', '', 0
),
(
    'Interstellar', 2014, 'English', 'USA', 'Sci-Fi',
    1, 0, 5, 6,
    'A team of explorers travel through a wormhole in space in an attempt to ensure humanity survival.',
    9, 'Stunning visuals and emotional story.', '', 0
),
(
    'Schindler''s List', 1993, 'English', 'USA', 'Drama',
    2, 1, 6, 8,
    'In German-occupied Poland during World War II, industrialist Oskar Schindler gradually becomes concerned for his Jewish workforce after witnessing their persecution by the Nazis.',
    10, 'Deeply moving. A must-watch.', '', 1
),
(
    'The Martian', 2015, 'English', 'USA', 'Sci-Fi',
    7, 0, 3, 4,
    'An astronaut becomes stranded on Mars after his team assumes him dead, and must rely on his ingenuity to find a way to signal to Earth that he is alive.',
    8, 'Great survival story!', '', 0
),
(
    'Saving Private Ryan', 1998, 'English', 'USA', 'War',
    2, 1, 6, 8,
    'Following the Normandy Landings, a group of U.S. soldiers go behind enemy lines to retrieve a paratrooper whose brothers have been killed in action.',
    9, 'Very emotional and realistic war film.', '', 1
);

-- ============================================================
-- SAMPLE DATA: UserRating (5 entries)
-- ============================================================

INSERT INTO UserRating (UserId, MovieID, Score, Comment) VALUES
(3, 1, 9,  'Loved the dream concept!'),
(4, 1, 8,  'Very confusing but great.'),
(3, 4, 7,  'Enjoyed the science parts.'),
(5, 4, 9,  'Best movie I have seen!'),
(4, 2, 10, 'Interstellar made me cry.');

-- ============================================================
-- SAMPLE DATA: Watchlist (5 entries)
-- ============================================================

INSERT INTO Watchlist (UserId, MovieID) VALUES
(3, 2),
(3, 4),
(4, 1),
(5, 2),
(5, 4);
