package src.menus;
import java.util.Scanner;
import src.models.Book;

public abstract class Menu {
    protected Scanner input;

    public Menu(Scanner input) {
        this.input = input;
    }

    protected void displayTitle(String title) {
        System.out.println("\n┌──────────────────────────────────────────────┐");
        System.out.println("│ " + centerText(title, 44) + " │");
        System.out.println("├──────────────────────────────────────────────┤");
    }

    protected int getUserChoice(String prompt, int maxOption) {
        System.out.print(prompt);
        while (!input.hasNextInt()) {
            System.out.print("Invalid input. " + prompt);
            input.next();
        }
        int choice = input.nextInt();
        if (choice < 0 || choice > maxOption) {
            System.out.println("Please select a valid option.");
            return getUserChoice(prompt, maxOption);
        }
        return choice;
    }

    public String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text + " ".repeat(Math.max(0, width - text.length() - padding));
    }

    public abstract void displayMenu();

    public void displayMenu(Book book) {
        throw new UnsupportedOperationException("This menu does not support displayMenu(Book).");
    }
}
