package src.menus;
import src.managers.ReviewManager;
import src.managers.ShelfManager;
import src.managers.UserManager;
import src.models.Book;
import src.models.User;
import java.util.List;
import java.util.Scanner;

public class BookMenu extends Menu {

    private final UserManager userManager = UserManager.getInstance();
    private final ShelfManager shelfManager = new ShelfManager();
    private final ReviewManager reviewManager = new ReviewManager();

    public BookMenu(Scanner input) {
        super(input);
    }

    @Override
    public void displayMenu(Book book) {
        boolean exit = false;
        User loggedInUser = userManager.getLoggedInUser();

        if (loggedInUser == null) {
            System.out.println("Error: No user logged in.");
            return;
        }

        while (!exit) {
            System.out.println("\n" + book.toString());
            System.out.println("│ [1] Add to Shelf                             │");
            System.out.println("│ [2] Rate and Review                          │");
            System.out.println("│ [3] View Reviews                             │");
            System.out.println("├──────────────────────────────────────────────┤");
            System.out.println("│ [0] Exit                                     │");
            System.out.println("└──────────────────────────────────────────────┘");

            int choice = getUserChoice("\nEnter your choice: ", 3);

            switch (choice) {
                case 1 -> {
                    String selectedShelf = selectShelf(input, loggedInUser.getUserId());
                    if (selectedShelf != null) {
                        shelfManager.addBookToShelf(loggedInUser.getUserId(), book, selectedShelf, input);
                    }
                }
                case 2 -> rateAndReviewBook(loggedInUser.getUserId(), book, input);
                case 3 -> viewReviews(book.getBookId(), input);
                case 0 -> exit = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    @Override
    public void displayMenu() {
        System.out.println("This menu requires a Book parameter to display.");
    }

    public String selectShelf(Scanner input, int userId) {
        List<String> shelves = shelfManager.getUserShelves(userId);
        displayTitle("Your Shelves");
        System.out.println("│ Choose a shelf to add book to:               │");

        if (shelves.isEmpty()) {
            System.out.println("│ No shelves available.                       │");
        } else {
            for (int i = 0; i < shelves.size(); i++) {
                String shelfDisplay = String.format("[%d] %-40s", i + 1, shelves.get(i));
                System.out.println("│ " + shelfDisplay + " │");
            }
        }
        System.out.println("├──────────────────────────────────────────────┤");
        System.out.println("│ [0] Exit                                     │");
        System.out.println("└──────────────────────────────────────────────┘");

        int choice = getUserChoice("\nSelect a shelf: ", shelves.size());

        if (choice > 0 && choice <= shelves.size()) {
            return shelves.get(choice - 1);
        } else {
            return null;
        }
    }

    public void rateAndReviewBook(int userId, Book book, Scanner input) {
        boolean hasReviewed = reviewManager.hasUserReviewedBook(userId, book.getBookId());
        displayTitle("Rate and Review");
        if (hasReviewed) {
            input.nextLine();
            System.out.println("│ You have already rated and reviewed this book│");
            System.out.println("│ [1] Update rating and review                 │");
            System.out.println("│ [2] Cancel                                   │");
            System.out.println("└──────────────────────────────────────────────┘");
            int response = getUserChoice("\nEnter your choice: ", 2);
            if (response == 1) {}
            else if (response == 2) {return;}
        }

        int rating = getUserChoice("\nRate this book out of 5: ", 5);
        input.nextLine();

        System.out.print("Write your review: ");
        String review = input.nextLine();

        reviewManager.addRatingAndReview(userId, book.getBookId(), rating, review);
        System.out.println("\nThank you for rating and reviewing the book!");
    }

    public void viewReviews(int bookId, Scanner input) {
        List<String> reviews = reviewManager.loadReviews(bookId);

        System.out.println("\n┌──────────────────────────────────────────────┐");
        System.out.println("│                   Reviews                    │");
        System.out.println("└──────────────────────────────────────────────┘");
        if (reviews.isEmpty()) {
            System.out.println("\n┌──────────────────────────────────────────────┐");
            System.out.println("│ No reviews available for this book.          │");
            System.out.println("└──────────────────────────────────────────────┘");
        } else {
            for (String review : reviews) {
                System.out.println(review);
            }
        }

        System.out.print("\nPress Enter to return.");
        input.nextLine();
        input.nextLine();
    }
}
