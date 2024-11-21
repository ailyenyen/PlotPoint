package src.models;

import java.sql.Timestamp;
import java.time.LocalDate;

public class User {
    private int userId;
    private String username;
    private String password;
    private LocalDate dateJoined;
    private boolean isAdmin; // Field to indicate if the user is an admin

    // Updated constructor to include the isAdmin parameter
    public User(int userId, String username, String password, Timestamp dateJoinedTimestamp, boolean isAdmin) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.dateJoined = dateJoinedTimestamp.toLocalDateTime().toLocalDate();
        this.isAdmin = isAdmin;
    }

    // Getter for userId
    public int getUserId() {
        return userId;
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Getter for dateJoined
    public LocalDate getDateJoined() {
        return dateJoined;
    }

    // Getter for isAdmin
    public boolean isAdmin() {
        return isAdmin;
    }
}
