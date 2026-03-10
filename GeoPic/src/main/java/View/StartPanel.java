package View;

import javax.swing.*;
import java.awt.*;

public class StartPanel extends JPanel {

    public StartPanel(Runnable onAdminLogin, Runnable onUserLogin) {

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // ===== TITOLO =====
        JLabel title = new JLabel("Login GeoPic", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        // ===== FORM CENTRALE =====
        JPanel centerPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        JLabel lblUsername = new JLabel("Username:");
        JTextField txtUsername = new JTextField();

        JLabel lblPassword = new JLabel("Password:");
        JPasswordField txtPassword = new JPasswordField();

        centerPanel.add(lblUsername);
        centerPanel.add(txtUsername);
        centerPanel.add(lblPassword);
        centerPanel.add(txtPassword);

        add(centerPanel, BorderLayout.CENTER);

        // ===== BOTTONE LOGIN =====
        JButton btnLogin = new JButton("Login");
        add(btnLogin, BorderLayout.SOUTH);

        btnLogin.addActionListener(e -> {

            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();

            if (username.equals("admin") && password.equals("admin")) {

                JOptionPane.showMessageDialog(this, "Accesso come Admin");
                onAdminLogin.run();

            } else if (username.equals("utente") && password.equals("password")) {

                JOptionPane.showMessageDialog(this, "Accesso come Utente");
                onUserLogin.run();

            } else {

                JOptionPane.showMessageDialog(this,
                        "Credenziali non valide",
                        "Errore Login",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}