package src.menus;
import src.managers.*;
import src.models.Book;
import src.models.User;
import java.util.List;
import java.util.Scanner;

public class ProfileMenu extends Menu {
    private final UserManager userManager = UserManager.getInstance();
    private final ShelfManager shelfManager = new ShelfManager();
    private final ReviewManager reviewManager = new ReviewManager();
    private final BookManager bookManager = new BookManager();
    private final BookMenu bookMenu = new BookMenu(input);

    public ProfileMenu(Scanner input) {
        super(input);
    }

    public void displayMenu() {
        User user = userManager.getLoggedInUser();
        if (user == null) {
            System.out.println("No user is logged in.");
            return;
        }

        boolean exit = false;
        while (!exit) {
            displayTitle("Welcome to your PlotPoint Profile!");
            System.out.printf("│ %-44s │\n", "Name: " + user.getUsername());
            System.out.printf("│ %-44s │\n", "Joined in: " + user.getDateJoined());
            System.out.printf("│ %-44s │\n", "Number of ratings and reviews: " + reviewManager.getTotalReviews(user.getUserId()));
            System.out.printf("│ %-44s │\n", "Average rating: " + String.format("%.2f", reviewManager.getAverageRating(user.getUserId())));
            System.out.printf("│ %-44s │\n", "Number of books read: "+ shelfManager.getReadShelfBookCount(user.getUserId()));
            System.out.println("├──────────────────────────────────────────────┤");
            System.out.println("│ [1] View your shelves                        │");
            System.out.println("│ [2] View your stats                          │");
            System.out.println("│ [3] View all rated books                     │");
            System.out.println("├──────────────────────────────────────────────┤");
            System.out.println("│ [0] Exit                                     │");
            System.out.println("└──────────────────────────────────────────────┘");

            int choice = getUserChoice("\nEnter your choice: ", 3);

            switch (choice) {
                case 1 -> viewShelves();
                case 2 -> viewStats();
                case 3 -> displayReviewedBooks(user.getUserId());
                case 0 -> exit = true;
                default -> System.out.println("Invalid choice. Please select an option from the menu.");
            }
        }
    }

    public void viewShelves() {
        boolean exit = false;

        while (!exit) {
            displayTitle("View Shelves");
            System.out.println("│ Choose a shelf to view:                      │");
            System.out.println("│ [1] Read                                     │");
            System.out.println("│ [2] Reading                                  │");
            System.out.println("│ [3] Want to Read                             │");
            System.out.println("├──────────────────────────────────────────────┤");
            System.out.println("│ [0] Exit                                     │");
            System.out.println("└──────────────────────────────────────────────┘");

            int choice = getUserChoice("\nEnter your choice: ", 3);
            String shelfName = switch (choice) {
                case 1 -> "Read";
                case 2 -> "Reading";
                case 3 -> "Want to Read";
                case 0 -> {
                    exit = true;
                    yield null;
                }
                default -> {
                    System.out.println("Invalid choice. Please select an option from the menu.");
                    yield null;
                }
            };

            if (shelfName != null) {
                int userId = userManager.getLoggedInUser().getUserId();
                List<Book> booksInShelf = shelfManager.getBooksInShelf(userId, shelfName);
                displayShelfBooks(booksInShelf, shelfName, userId);
            }
        }
    }

    private void displayReviewedBooks(int userId) {
        List<Book> reviewedBooks = bookManager.getReviewedBooks(userId);

        boolean exit = false;

        while (!exit) {
            displayTitle("Rated & Reviewed Books");

            if (reviewedBooks.isEmpty()) {
                System.out.printf("│ %-44s │\n", "No reviewed books.");
                System.out.println("└──────────────────────────────────────────────┘");
                System.out.print("\nPress enter to return.");
                input.nextLine();
                input.nextLine();
                return;
            }

            for (int i = 0; i < reviewedBooks.size(); i++) {
                Book book = reviewedBooks.get(i);
                String[] reviewDetails = reviewManager.getReviewDetails(book.getBookId(), userId);
                String reviewDate = reviewDetails[0];
                String userRating = reviewDetails[1];
                String reviewText = reviewDetails[2];
                String truncatedReviewText = reviewText.length() > 29 ? reviewText.substring(0, 29) + "..." : reviewText;

                System.out.printf("│ [%d] Title: %-33s │\n", i + 1, book.getTitle());
                System.out.printf("│     Author: %-32s │\n", book.getAuthor());
                System.out.printf("│     User Rating: %-27s │\n", userRating + "/5");
                System.out.printf("│     Review Date: %-27s │\n", reviewDate);
                System.out.printf("│     Review: %-32s │\n", truncatedReviewText);
                System.out.println("├──────────────────────────────────────────────┤");
            }

            System.out.println("│ [1] Select a book to view details            │");
            System.out.println("│ [2] Remove rating and review                 │");
            System.out.println("├──────────────────────────────────────────────┤");
            System.out.println("│ [0] Exit                                     │");
            System.out.println("└──────────────────────────────────────────────┘");

            int choice = getUserChoice("\nEnter your choice: ", 2);

            switch (choice) {
                case 1 -> {
                    int bookIndex = getUserChoice("Select book number to view details: ", reviewedBooks.size()) - 1;
                    if (bookIndex >= 0 && bookIndex < reviewedBooks.size()) {
                        Book selectedBook = reviewedBooks.get(bookIndex);
                        bookMenu.displayMenu(selectedBook);
                        return;
                    } else {
                        System.out.println("Invalid book selection.");
                    }
                }
                case 2 -> {
                    int bookIndex = getUserChoice("Select book number to remove: ", reviewedBooks.size()) - 1;
                    if (bookIndex >= 0 && bookIndex < reviewedBooks.size()) {
                        Book bookToRemove = reviewedBooks.get(bookIndex);
                        reviewManager.removeReview(bookToRemove.getBookId(), userId);
                        return;
                    } else {
                        System.out.println("Invalid book selection.");
                    }
                }
                case 0 -> exit = true;
                default -> System.out.println("Invalid choice. Please select an option from the menu.");
            }
        }
    }

