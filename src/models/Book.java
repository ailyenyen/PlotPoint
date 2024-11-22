package src.models;
import java.util.List;
import src.managers.BookManager;

public class Book {
    private final BookManager bookManager = new BookManager();

    private int bookId;
    private String title;
    private String author;
    private String publishedDate;
    private int pageCount;
    private double overallRating;
    private List<String> genres;
    private String synopsis;
    private  List<String> moods;
    private int reviewCount;

    public Book(int bookId, String title, String author, String publishedDate, int pageCount, double overallRating, String synopsis) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.publishedDate = publishedDate;
        this.pageCount = pageCount;
        this.overallRating = overallRating;
        this.synopsis = synopsis;
        this.genres = bookManager.loadGenres(bookId);
        this.moods = bookManager.getBookMoods(bookId);
        this.reviewCount = bookManager.getRatingAndReviewCount(bookId);
    }

    private String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text + " ".repeat(Math.max(0, width - text.length() - padding));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("┌──────────────────────────────────────────────┐\n");
        sb.append("│ ").append(centerText(title, 44)).append(" │\n");
        sb.append("├──────────────────────────────────────────────┤\n");
        sb.append(String.format("│ %-44s │\n", "Author: " + author));
        sb.append(String.format("│ %-44s │\n", "Published Date: " + publishedDate));
        sb.append(String.format("│ %-44s │\n", "Page Count: " + pageCount));
        sb.append(String.format("│ %-44s │\n", String.format("Overall Rating: %.2f", overallRating)));
        sb.append(String.format("│ %-44s │\n", "Genres: " + String.join(", ", genres)));
        sb.append(String.format("│ %-44s │\n", "Moods: " + String.join(", ", moods)));
        sb.append(String.format("│ %-44s │\n", "Reviews: " + reviewCount + " reviews"));
        sb.append("├──────────────────────────────────────────────┤\n");
        sb.append("│ Synopsis:                                    │\n");

        String[] synopsisLines = synopsis.split("(?<=\\G.{42})");
        for (String line : synopsisLines) {
            sb.append(String.format("│ %-44s │\n", line));
        }

        sb.append("├──────────────────────────────────────────────┤");

        return sb.toString();
    }

    public int getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public int getPageCount() {
        return pageCount;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getOverallRating() {
        return String.format("%.2f", overallRating);
    }
}
