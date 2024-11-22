package src.menus;

import src.managers.BookManager;
import src.models.Book;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AdminMenu extends Menu {
    private final BookManager bookManager = new BookManager();
    private final BookMenu bookMenu = new BookMenu(input);

    public AdminMenu(Scanner input) {
        super(input);
    }

    @Override
    public void displayMenu() {
        boolean exit = false;

        while (!exit) {
            displayTitle("Admin Menu");
            System.out.println("│ [1] Add a book                               │");
            System.out.println("│ [2] Delete a book                            │");
            System.out.println("│ [3] Edit a book                              │");
            System.out.println("│ [4] Search for a book                        │");
            System.out.println("├──────────────────────────────────────────────┤");
            System.out.println("│ [0] Exit                                     │");
            System.out.println("└──────────────────────────────────────────────┘");

            int choice = getUserChoice("\nEnter your choice: ", 4);

            switch (choice) {
                case 1 -> handleAddBook();
                case 2 -> handleDeleteBook();
                case 3 -> handleEditBook();
                case 4 -> handleSearchBook();
                case 0 -> exit = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void handleSearchBook(){
        System.out.println("\n┌──────────────────────────────────────────────┐");
        System.out.println("│               Search for a Book              │");
        System.out.println("└──────────────────────────────────────────────┘");
        Book book = searchBookByTitle();
        if (book != null) {
            viewBookDetails(book);
        }
    }

    private void handleAddBook() {
        System.out.println("\n┌──────────────────────────────────────────────┐");
        System.out.println("│                  Add a Book                  │");
        System.out.println("└──────────────────────────────────────────────┘");

        Book book = collectBookDetails();
        List<String> genres = selectTags("genre");
        List<String> moods = selectTags("mood");
        bookManager.insertBookIntoDatabase(book, genres, moods);
    }

    private void handleDeleteBook() {
        System.out.println("\n┌──────────────────────────────────────────────┐");
        System.out.println("│                 Delete a Book                │");
        System.out.println("└──────────────────────────────────────────────┘");
        Book book = searchBookByTitle();
        if (book != null) {
            bookManager.deleteBookFromDatabase(book.getBookId());
        }
    }

    private void handleEditBook() {
        System.out.println("\n┌──────────────────────────────────────────────┐");
        System.out.println("│            Search for a book to edit         │");
        System.out.println("└──────────────────────────────────────────────┘");
        Book book = searchBookByTitle();
        if (book != null) {
            editBookDetails(book.getBookId());
        }
    }

    private Book searchBookByTitle() {
        input.nextLine();
        System.out.print("Enter book title: ");
        String title = input.nextLine();

        List<Book> books = bookManager.searchByTitle(title);

        displayTitle("Search Results");
        if (books == null || books.isEmpty()) {
            System.out.println("│                No books found                │");
            System.out.println("└──────────────────────────────────────────────┘\n");
            System.out.print("Press enter to return.");
            input.nextLine();
            return  null;
        }

        for (int i = 0; i < books.size(); i++) {
            String title1 = books.get(i).getTitle();
            String author = books.get(i).getAuthor();
            String rating = books.get(i).getOverallRating();

            String bookInfo = "[" + (i + 1) + "] " + title1 + " by " + author;
            if (bookInfo.length() > 38) {
                bookInfo = bookInfo.substring(0, 35) + "...";
            }

            System.out.printf("│ %-44s │\n", bookInfo);
            System.out.printf("│    Average rating: %-25s │\n", rating);
            System.out.println("├──────────────────────────────────────────────┤");
        }
        System.out.println("│ [0] Exit                                     │");
        System.out.println("└──────────────────────────────────────────────┘");

        int choice = getUserChoice("\nSelect a book: ", books.size());

        return choice == 0 ? null : books.get(choice - 1);
    }

    private void viewBookDetails(Book book){
        boolean exit = false;

        while (!exit) {
            System.out.println("\n" + book.toString());
            System.out.println("│ [1] View Reviews                             │");
            System.out.println("├──────────────────────────────────────────────┤");
            System.out.println("│ [0] Exit                                     │");
            System.out.println("└──────────────────────────────────────────────┘");

            int choice = getUserChoice("\nEnter your choice: ", 1);

            switch (choice) {
                case 1 -> bookMenu.viewReviews(book.getBookId(), input);
                case 0 -> exit = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void editBookDetails(int bookId) {
        boolean exit = false;
        while (!exit) {
            displayTitle("Edit Book");
            System.out.println("│ What detail would you like to edit?          │");
            System.out.println("│ [1] Title                                    │");
            System.out.println("│ [2] Author                                   │");
            System.out.println("│ [3] Publication Date                         │");
            System.out.println("│ [4] Page Count                               │");
            System.out.println("│ [5] Synopsis                                 │");
            System.out.println("│ [6] Genres                                   │");
            System.out.println("│ [7] Moods                                    │");
            System.out.println("├──────────────────────────────────────────────┤");
            System.out.println("│ [0] Exit                                     │");
            System.out.println("└──────────────────────────────────────────────┘");

            int choice = getUserChoice("Enter your choice: ", 7);
            input.nextLine();

            if (choice == 0) {
                System.out.println("Exiting edit book details...");
                exit = true;
                continue;
            }

            String newValue;
            switch (choice) {
                case 1 -> {
                    System.out.print("Enter new title: ");
                    newValue = input.nextLine();
                    bookManager.updateBookInDatabase(bookId, choice, newValue);
                }
                case 2 -> {
                    System.out.print("Enter new author: ");
                    newValue = input.nextLine();
                    bookManager.updateBookInDatabase(bookId, choice, newValue);
                }
                case 3 -> {
                    newValue = getValidDate("Enter new publication date (YYYY-MM-DD): ");
                    bookManager.updateBookInDatabase(bookId, choice, newValue);
                }
                case 4 -> {
                    newValue = String.valueOf(getPageCount("Enter new page count: "));
                    bookManager.updateBookInDatabase(bookId, choice, newValue);
                }
                case 5 -> {
                    System.out.print("Enter new synopsis: ");
                    newValue = input.nextLine();
                    bookManager.updateBookInDatabase(bookId, choice, newValue);
                }
                case 6 -> {
                    List<String> genres = selectTags("genre");
                    bookManager.updateBookTagsInDatabase(bookId, "genre", genres);
                }
                case 7 -> {
                    List<String> moods = selectTags("mood");
                    bookManager.updateBookTagsInDatabase(bookId, "mood", moods);
                }
                default -> {
                    System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }

    private String getValidDate(String prompt){
        String publicationDate = "";
        while (true) {
            System.out.print(prompt);
            publicationDate = input.nextLine();
            try {
                LocalDate.parse(publicationDate);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("\nInvalid date format. Please enter a valid date in YYYY-MM-DD format.\n");
            }
        }
        return publicationDate;
    }

    private int getPageCount(String prompt){
        int pageCount;
        while (true) {
            System.out.print(prompt);
            try {
                pageCount = Integer.parseInt(input.nextLine());
                if (pageCount > 0) {
                    break;
                } else {
                    System.out.println("Page count must be a positive number. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid positive integer.");
            }
        }
        return pageCount;
    }

    private Book collectBookDetails() {
        input.nextLine();

        System.out.print("Enter title: ");
        String title = input.nextLine();

        System.out.print("Enter author: ");
        String author = input.nextLine();

        String publicationDate = getValidDate("Enter publication date (YYYY-MM-DD): ");

        int pageCount = getPageCount("Enter page count: ");

        System.out.print("Enter synopsis: ");
        String synopsis = input.nextLine();

        return new Book(0, title, author, publicationDate, pageCount, 0, synopsis);
    }

    private List<String> selectTags(String type) {
        List<String> availableTags = bookManager.getTagsByType(type);
        displayTags(availableTags, type);
        return selectTagsFromList(availableTags, type);
    }

    private void displayTags(List<String> tags, String type) {
        if (tags.isEmpty()) {
            System.out.println("No " + type + "s found in the system.");
            return;
        }

        displayTitle("Available " + type + "s:");
        for (int i = 0; i < tags.size(); i++) {
            System.out.printf("│ %-44s │\n", "[" + (i + 1) + "] " + tags.get(i));
        }
        System.out.println("└──────────────────────────────────────────────┘");
    }

    private List<String> selectTagsFromList(List<String> availableTags, String type) {
        List<String> selectedTags = new ArrayList<>();
        String[] selectedIndexes;

        if (availableTags.isEmpty()) {
            System.out.println("No " + type + "s found in the system.");
            return selectedTags;
        }

        while (true) {
            System.out.print("\nEnter the numbers of the " + type + "s you want to select (comma-separated): ");
            String inputLine = input.nextLine();

            try {
                selectedIndexes = inputLine.split(",\\s*");
                boolean allValid = true;

                for (String index : selectedIndexes) {
                    int parsedIndex = Integer.parseInt(index.trim());
                    if (parsedIndex < 1 || parsedIndex > availableTags.size()) {
                        System.out.println("\nInvalid input. Please enter numbers between 1 and " + availableTags.size() + ".");
                        allValid = false;
                        break;
                    }
                }
                if (allValid) {
                    break;
                }

            } catch (NumberFormatException e) {
                System.out.println("\nInvalid input. Please enter numbers separated by commas (e.g., 1, 2, 3).");
            }
        }

        for (String index : selectedIndexes) {
            int tagIndex = Integer.parseInt(index) - 1;
            selectedTags.add(availableTags.get(tagIndex));
        }

        return selectedTags;
    }

}
