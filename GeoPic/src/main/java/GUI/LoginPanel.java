package GUI;

import Controller.Controller;

import javax.swing.*;
import static javax.swing.JOptionPane.*;

/**
 * Pannello grafico utilizzato per la fase di autenticazione dell'utente.
 * Offre un form con campi per nome utente e password e gestisce il ciclo di vita
 * del dialogo di login fino all'ottenimento di credenziali valide o alla chiusura dell'applicazione.
 */
public class LoginPanel extends JPanel{
    private final JTextField campoUtente = new JTextField(15);
    private final JPasswordField campoPassword= new JPasswordField(15);

    /**
     * Costruisce il pannello di login disponendo verticalmente le etichette
     * e i campi di input tramite {@link BoxLayout}.
     */
     LoginPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(new JLabel("Utente:"));
        add(campoUtente);
        add(Box.createVerticalStrut(10));
        add(new JLabel("Password:"));
        add(campoPassword);
    }

    /** @return Il nome utente inserito nel campo di testo. */
    public String getUtente() {
        return campoUtente.getText();
    }

    /** @return La password inserita convertita in {@code String}. */
    public String getPassword() {
        return new String(campoPassword.getPassword());
    }

    /** @param utente Imposta il testo del campo utente. */
    public void setUtente(String utente) {campoUtente.setText(utente);}

    /** @param password Imposta il testo del campo password. */
    public void setPassword(String password) {campoPassword.setText(password);}

    /**
     * Avvia il processo di login mostrando un {@link JOptionPane} modale.
     * Utilizza un ciclo {@code while} per mantenere il dialogo aperto finché l'utente
     * non effettua un accesso valido o chiude forzatamente l'applicazione.
     * @param controller Il riferimento al {@link Controller} per verificare le credenziali.
     * @return 2 se admin, 1 se utente standard, altrimenti gestisce il ciclo di riprova.
     */
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