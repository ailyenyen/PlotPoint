package src.managers;

import src.models.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BookManager {

    private DatabaseHelper dbHelper = new DatabaseHelper();

    public List<Book> searchByTitle(String title) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books WHERE title LIKE ?";

        try (Connection connection = dbHelper.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, "%" + title + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                books.add(extractBookFromResultSet(resultSet));
            }
        } catch (Exception e) {
            System.err.println("Error searching books by title: " + e.getMessage());
        }

        return books;
    }

    public List<Book> searchByAuthor(String author) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books WHERE author LIKE ?";

        try (Connection connection = dbHelper.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, "%" + author + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                books.add(extractBookFromResultSet(resultSet));
            }
        } catch (Exception e) {
            System.err.println("Error searching books by author: " + e.getMessage());
        }

        return books;
    }

    private Book extractBookFromResultSet(ResultSet resultSet) throws Exception {
        int bookId = resultSet.getInt("book_id");
        String title = resultSet.getString("title");
        String author = resultSet.getString("author");
        String publishedDate = resultSet.getString("publication_date");
        int pageCount = resultSet.getInt("page_count");
        double overallRating = resultSet.getDouble("average_rating");
        String synopsis = resultSet.getString("synopsis");

        return new Book(bookId, title, author, publishedDate, pageCount, overallRating, synopsis);
    }

    public List<String> loadGenres(int bookId) {
        List<String> genres = new ArrayList<>();
        String query = "SELECT tag_name FROM tags t JOIN book_tags bt ON t.tag_id = bt.tag_id WHERE bt.book_id = ? AND t.tag_type = 'genre'";

        try (Connection connection = dbHelper.connect();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, bookId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                genres.add(resultSet.getString("tag_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return genres;
    }

    public List<String> getTagsByType(String type) {
        List<String> tags = new ArrayList<>();
        String query = "SELECT tag_name FROM tags WHERE tag_type = ? ORDER BY tag_name";
        
        try (Connection connection = dbHelper.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    
            preparedStatement.setString(1, type);
            ResultSet resultSet = preparedStatement.executeQuery();
    
            while (resultSet.next()) {
                tags.add(resultSet.getString("tag_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return tags;
    }
    
    public List<Book> getRecommendedBooksByTag(String tagName) {
        List<Book> books = new ArrayList<>();
        String query = """
            SELECT b.* FROM books b
            JOIN book_tags bt ON b.book_id = bt.book_id
            JOIN tags t ON bt.tag_id = t.tag_id
            WHERE t.tag_name = ?
            ORDER BY b.average_rating DESC, b.book_id ASC
        """;
    
        try (Connection connection = dbHelper.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    
            preparedStatement.setString(1, tagName);
            ResultSet resultSet = preparedStatement.executeQuery();
    
            while (resultSet.next()) {
                books.add(extractBookFromResultSet(resultSet));
            }
        } catch (Exception e) {
            System.err.println("Error retrieving recommendations: " + e.getMessage());
        }
    
        return books;
    }

    public List<Book> getReviewedBooks(int userId) {
        List<Book> reviewedBooks = new ArrayList<>();
        String query = """
            SELECT b.book_id, b.title, b.author, b.publication_date, b.page_count, b.synopsis, b.average_rating
            FROM books b
            JOIN reviews r ON b.book_id = r.book_id
            WHERE r.user_id = ?
        """;

        try (Connection conn = dbHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Book book = extractBookFromResultSet(rs);
                reviewedBooks.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reviewedBooks;
    }
}
