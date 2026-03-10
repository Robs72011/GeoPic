package View;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VideoPanel extends JPanel {
    private final JLabel title = new JLabel();
    private final JLabel info = new JLabel("Sequenza automatica: 3 secondi per foto");
    private final JTextArea frameArea = new JTextArea();
    private final DefaultListModel<String> fotoModel = new DefaultListModel<>();
    private final JList<String> fotoList = new JList<>(fotoModel);

    private javax.swing.Timer timer;
    private List<AppMain.PhotoItem> currentFrames = new ArrayList<>();
    private int currentIndex = 0;

    public VideoPanel(Runnable onBack) {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        frameArea.setEditable(false);
        frameArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        frameArea.setBackground(new Color(245, 245, 245));

        JPanel top = new JPanel(new GridLayout(0, 1));
        top.add(title);
        top.add(info);

        JPanel center = new JPanel(new GridLayout(1, 2, 10, 10));
        JPanel left = new JPanel(new BorderLayout());
        left.add(new JLabel("Frame corrente"), BorderLayout.NORTH);
        left.add(new JScrollPane(frameArea), BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout());
        right.add(new JLabel("Sequenza foto del video"), BorderLayout.NORTH);
        right.add(new JScrollPane(fotoList), BorderLayout.CENTER);

        center.add(left);
        center.add(right);

        JButton backBtn = new JButton("Chiudi Video");
        backBtn.addActionListener(e -> {
            stopTimer();
            onBack.run();
        });

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(backBtn, BorderLayout.SOUTH);
    }

    public void showVideo(AppMain.VideoItem video, List<AppMain.PhotoItem> fotoSequence) {
        stopTimer();
        title.setText("Video: " + video.getNome() + " (ID: " + video.getId() + ")");

        currentFrames = (fotoSequence != null) ? new ArrayList<>(fotoSequence) : new ArrayList<>();
        currentIndex = 0;

        fotoModel.clear();
        for (int i = 0; i < currentFrames.size(); i++) {
            AppMain.PhotoItem p = currentFrames.get(i);
            fotoModel.addElement((i + 1) + ") " + p.getId() + " - " + p.getLuogo());
        }

        if (currentFrames.isEmpty()) {
            frameArea.setText("Questo video non contiene foto.");
            return;
        }

        renderCurrentFrame();
        timer = new javax.swing.Timer(3000, e -> {
            currentIndex = (currentIndex + 1) % currentFrames.size();
            renderCurrentFrame();
        });
        timer.start();
    }

    private void renderCurrentFrame() {
        if (currentFrames.isEmpty()) {
            frameArea.setText("Nessun frame.");
            return;
        }

        AppMain.PhotoItem p = currentFrames.get(currentIndex);
        fotoList.setSelectedIndex(currentIndex);
        fotoList.ensureIndexIsVisible(currentIndex);

        StringBuilder sb = new StringBuilder();
        sb.append("[Frame ").append(currentIndex + 1).append(" / ").append(currentFrames.size()).append("]\n\n");
        sb.append("ID foto: ").append(p.getId()).append("\n");
        sb.append("Data scatto: ").append(p.getDataScatto()).append("\n");
        sb.append("Coordinate: ").append(p.getLatitudine()).append(" ").append(p.getLongitudine()).append("\n");
        sb.append("Luogo: ").append(p.getLuogo()).append("\n");
        sb.append("Dispositivo: ").append(p.getDispositivo()).append("\n");
        sb.append("Soggetti: ");
        if (p.getSoggetti().isEmpty()) {
            sb.append("nessuno");
        } else {
            for (int i = 0; i < p.getSoggetti().size(); i++) {
                AppMain.SubjectItem s = p.getSoggetti().get(i);
                if (i > 0) sb.append(", ");
                sb.append(s.getNome()).append(" (" ).append(s.getCategoria()).append(")");
            }
        }
        sb.append("\n\n");
        sb.append("(Demo UI: qui la foto cambia automaticamente ogni 3 secondi)");

        frameArea.setText(sb.toString());
        frameArea.setCaretPosition(0);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }
}
