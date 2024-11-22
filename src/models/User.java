package src.models;
import java.sql.Timestamp;
import java.time.LocalDate;

public class User {
    private int userId;
    private String username;
    private String password;
    private LocalDate dateJoined;
    private boolean isAdmin;

    public User(int userId, String username, String password, Timestamp dateJoinedTimestamp, boolean isAdmin) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.dateJoined = dateJoinedTimestamp.toLocalDateTime().toLocalDate();
        this.isAdmin = isAdmin;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public LocalDate getDateJoined() {
        return dateJoined;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
