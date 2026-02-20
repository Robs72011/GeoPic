package View;

import javax.swing.*;
import java.awt.*;

public class StartPanel extends JPanel {

    public StartPanel(Runnable onAdmin, Runnable onUser) {
        super(new GridBagLayout()); // Centra i bottoni

        JButton admin = new JButton("Entra come Admin");
        JButton user  = new JButton("Entra come Utente");

        // Stile bottoni
        admin.setPreferredSize(new Dimension(200, 50));
        user.setPreferredSize(new Dimension(200, 50));

        JPanel box = new JPanel(new GridLayout(2, 1, 10, 10));
        box.add(admin);
        box.add(user);

        add(box);

        // Collega le azioni passate da AppMain
        admin.addActionListener(e -> onAdmin.run());
        user.addActionListener(e -> onUser.run());
    }
}