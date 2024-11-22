package src.managers;
import src.models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserManager {

    private static UserManager instance;
    private final DatabaseHelper dbHelper = new DatabaseHelper();
    private User loggedInUser;

    private UserManager() {}

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public User verifyCredentials(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection connection = dbHelper.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                loggedInUser = new User(
                        resultSet.getInt("user_id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getTimestamp("date_joined"),
                        resultSet.getBoolean("is_admin")
                );
                return loggedInUser;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean validUsername(String username) {
        String query = "SELECT username FROM users WHERE username = ?";
        try (Connection connection = dbHelper.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public void addUser(String username, String password, boolean isAdmin) {
        String query = "INSERT INTO users (username, password, is_admin) VALUES (?, ?, ?)";

        try (Connection connection = dbHelper.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setBoolean(3, isAdmin);

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
