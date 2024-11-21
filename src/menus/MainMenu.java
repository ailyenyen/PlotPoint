package src.menus;

import src.managers.UserManager;
import src.models.User;

import java.util.Scanner;

public class MainMenu extends Menu {
    private final UserManager userManager = UserManager.getInstance();
    private User loggedInUser;
    private final UserMenu userMenu = new UserMenu(input);
    private final AdminMenu adminMenu = new AdminMenu(input);

    public MainMenu(Scanner input) {
        super(input);
    }

    @Override
    public void displayMenu() {
        boolean exit = false;
        while (!exit) {
            displayTitle("Welcome to PlotPoint!");
            System.out.println("│ [1] Log In                                   │");
            System.out.println("│ [2] Sign Up                                  │");
            System.out.println("├──────────────────────────────────────────────┤");
            System.out.println("│ [0] Exit                                     │");
            System.out.println("└──────────────────────────────────────────────┘");

            int choice = getUserChoice("\nEnter your choice: ", 2);
            switch (choice) {
                case 1 -> logIn();
                case 2 -> signUp();
                case 0 -> exit = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void logIn() {
        try {
            System.out.println("\n┌──────────────────────────────────────────────┐");
            System.out.println("│                  Log - In                    │");
            System.out.println("└──────────────────────────────────────────────┘");
            System.out.print(" Enter your username: ");
            input.nextLine(); // Consume any leftover newline
            String username = input.nextLine();

            System.out.print(" Enter your password: ");
            String password = input.nextLine();

            loggedInUser = userManager.verifyCredentials(username, password);

            if (loggedInUser != null) {
                System.out.println("\nLogged in successfully.");
                if (loggedInUser.isAdmin()) {
                    adminMenu.displayMenu();
                } else {
                    userMenu.displayMenu();
                }
            } else {
                System.out.println("\nInvalid username or password. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error occurred while logging in. Please try again.");
        }
    }

    private void signUp() {
        try {
            System.out.println("\n┌──────────────────────────────────────────────┐");
            System.out.println("│                 Sign - Up                    │");
            System.out.println("└──────────────────────────────────────────────┘");
            input.nextLine();
            String username, password;

            while (true) {
                System.out.print(" Enter a username: ");
                username = input.nextLine();
                if (userManager.validUsername(username)) {
                    System.out.println("\nUsername already exists. Please choose another one.\n");
                } else {
                    break;
                }
            }

            while (true) {
                System.out.print(" Enter password: ");
                password = input.nextLine();
                if (password.length() < 3) {
                    System.out.println("\nPassword must be at least 3 characters long.\n");
                } else {
                    break;
                }
            }

            userManager.addUser(username, password, false);
            System.out.println("\nAccount created successfully!");

        } catch (Exception e) {
            System.out.println("An error occurred during sign up. Please try again.");
        }
    }
}
