package View;

import javax.swing.*;
import java.awt.*;

public class PhotoPanel extends JPanel {
    private final JLabel title = new JLabel();
    private final JTextArea details = new JTextArea();

    public PhotoPanel(Runnable onBack) {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        JButton back = new JButton("Indietro");
        back.addActionListener(e -> onBack.run());

        details.setEditable(false);
        details.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        details.setBackground(new Color(245, 245, 245));

        add(title, BorderLayout.NORTH);
        add(new JScrollPane(details), BorderLayout.CENTER);
        add(back, BorderLayout.SOUTH);
    }

    public void showPhoto(AppMain.PhotoItem p) {
        title.setText("Foto " + p.getId());
        StringBuilder sb = new StringBuilder();
        sb.append("ID foto: ").append(p.getId()).append("\n");
        sb.append("Data scatto: ").append(p.getDataScatto()).append("\n");
        sb.append("Coordinate: ").append(p.getLatitudine()).append(" ").append(p.getLongitudine()).append("\n");
        sb.append("Nome del luogo: ").append(p.getLuogo()).append("\n");
        sb.append("Dispositivo: ").append(p.getDispositivo()).append("\n\n");
        sb.append("Soggetti e categoria:\n");
        if (p.getSoggetti().isEmpty()) {
            sb.append("- Nessun soggetto\n");
        } else {
            for (AppMain.SubjectItem s : p.getSoggetti()) {
                sb.append("- ").append(s.getNome()).append(" | Categoria: ").append(s.getCategoria()).append("\n");
            }
        }
        details.setText(sb.toString());
        details.setCaretPosition(0);
    }
}
