package src.managers;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseHelper {
    private final String DB_URL = "jdbc:mysql://127.0.0.1:3306/book_tracker";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "Alohamora7";

    public Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
