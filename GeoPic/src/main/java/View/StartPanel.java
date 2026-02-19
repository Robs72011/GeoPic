package View;

import javax.swing.*;
import java.awt.*;

public class StartPanel extends JPanel {

    public StartPanel(Runnable onAdmin, Runnable onUser) {
        super(new GridBagLayout());

        JButton admin = new JButton("Entra come Admin");
        JButton user  = new JButton("Entra come Utente");

        JPanel box = new JPanel(new GridLayout(2, 1, 10, 10));
        box.add(admin);
        box.add(user);

        add(box);

        admin.addActionListener(e -> onAdmin.run());
        user.addActionListener(e -> onUser.run());
    }
}