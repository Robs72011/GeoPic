package GUI;

import javax.swing.*;

/**
 * Questo dovrebbe corrispondere a quello che divernterà il Contoller,
 * per ora ho commentato la roba riguardante il login.
 * @see TabbedPaneFrame Frame dell'app e gestore schede.
 */
public class Main {
    public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                LoginPanel dialog = new LoginPanel(); //nome e password: admin admin
                dialog.mostra();
                System.out.println("Utente: " + dialog.getUtente());
                System.out.println("Password: " + dialog.getPassword());
                new TabbedPaneFrame();
        });
    }
}
