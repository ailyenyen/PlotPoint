package src.managers;

import src.models.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReviewManager {
    private DatabaseHelper dbHelper = new DatabaseHelper();

    public boolean hasUserReviewedBook(int userId, int bookId) {
        String query = "SELECT COUNT(*) FROM reviews WHERE user_id = ? AND book_id = ?";
        try (Connection connection = dbHelper.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, bookId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void addRatingAndReview(int userId, int bookId, int rating, String review) {
        String query = "INSERT INTO reviews (user_id, book_id, rating, review_text) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE rating = ?, review_text = ?";

        try (Connection connection = dbHelper.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, bookId);
            preparedStatement.setInt(3, rating);
            preparedStatement.setString(4, review);
            preparedStatement.setInt(5, rating);  // For update on duplicate key
            preparedStatement.setString(6, review);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> loadReviews(int bookId) {
        List<String> reviews = new ArrayList<>();
        String query = "SELECT u.username, r.rating, r.review_text " +
                "FROM reviews r JOIN users u ON r.user_id = u.user_id " +
                "WHERE r.book_id = ?";

        try (Connection connection = dbHelper.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, bookId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                int rating = resultSet.getInt("rating");
                String reviewText = resultSet.getString("review_text");

                // Create the formatted review box
                StringBuilder sb = new StringBuilder();
                sb.append("┌──────────────────────────────────────────────┐\n");
                sb.append(String.format("│ %-44s │\n", "Username: " + username));
                sb.append("├──────────────────────────────────────────────┤\n");

                // Format the review with both rating and review text side-by-side
                int lineWidth = 44;
                String[] words = reviewText.split(" ");
                StringBuilder line = new StringBuilder("│ Rating & Review: " + rating + "/5 - ");

                for (String word : words) {
                    if (line.length() + word.length() + 1 > lineWidth) {
                        sb.append(String.format("%-47s│\n", line.toString()));
                        line = new StringBuilder("│ " + word + " ");
                    } else {
                        line.append(word).append(" ");
                    }
                }
                sb.append(String.format("%-47s│\n", line.toString()));
                sb.append("└──────────────────────────────────────────────┘");

                // Add the formatted review to the list
                reviews.add(sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reviews;
    }

    public int getTotalReviews(int userId) {
        String query = "SELECT COUNT(*) FROM reviews WHERE user_id = ?";
        int totalReviews = 0;

        try (Connection conn = dbHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                totalReviews = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalReviews;
    }

    public double getAverageRating(int userId) {
        String query = "SELECT AVG(rating) FROM reviews WHERE user_id = ?";
        double averageRating = 0.0;

        try (Connection conn = dbHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                averageRating = rs.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return averageRating;
    }

    public String[] getReviewDetails(int bookId, int userId) {
        String[] reviewDetails = new String[3];
        String query = """
        SELECT rating, DATE_FORMAT(date, '%Y-%m-%d') AS review_date, review_text
        FROM reviews
        WHERE book_id = ? AND user_id = ?
    """;

        try (Connection conn = dbHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, bookId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                reviewDetails[0] = rs.getString("review_date");
                reviewDetails[1] = rs.getString("rating");
                reviewDetails[2] = rs.getString("review_text");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reviewDetails;
    }


    public void removeReview(int bookId, int userId) {
        String query = "DELETE FROM reviews WHERE book_id = ? AND user_id = ?";

        try (Connection conn = dbHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, bookId);
            stmt.setInt(2, userId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Review successfully removed.");
            } else {
                System.out.println("Review not found or could not be removed.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred while removing the review.");
            e.printStackTrace();
        }
    }

}
