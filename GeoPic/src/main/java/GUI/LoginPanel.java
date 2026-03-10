package GUI;

import Controller.Controller;

import javax.swing.*;
import static javax.swing.JOptionPane.*;

public class LoginPanel extends JPanel{
    private final JTextField campoUtente = new JTextField(15);
    private final JPasswordField campoPassword= new JPasswordField(15);

     LoginPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(new JLabel("Utente:"));
        add(campoUtente);
        add(Box.createVerticalStrut(10));
        add(new JLabel("Password:"));
        add(campoPassword);
    }

    public String getUtente() {
        return campoUtente.getText();
    }
    public String getPassword() {
        return new String(campoPassword.getPassword());
    }
    public void setUtente(String utente) {campoUtente.setText(utente);}
    public void setPassword(String password) {campoPassword.setText(password);}

    public int Login(Controller controller) {
        String[] options = {"Login", "Cancella"};
        LoginPanelHandler loginhandler = new LoginPanelHandler(controller);

        while (true) {
            //DIALOG DI LOGIN
            int scelta = JOptionPane.showOptionDialog(
                    null,
                    this,
                    "Login",
                    DEFAULT_OPTION,
                    QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            loginhandler.setlogincredits(getUtente(), getPassword());

            switch (scelta) {
                //VALUTAZIONE LOGIN
                case 0 -> {
                    int auth_return = loginhandler.auth();
                    if (auth_return == 1 || auth_return == 2) {
                        showMessageDialog(null, "Accesso effettuato con: " + getUtente(), "Accesso Effettuato", INFORMATION_MESSAGE);
                        return auth_return;
                    } else {
                        showMessageDialog(null, "Utente e/o password errati.", "Errore", WARNING_MESSAGE);
                    }
                }

                //CANCELLA
                case 1 -> {
                    setUtente("");
                    setPassword("");
                }

                //CHIUSURA FINESTRA
                default -> System.exit(0);
            }
        }
    }
}