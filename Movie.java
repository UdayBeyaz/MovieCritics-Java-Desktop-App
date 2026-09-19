package moviecritics;

public class Movie {
    private int movieID;
    private String title;
    private int releaseYear;
    private String language;
    private String country;
    private String genre;
    private int directorId;
    private boolean watched;
    private int leadActorId;
    private int supportActorId;
    private String about;
    private int rating;
    private String comments;
    private String poster;
    private boolean parentalRestriction;

    public Movie() {}

    public Movie(int movieID, String title, int releaseYear, String language, String country,
                 String genre, int directorId, boolean watched, int leadActorId, int supportActorId,
                 String about, int rating, String comments, String poster, boolean parentalRestriction) {
        this.movieID = movieID;
        this.title = title;
        this.releaseYear = releaseYear;
        this.language = language;
        this.country = country;
        this.genre = genre;
        this.directorId = directorId;
        this.watched = watched;
        this.leadActorId = leadActorId;
        this.supportActorId = supportActorId;
        this.about = about;
        this.rating = rating;
        this.comments = comments;
        this.poster = poster;
        this.parentalRestriction = parentalRestriction;
    }

    public int getMovieID() { return movieID; }
    public void setMovieID(int movieID) { this.movieID = movieID; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getReleaseYear() { return releaseYear; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public int getDirectorId() { return directorId; }
    public void setDirectorId(int directorId) { this.directorId = directorId; }
    public boolean isWatched() { return watched; }
    public void setWatched(boolean watched) { this.watched = watched; }
    public int getLeadActorId() { return leadActorId; }
    public void setLeadActorId(int leadActorId) { this.leadActorId = leadActorId; }
    public int getSupportActorId() { return supportActorId; }
    public void setSupportActorId(int supportActorId) { this.supportActorId = supportActorId; }
    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public String getPoster() { return poster; }
    public void setPoster(String poster) { this.poster = poster; }
    public boolean isParentalRestriction() { return parentalRestriction; }
    public void setParentalRestriction(boolean parentalRestriction) { this.parentalRestriction = parentalRestriction; }

    @Override
    public String toString() { return title + " (" + releaseYear + ")"; }
}
