import Controller.Controller;
import DAO.UtenteDAO;
import ImplementazioniPostgresDAO.UtentePostgresDAO;
import Model.Fotografia;
import Model.Utente;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);  // Create a Scanner object

        System.out.print("Enter username: ");
        String username = scanner.nextLine();  // Read user input

        System.out.print("Enter password: ");
        String password = scanner.nextLine();  // Read user input

        Controller controller = new Controller();

        controller.loadInMemory();

        System.out.println(controller.authentication(username, password));
    }
}