    private void displayShelfBooks(List<Book> booksInShelf, String shelfName, int userId) {
        displayTitle(shelfName + " Shelf");

        if (booksInShelf.isEmpty()) {
            System.out.printf("│ %-44s │\n", "No books in this shelf.");
            System.out.println("└──────────────────────────────────────────────┘");
            System.out.print("\nPress enter to return.");
            input.nextLine();
            input.nextLine();
            return;
        }

        for (int i = 0; i < booksInShelf.size(); i++) {
            Book book = booksInShelf.get(i);
            String[] details = shelfManager.getBookDetailsInShelf(book.getBookId(), shelfName, userId);
            String dateAdded = details[0];
            String userRating = details[1];

            System.out.printf("│ [%d] Title: %-33s │\n", i + 1, book.getTitle());
            System.out.printf("│     Author: %-32s │\n", book.getAuthor());
            System.out.printf("│     User Rating: %-27s │\n", userRating + "/5");
            System.out.printf("│     Date Added: %-28s │\n", dateAdded);
            System.out.println("├──────────────────────────────────────────────┤");
        }

        viewShelfOptions(booksInShelf, userId, shelfName);
    }

    private void viewShelfOptions(List<Book> booksInShelf, int userId, String shelfName) {
        boolean exit = false;

        while (!exit) {
            System.out.println("│ [1] Select a book to view details            │");
            System.out.println("│ [2] Remove a book from this shelf            │");
            System.out.println("├──────────────────────────────────────────────┤");
            System.out.println("│ [0] Exit                                     │");
            System.out.println("└──────────────────────────────────────────────┘");

            int choice = getUserChoice("\nEnter your choice: ", 2);

            switch (choice) {
                case 1 -> {
                    int bookIndex = getUserChoice("Select book number to view details: ", booksInShelf.size()) - 1;
                    if (bookIndex >= 0 && bookIndex < booksInShelf.size()) {
                        Book selectedBook = booksInShelf.get(bookIndex);
                        bookMenu.displayMenu(selectedBook);
                        return;
                    } else {
                        System.out.println("Invalid book selection.");
                    }
                }
                case 2 -> {
                    int bookIndex = getUserChoice("Select book number to remove: ", booksInShelf.size()) - 1;
                    if (bookIndex >= 0 && bookIndex < booksInShelf.size()) {
                        Book bookToRemove = booksInShelf.get(bookIndex);
                        shelfManager.removeBookFromShelf(userId, bookToRemove.getBookId(), shelfName);
                        System.out.println("\nBook successfully removed from shelf.");
                        return;
                    } else {
                        System.out.println("Invalid book selection.");
                    }
                }
                case 0 -> exit = true;
                default -> System.out.println("Invalid choice. Please select an option from the menu.");
            }
        }
    }

    public void viewStats() {
        boolean exit = false;

        while (!exit) {
            displayTitle("Your Stats");
            System.out.println("│ Choose stats to view:                        │");
            System.out.println("│ [1] Most read genres                         │");
            System.out.println("│ [2] Books read per month                     │");
            System.out.println("│ [3] Pages read per month                     │");
            System.out.println("│ [4] Average rating per month                 │");
            System.out.println("├──────────────────────────────────────────────┤");
            System.out.println("│ [0] Exit                                     │");
            System.out.println("└──────────────────────────────────────────────┘");

            int choice = getUserChoice("\nEnter your choice: ", 4);

            switch (choice) {
                case 1 ->{
                    displayTitle("Most Read Genres");
                    shelfManager.showMostReadGenres(input);
                }
                case 2 -> {
                    displayTitle("Books read per month");
                    shelfManager.showBooksReadPerMonth(input);
                }
                case 3 -> {
                    displayTitle("Pages read per month");
                    shelfManager.showPagesReadPerMonth(input);
                }
                case 4 -> {
                    displayTitle("Average rating per month");
                    shelfManager.showAverageRatingPerMonth(input);
                }
                case 0 -> exit = true;
                default -> System.out.println("Invalid choice. Please select an option from the menu.");
            }
        }
    }
}
