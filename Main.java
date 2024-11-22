import src.menus.MainMenu;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        MainMenu mainMenu = new MainMenu(input);
        mainMenu.displayMenu();
        input.close();
    }
}

