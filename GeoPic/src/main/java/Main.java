import Controller.Controller;
import GUI.dialog.DialogLogin;
import GUI.frame.FinestraAdmin;
import GUI.frame.FinestraUtente;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Controller controller = new Controller();
            controller.loadInMemory();

            DialogLogin dialog = new DialogLogin(controller);
            int authenticate = dialog.mostraDialogo();

            if (authenticate == 1) {
                new FinestraUtente(controller);
            } else if (authenticate == 2) {
                new FinestraAdmin(controller);
            }
        });
    }
}