package src.managers;

import src.models.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookManager {

    private final DatabaseHelper dbHelper = new DatabaseHelper();

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
        String query = """
        SELECT t.tag_name FROM tags t 
        JOIN book_tags bt ON t.tag_name = bt.tag_name 
        WHERE bt.book_id = ? AND t.tag_type = 'genre'
    """;

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
        JOIN tags t ON bt.tag_name = t.tag_name
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

    public List<String> getBookMoods(int bookId) {
        List<String> moods = new ArrayList<>();
        String query = """
        SELECT t.tag_name FROM tags t
        JOIN book_tags bt ON t.tag_name = bt.tag_name
        WHERE bt.book_id = ? AND t.tag_type = 'mood';
    """;

        try (Connection connection = dbHelper.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, bookId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                moods.add(resultSet.getString("tag_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return moods;
    }

    public int getRatingAndReviewCount(int bookId) {
        int reviewCount = 0;
        String query = "SELECT COUNT(*) AS review_count FROM reviews WHERE book_id = ?";

        try (Connection connection = dbHelper.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, bookId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                reviewCount = resultSet.getInt("review_count");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reviewCount;
    }


    public void updateBookInDatabase(int bookId, int detailChoice, String newValue) {
        String column = switch (detailChoice) {
            case 1 -> "title";
            case 2 -> "author";
            case 3 -> "publication_date";
            case 4 -> "page_count";
            case 5 -> "synopsis";
            default -> throw new IllegalArgumentException("Invalid choice");
        };

        try (Connection conn = dbHelper.connect()) {
            String sql = "UPDATE books SET " + column + " = ? WHERE book_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newValue);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
            System.out.println("Book updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error updating book: " + e.getMessage());
        }
    }

    public void updateBookTagsInDatabase(int bookId, String tagType, List<String> tags) {
        try (Connection conn = dbHelper.connect()) {
            String deleteSql = """
                DELETE bt
                FROM book_tags bt
                JOIN tags t ON bt.tag_name = t.tag_name
                WHERE bt.book_id = ? AND t.tag_type = ?
                """;
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, bookId);
                deleteStmt.setString(2, tagType);
                deleteStmt.executeUpdate();
            }
            String insertSql = "INSERT INTO book_tags (book_id, tag_name) VALUES (?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                for (String tag : tags) {
                    insertStmt.setInt(1, bookId);
                    insertStmt.setString(2, tag);
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }

            System.out.println(tagType.substring(0, 1).toUpperCase() + tagType.substring(1) + "s updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error updating " + tagType + "s: " + e.getMessage());
        }
    }


    public void deleteBookFromDatabase(int bookId) {
        try (Connection conn = dbHelper.connect()) {
            String sql = "DELETE FROM books WHERE book_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bookId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\nBook deleted successfully!");
            } else {
                System.out.println("No book found with the given ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting book: " + e.getMessage());
        }
    }

    public void insertBookIntoDatabase(Book book, List<String> genres, List<String> moods) {
        try (Connection conn = dbHelper.connect()) {
            conn.setAutoCommit(false);

            String bookSql = "INSERT INTO books (title, author, publication_date, page_count, synopsis) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement bookStmt = conn.prepareStatement(bookSql, PreparedStatement.RETURN_GENERATED_KEYS);
            bookStmt.setString(1, book.getTitle());
            bookStmt.setString(2, book.getAuthor());
            bookStmt.setString(3, book.getPublishedDate());
            bookStmt.setInt(4, book.getPageCount());
            bookStmt.setString(5, book.getSynopsis());
            bookStmt.executeUpdate();

            ResultSet generatedKeys = bookStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int bookId = generatedKeys.getInt(1);

                String tagInsertSql = "INSERT IGNORE INTO tags (tag_name, tag_type) VALUES (?, ?)";
                PreparedStatement tagStmt = conn.prepareStatement(tagInsertSql);

                for (String genre : genres) {
                    tagStmt.setString(1, genre);
                    tagStmt.setString(2, "genre");
                    tagStmt.addBatch();
                }

                for (String mood : moods) {
                    tagStmt.setString(1, mood);
                    tagStmt.setString(2, "mood");
                    tagStmt.addBatch();
                }

                tagStmt.executeBatch();

                String bookTagSql = "INSERT INTO book_tags (book_id, tag_name) VALUES (?, ?)";
                PreparedStatement bookTagStmt = conn.prepareStatement(bookTagSql);

                for (String genre : genres) {
                    bookTagStmt.setInt(1, bookId);
                    bookTagStmt.setString(2, genre);
                    bookTagStmt.addBatch();
                }

                for (String mood : moods) {
                    bookTagStmt.setInt(1, bookId);
                    bookTagStmt.setString(2, mood);
                    bookTagStmt.addBatch();
                }

                bookTagStmt.executeBatch();
            } else {
                throw new SQLException("Failed to retrieve the generated book ID.");
            }
            conn.commit();
            System.out.println("Book added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }


}
