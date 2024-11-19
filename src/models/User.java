package src.models;

import java.sql.*;
import java.time.LocalDate;

public class User {
    private int userId;
    private String username;
    private String password;
    private LocalDate dateJoined;

    public User(int userId, String username, String password, Timestamp dateJoinedTimestamp) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.dateJoined = dateJoinedTimestamp.toLocalDateTime().toLocalDate();
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public LocalDate getDateJoined() {return dateJoined;}
}
