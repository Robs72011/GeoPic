package GUI;

import Controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Finestra di dialogo modale utilizzata per la fase di autenticazione dell'utente.
 */
public class LoginDialog extends JDialog {
    private final JTextField campoUtente = new JTextField(15);
    private final JPasswordField campoPassword = new JPasswordField(15);
    private int esitoAutenticazione = 0;
    
    private String utenteLoggato;
    private String passwordLoggata;

    public LoginDialog(Controller controller) {
        setTitle("Login");
        setModal(true); // Fondamentale: blocca il resto dell'app finche e aperto
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        // Pannello contenitore principale per dare margine
        JPanel mainPanel = new JPanel(new BorderLayout(20, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));

        // Se l'utente chiude dalla 'X', chiudiamo l'app
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // --- ICONA ---
        Icon questionIcon = UIManager.getIcon("OptionPane.questionIcon");
        JLabel iconLabel = new JLabel(questionIcon);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        mainPanel.add(iconLabel, BorderLayout.WEST);

        // --- PANNELLO CENTRALE (Form) ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        JLabel lblUtente = new JLabel("Utente:");
        lblUtente.setAlignmentX(Component.LEFT_ALIGNMENT);
        campoUtente.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        campoPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(lblUtente);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(campoUtente);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(lblPassword);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(campoPassword);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // --- PANNELLO INFERIORE (Bottoni) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnLogin = new JButton("Login");
        JButton btnCancella = new JButton("Cancella");

        btnCancella.addActionListener(_ -> {
            campoUtente.setText("");
            campoPassword.setText("");
        });

        btnLogin.addActionListener(_ -> {
            String u = campoUtente.getText();
            String p = new String(campoPassword.getPassword());

            int auth = controller.authentication(u, p);
            if (auth == 1 || auth == 2) {
                esitoAutenticazione = auth;
                utenteLoggato = u;
                passwordLoggata = p;
                JOptionPane.showMessageDialog(this, "Accesso effettuato con: " + u, "Accesso Effettuato", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Chiude il dialog ed esce dal blocco
            } else {
                JOptionPane.showMessageDialog(this, "Utente e/o password errati.", "Errore", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Permette di premere "Invio" per fare il login
        getRootPane().setDefaultButton(btnLogin);

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnCancella);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        pack();
        setResizable(false); // Impedisce il ridimensionamento della finestra
        setLocationRelativeTo(null); // Centra sullo schermo
    }

    /**
     * Mostra la finestra di dialogo e blocca l'esecuzione corrente finche non viene chiusa.
     * @return 2 se admin, 1 se utente standard.
     */
    public int mostraDialogo() {
        setVisible(true);
        return esitoAutenticazione;
    }

    /** @return Il nome utente loggato con successo. */
    public String getUtente() { return utenteLoggato; }

    /** @return La password utilizzata per il login effettuato. */
    public String getPassword() { return passwordLoggata; }
}

