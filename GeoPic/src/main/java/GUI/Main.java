package GUI;

import Controller.Controller;

import javax.swing.*;

/**
 * Questo dovrebbe corrispondere a quello che diventerà il Contoller,
 * per ora ho commentato la roba riguardante il login.
 * @see TabbedPaneFrame Frame dell'app e gestore schede.
 */
public class Main {
    public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                Controller controller = new Controller();//crea il controller
                controller.loadInMemory();

                LoginPanel dialog = new LoginPanel();
                int authenticate = dialog.Login(controller);

                if(authenticate == 1) {
                    System.out.println("Utente: " + dialog.getUtente());
                    System.out.println("Password: " + dialog.getPassword());
                    new TabbedPaneFrame(controller);
                }
                else if(authenticate == 2){
                    //parte da implementare per la gui admin
                }
        });
    }
}
