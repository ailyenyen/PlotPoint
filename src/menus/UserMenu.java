package src.menus;

import java.util.Scanner;

public class UserMenu extends Menu {
    private BookSearchMenu bookSearchMenu  = new BookSearchMenu(input);
    private RecommendationsMenu recommendations = new RecommendationsMenu(input);
    private ProfileMenu profile = new ProfileMenu(input);

    public UserMenu(Scanner input) {
        super(input);
    }

    @Override
    public void displayMenu() {
        boolean exit = false;
        while (!exit) {
            displayTitle("Welcome to PlotPoint!");
            System.out.println("│ [1] Search for a book                        │");
            System.out.println("│ [2] Get Recommendations                      │");
            System.out.println("│ [3] View your profile                        │");
            System.out.println("├──────────────────────────────────────────────┤");
            System.out.println("│ [0] Log out                                  │");
            System.out.println("└──────────────────────────────────────────────┘");

            int choice = getUserChoice("\nEnter your choice: ", 3);
            switch (choice) {
                case 1 -> bookSearchMenu.displayMenu();
                case 2 -> recommendations.displayMenu();
                case 3 -> profile.displayMenu();
                case 0 -> exit = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

}
