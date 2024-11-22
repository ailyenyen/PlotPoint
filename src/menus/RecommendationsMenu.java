package src.menus;
import java.util.List;
import java.util.Scanner;
import src.models.Book;
import src.managers.BookManager;

public class RecommendationsMenu extends Menu {
    private final BookManager bookManager = new BookManager();

    public RecommendationsMenu(Scanner input) {
        super(input);
    }

    public void displayMenu() {
        boolean exit = false;

        while (!exit) {
            displayTitle("Get Recommendations");
            System.out.println("│ Choose recommendation type:                  │");
            System.out.println("│ [1] Genre-based                              │");
            System.out.println("│ [2] Mood-based                               │");
            System.out.println("├──────────────────────────────────────────────┤");
            System.out.println("│ [0] Exit                                     │");
            System.out.println("└──────────────────────────────────────────────┘");

            int choice = getUserChoice("\nEnter your choice: ", 2);

            switch (choice) {
                case 1 -> recommendByType("genre");
                case 2 -> recommendByType("mood");
                case 0 -> exit = true;
                default -> System.out.println("Invalid choice. Please select an option from the menu.");
            }
        }
    }

    private void recommendByType(String type) {
        List<String> tags = bookManager.getTagsByType(type);
        if (tags.isEmpty()) {
            System.out.println("No tags available for recommendations.");
            return;
        }

        displayTitle("Get Recommendations");
        System.out.printf("│ Choose a %-35s │\n", type + ":");

        for (int i = 0; i < tags.size(); i++) {
            String option = String.format("[%d] %-40s", i + 1, tags.get(i));
            System.out.println("│ " + option.substring(0, 44) + " │");
        }
        System.out.println("├──────────────────────────────────────────────┤");
        System.out.println("│ [0] Exit                                     │");
        System.out.println("└──────────────────────────────────────────────┘");

        int choice = getUserChoice("\nEnter your choice: ", tags.size());

        if (choice > 0 && choice <= tags.size()) {
            String selectedTag = tags.get(choice - 1);
            List<Book> recommendations = bookManager.getRecommendedBooksByTag(selectedTag);
            new BookSearchMenu(input).displayBookList(recommendations);
        }
    }
}
