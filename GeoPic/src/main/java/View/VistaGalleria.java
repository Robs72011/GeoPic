package View;

import javax.swing.*;
import java.awt.*;

public class VistaGalleria extends JFrame {

    public VistaGalleria(String nome, String tipo) {
        super("Galleria: " + nome);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel(nome + " (" + tipo + ")");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        // Placeholder: qui metterai contenuti della galleria (foto, lista, griglia, ecc.)
        JTextArea area = new JTextArea(
                "Qui mostri il contenuto della galleria:\n- foto\n- descrizioni\n- ecc.\n\n" +
                        "Hai aperto: " + nome + " | Tipo: " + tipo
        );
        area.setEditable(false);

        JButton back = new JButton("Chiudi");
        back.addActionListener(e -> dispose());

        root.add(title, BorderLayout.NORTH);
        root.add(new JScrollPane(area), BorderLayout.CENTER);
        root.add(back, BorderLayout.SOUTH);

        setContentPane(root);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null);
    }
}

