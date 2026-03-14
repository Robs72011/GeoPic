package GUI.dialog;

import Controller.Controller;

import javax.swing.*;
import java.awt.*;

/**
 * Dialogo modale per l'inserimento di un nuovo utente nel sistema.
 */
public class DialogAggiungiUtente extends DialogAggiungi {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkAdmin;

    public DialogAggiungiUtente(Frame parentFrame, Controller controller) {
        super(parentFrame, "Aggiungi Nuovo Utente", controller);
        configuraDimensioni(350, 200, 350, 200, false);

        JPanel mainPanel = creaMainPanel();
        buildSezioneForm(mainPanel);
        montaContenutoConBottoniSalva(mainPanel, this::salvaUtente);
    }

    private void buildSezioneForm(JPanel mainPanel) {
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        formPanel.add(new JLabel("Username:"));
        txtUsername = new JTextField();
        formPanel.add(txtUsername);

        formPanel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        formPanel.add(txtPassword);

        formPanel.add(new JLabel("Privilegi Admin:"));
        chkAdmin = new JCheckBox();
        formPanel.add(chkAdmin);

        mainPanel.add(formPanel, BorderLayout.CENTER);
    }

    private void salvaUtente() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        boolean isAdmin = chkAdmin.isSelected();

        if (username.isEmpty() || password.isEmpty()) {
            mostraErrore("Username e Password sono obbligatori.");
            return;
        }

        try {
            boolean success = controller.creazioneNuovoUtente(username, password, isAdmin, false);
            if (success) {
                mostraMessaggio("Utente aggiunto con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                mostraMessaggio("Errore durante la creazione dell'utente. Potrebbe già esistere uno username identico.", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            mostraMessaggio("Errore imprevisto: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
}
