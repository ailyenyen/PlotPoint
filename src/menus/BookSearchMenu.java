package src.menus;

import java.util.List;
import java.util.Scanner;
import src.managers.*;
import src.models.Book;
import src.models.User;

public class BookSearchMenu extends Menu {
    private BookManager bookManager = new BookManager();
    private BookMenu bookMenu = new BookMenu(input);

    public BookSearchMenu(Scanner input) {
        super(input);
    }

    public void displayMenu() {
        boolean exit = false;

        while (!exit) {
            displayTitle("Search For a Book");
            System.out.println("│ Search by:                                   │");
            System.out.println("│ [1] Title                                    │");
            System.out.println("│ [2] Author                                   │");
            System.out.println("├──────────────────────────────────────────────┤");
            System.out.println("│ [0] Exit                                     │");
            System.out.println("└──────────────────────────────────────────────┘");

            int choice = getUserChoice("\nEnter your choice: ", 2);
            input.nextLine();
            List<Book> books = null;

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter book title: ");
                    String title = input.nextLine();
                    books = bookManager.searchByTitle(title);
                }
                case 2 -> {
                    System.out.print("Enter author name: ");
                    String author = input.nextLine();
                    books = bookManager.searchByAuthor(author);
                }
                case 0 -> exit = true;
                default -> System.out.println("Invalid choice. Please select an option from the menu.");
            }

            if (books != null) {
                displayBookList(books);
            }
        }
    }

    public void displayBookList(List<Book> books) {
        displayTitle("Search Results");
        if (books == null || books.isEmpty()) {
            System.out.println("│                No books found                │");
            System.out.println("└──────────────────────────────────────────────┘\n");
            System.out.print("Press enter to return.");
            input.nextLine();
            return;
        }

        for (int i = 0; i < books.size(); i++) {
            String title = books.get(i).getTitle();
            String author = books.get(i).getAuthor();
            String rating = books.get(i).getOverallRating();

            String bookInfo = "[" + (i + 1) + "] " + title + " by " + author;
            if (bookInfo.length() > 38) {
                bookInfo = bookInfo.substring(0, 35) + "...";
            }

            System.out.printf("│ %-44s │\n", bookInfo);
            System.out.printf("│    Average rating: %-25s │\n", rating);
            System.out.println("├──────────────────────────────────────────────┤");
        }
        System.out.println("│ [0] Exit                                     │");
        System.out.println("└──────────────────────────────────────────────┘");

        int bookChoice = getUserChoice("\nSelect a book to view details: ", books.size());
        if (bookChoice > 0 && bookChoice <= books.size()) {
            bookMenu.displayMenu(books.get(bookChoice - 1));
        }
    }


}
