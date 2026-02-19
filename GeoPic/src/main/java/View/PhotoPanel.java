package View;

import javax.swing.*;
import java.awt.*;

public class PhotoPanel extends JPanel {

    private final JLabel title = new JLabel();
    private final JTextArea area = new JTextArea();

    public PhotoPanel(Runnable onBack) {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        area.setEditable(false);

        JButton back = new JButton("Indietro");
        back.addActionListener(e -> onBack.run());

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.WEST);
        top.add(back, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(area), BorderLayout.CENTER);
    }

    public void showPhoto(String photoId, String titolo) {
        title.setText("Foto: " + titolo);
        area.setText("Qui mostrerai l'immagine vera.\n\nphotoId = " + photoId);
    }
}
