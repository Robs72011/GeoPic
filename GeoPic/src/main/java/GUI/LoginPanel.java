package GUI;

import javax.swing.*;

import static javax.swing.JOptionPane.*;

public class LoginPanel extends JPanel{
    private final JTextField campoUtente = new JTextField(15);
    private final JPasswordField campoPassword= new JPasswordField(15);;

     LoginPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(new JLabel("Utente:"));
        add(campoUtente);
        add(Box.createVerticalStrut(10));
        add(new JLabel("Password:"));
        add(campoPassword);
    }

    public void mostra() {
        String[] options = {"Login", "Cancella"};
        LoginPanelHandler loginhandler = new LoginPanelHandler();
        boolean accessoConsentito = false;

        while (!accessoConsentito) {
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
            String utente = getUtente();
            loginhandler.getlogincredits(utente, getPassword());

            switch (scelta) {
                //VALUTAZIONE LOGIN
                case 0 -> {
                    if (loginhandler.check_login()) {
                        showMessageDialog(null, "Accesso effettuato con: " + utente, "Accesso Effettuato", INFORMATION_MESSAGE);
                        accessoConsentito = true;
                    } else {
                        showMessageDialog(null, "Utente e/o password errati.", "Errore", WARNING_MESSAGE);
                    }
                }

                //CANCELLA
                case 1 -> {
                    campoUtente.setText("");
                    campoPassword.setText("");
                }

                //CHIUSURA FINESTRA
                default -> System.exit(0);
            }
        }
    }

    public String getUtente() {
        return campoUtente.getText();
    }

    public String getPassword() {
        return new String(campoPassword.getPassword());
    }
}