package moviecritics;

public class User {
    private int userId;
    private String username;
    private String password;
    private int userType;
    private String email;

    public User() {}

    public User(int userId, String username, String password, int userType, String email) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.email = email;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getUserType() { return userType; }
    public void setUserType(int userType) { this.userType = userType; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isParent() { return userType == 1; }

    @Override
    public String toString() { return username; }
}
