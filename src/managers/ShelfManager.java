package src.managers;

import src.models.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ShelfManager {
    private final DatabaseHelper dbHelper = new DatabaseHelper();
    private final UserManager userManager = UserManager.getInstance();

    public List<String> getUserShelves(int userId) {
        List<String> shelves = new ArrayList<>();
        String query = "SELECT shelf_name FROM shelves WHERE user_id = ?";

        try (Connection connection = dbHelper.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                shelves.add(resultSet.getString("shelf_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shelves;
    }

    public String isBookInAnyShelf(int userId, int bookId) {
        String query = """
        SELECT s.shelf_name
        FROM shelf_books sb
        JOIN shelves s ON sb.shelf_id = s.shelf_id
        WHERE sb.book_id = ? AND s.user_id = ?
    """;

        try (Connection connection = dbHelper.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, bookId);
            preparedStatement.setInt(2, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("shelf_name");
            }
        } catch (SQLException e) {
            System.err.println("Error checking book in shelves: " + e.getMessage());
        }
        return null;
    }

    public void addBookToShelf(int userId, Book book, String shelfName, Scanner input) {

        String existingShelf = isBookInAnyShelf(userId, book.getBookId());

        if (existingShelf != null && !existingShelf.equals(shelfName)) {
            removeBookFromShelf(userId, book.getBookId(), existingShelf);
        }

        String shelfQuery = "SELECT shelf_id FROM shelves WHERE user_id = ? AND shelf_name = ?";
        String insertQuery = "INSERT INTO shelf_books (shelf_id, book_id) VALUES (?, ?)";

        try (Connection connection = dbHelper.connect();
             PreparedStatement shelfStmt = connection.prepareStatement(shelfQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

            shelfStmt.setInt(1, userId);
            shelfStmt.setString(2, shelfName);
            ResultSet resultSet = shelfStmt.executeQuery();

            if (resultSet.next()) {
                int shelfId = resultSet.getInt("shelf_id");

                insertStmt.setInt(1, shelfId);
                insertStmt.setInt(2, book.getBookId());
                insertStmt.executeUpdate();
                System.out.println("Book added to your " + shelfName + " shelf!");
                System.out.print("Press enter to return.");
                input.nextLine();
                input.nextLine();
            } else {
                System.out.println("Shelf not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Book> getBooksInShelf(int userId, String shelfName) {
        List<Book> booksInShelf = new ArrayList<>();
        String query = """
            SELECT b.book_id, b.title, b.author, b.publication_date, b.page_count, b.average_rating, b.synopsis
            FROM books b
            JOIN shelf_books sb ON b.book_id = sb.book_id
            JOIN shelves s ON sb.shelf_id = s.shelf_id
            WHERE s.user_id = ? AND s.shelf_name = ?
        """;

        try (Connection connection = dbHelper.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, shelfName);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int bookId = resultSet.getInt("book_id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String publicationDate = resultSet.getString("publication_date");
                int pageCount = resultSet.getInt("page_count");
                double averageRating = resultSet.getDouble("average_rating");
                String synopsis = resultSet.getString("synopsis");

                Book book = new Book(bookId, title, author, publicationDate, pageCount, averageRating, synopsis);
                booksInShelf.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return booksInShelf;
    }

    public String[] getBookDetailsInShelf(int bookId, String shelfName, int userId) {
        String dateAdded = "";
        String userRating = " - ";
        String query = """
        SELECT sb.date_added, r.rating
        FROM shelf_books sb
        JOIN shelves s ON sb.shelf_id = s.shelf_id
        LEFT JOIN reviews r ON sb.book_id = r.book_id AND s.user_id = r.user_id
        WHERE sb.book_id = ? AND s.shelf_name = ? AND s.user_id = ?;
    """;

        try (Connection conn = dbHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, bookId);
            stmt.setString(2, shelfName);
            stmt.setInt(3, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dateAdded = rs.getTimestamp("date_added").toString();
                    userRating = rs.getString("rating") != null ? rs.getString("rating") : " - ";
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching book details in shelf: " + e.getMessage());
        }

        return new String[]{dateAdded, userRating};
    }

    public void removeBookFromShelf(int userId, int bookId, String shelfName) {
        String query = """
        DELETE sb
        FROM shelf_books sb
        JOIN shelves s ON sb.shelf_id = s.shelf_id
        WHERE sb.book_id = ? AND s.user_id = ? AND s.shelf_name = ?
    """;

        try (Connection connection = dbHelper.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, bookId);
            preparedStatement.setInt(2, userId);
            preparedStatement.setString(3, shelfName);

            int rowsAffected = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error removing book from shelf: " + e.getMessage());
        }
    }

    public void showMostReadGenres(Scanner input) {
        String query = """
        SELECT tags.tag_name, COUNT(*) AS count
        FROM shelf_books
        JOIN shelves ON shelf_books.shelf_id = shelves.shelf_id
        JOIN book_tags ON shelf_books.book_id = book_tags.book_id
        JOIN tags ON book_tags.tag_name = tags.tag_name
        WHERE shelves.shelf_name = 'Read' AND tags.tag_type = 'genre' AND shelves.user_id = ?
        GROUP BY tags.tag_name
        ORDER BY count DESC;
    """;

        try (Connection connection = dbHelper.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userManager.getLoggedInUser().getUserId());
            ResultSet rs = stmt.executeQuery();

            // Calculate total reads
            int totalReads = 0;
            List<String[]> genreData = new ArrayList<>();
            while (rs.next()) {
                int count = rs.getInt("count");
                totalReads += count;
                genreData.add(new String[]{rs.getString("tag_name"), String.valueOf(count)});
            }

            // Output formatted genre data
            for (int i = 0; i < genreData.size(); i++) {
                String tagName = genreData.get(i)[0];
                int count = Integer.parseInt(genreData.get(i)[1]);
                double percentage = (totalReads > 0) ? (count / (double) totalReads) * 100 : 0;

                // Format output for read count
                String readText = count == 1 ? "read" : "reads";
                String genreLine = String.format("│ [%d] %-18s %3d %-8s %7.1f%% │",
                        (i + 1), tagName, count, readText, percentage);

                System.out.println(genreLine);
            }
            System.out.println("└──────────────────────────────────────────────┘");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.print("\n Press enter to return.");
        input.nextLine();
        input.nextLine();
    }




    public void showBooksReadPerMonth(Scanner input) {
        String query = """
        SELECT DATE_FORMAT(date_added, '%Y-%m') AS month, COUNT(*) AS count
        FROM shelf_books
        JOIN shelves ON shelf_books.shelf_id = shelves.shelf_id
        WHERE shelves.shelf_name = 'Read' AND shelves.user_id = ?
        GROUP BY month
        ORDER BY month;
    """;
        try (Connection connection = dbHelper.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userManager.getLoggedInUser().getUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String month = rs.getString("month");
                int count = rs.getInt("count");
                String bookText = count == 1 ? "book" : "books";

                // Print each month entry formatted to fit within the box
                System.out.printf("│  %-10s -   %-3d %-5s                    │\n", month, count, bookText);
            }

            System.out.println("└──────────────────────────────────────────────┘");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.print("\nPress enter to return.");
        input.nextLine();
        input.nextLine();
    }


    public void showPagesReadPerMonth(Scanner input) {
        String query = """
        SELECT DATE_FORMAT(date_added, '%Y-%m') AS month, SUM(books.page_count) AS pages
        FROM shelf_books
        JOIN shelves ON shelf_books.shelf_id = shelves.shelf_id
        JOIN books ON shelf_books.book_id = books.book_id
        WHERE shelves.shelf_name = 'Read' AND shelves.user_id = ?
        GROUP BY month
        ORDER BY month;
    """;
        try (Connection connection = dbHelper.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userManager.getLoggedInUser().getUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String month = rs.getString("month");
                int pages = rs.getInt("pages");

                // Print each month entry formatted to fit within the box
                System.out.printf("│ %-10s -  %-4d pages                     │\n", month, pages);
            }

            System.out.println("└──────────────────────────────────────────────┘");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.print("\nPress enter to return.");
        input.nextLine();
        input.nextLine();
    }

    public void showAverageRatingPerMonth(Scanner input) {
        String query = """
        SELECT DATE_FORMAT(reviews.date, '%Y-%m') AS month, AVG(reviews.rating) AS avg_rating
        FROM reviews
        JOIN shelves ON reviews.user_id = shelves.user_id
        JOIN shelf_books ON shelves.shelf_id = shelf_books.shelf_id
        WHERE shelves.shelf_name = 'Read' AND shelves.user_id = ?
        GROUP BY month
        ORDER BY month;
    """;
        try (Connection connection = dbHelper.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userManager.getLoggedInUser().getUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String month = rs.getString("month");
                double avgRating = rs.getDouble("avg_rating");

                // Print each month entry formatted to fit within the box
                System.out.printf("│ %-10s -   %-4.2f rating                   │\n", month, avgRating);
            }

            System.out.println("└──────────────────────────────────────────────┘");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.print("\nPress enter to return.");
        input.nextLine();
        input.nextLine();
    }

    public int getReadShelfBookCount(int userId) {
        int bookCount = 0;
        String query = """
            SELECT COUNT(sb.book_id) AS book_count
            FROM shelves s
            JOIN shelf_books sb ON s.shelf_id = sb.shelf_id
            WHERE s.user_id = ? AND s.shelf_name = 'Read'
        """;

        try (Connection conn = dbHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                bookCount = rs.getInt("book_count");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bookCount;
    }
}
