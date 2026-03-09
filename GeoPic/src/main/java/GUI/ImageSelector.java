package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;

public class ImageSelector extends JPanel {
    protected List<File> immagini = List.of();
    protected int indiceCorrente = 0;
    protected final JLabel imageLabel = new JLabel("", SwingConstants.CENTER);
    protected JPanel footer = new JPanel();

    private static final int MAX_IMG_WIDTH = 1920;
    private static final int MAX_IMG_HEIGHT = 1080;

    /**
     * Pannello per la visualizzazione di immagini. Con gestione dinamica della visualizzazione del contenuto, utile
     * se si vuole accedere con diversi utenti e aggiornare le immagini disponibili.
     * @param onBackClick azione da eseguire al clic su "Indietro"
     */
    public ImageSelector(Runnable onBackClick) {
        this.setLayout(new BorderLayout());
        this.add(imageLabel, BorderLayout.CENTER);

        // Pannello footer
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));

        // Pannello per i metadati fissi di esempio
        footer.add(new MetadataPanel("001", "Canon EOS 80D", "Mario Rossi", "2024-08-12", "Roma"));

        // Bottoni
        footer.add(creaPannelloBottoni(onBackClick));
        this.add(footer, BorderLayout.SOUTH);
    }

    public void setContent(List<File> immagini) {
       this.immagini = immagini != null ? immagini : List.of();
       mostraImmagine(0);
    }

    /**
     * Crea un pannello bottoni per scorrere le immagini nella visione in dettaglio
     * @param onBackClick l'evento da ritornare a {@link GalleryPanelContainer}
     * per uscire dalla visone in dettaglio
     * @return pannello bottoni
     * @see GalleryPanel
     */
    private JPanel creaPannelloBottoni(Runnable onBackClick) {
        JButton btnIndietro = new JButton("⤬ Indietro");
        JButton btnPrecedente = new JButton("<< Precedente");
        JButton btnSuccessivo = new JButton("Successivo >>");
        JButton btnPrivatizza = new JButton("\uD83D\uDD12");

        btnPrecedente.addActionListener(_ -> mostraImmagine((indiceCorrente - 1 + immagini.size()) % immagini.size()));
        btnSuccessivo.addActionListener(_ -> mostraImmagine((indiceCorrente + 1) % immagini.size()));
        btnIndietro.addActionListener(_ -> onBackClick.run());
        btnPrivatizza.addActionListener(_ -> {
            int scelta = JOptionPane.showConfirmDialog(
                    null,
                    "Sei sicuro di voler rendere la foto privata?",
                    "Conferma",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (scelta == JOptionPane.YES_OPTION) {}
        });

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonsPanel.add(btnPrecedente);
        buttonsPanel.add(btnSuccessivo);
        buttonsPanel.add(btnIndietro);
        buttonsPanel.add(btnPrivatizza);
        return buttonsPanel;
    }

    public void mostraImmagine(int index) {
        if (immagini == null || immagini.isEmpty()) return;

        indiceCorrente = index;

        try {
            BufferedImage original = ImageIO.read(immagini.get(indiceCorrente));

            int width = original.getWidth();
            int height = original.getHeight();

            if (width > MAX_IMG_WIDTH || height > MAX_IMG_HEIGHT) {
                int newWidth = width / 2;
                int newHeight = height / 2;
                Image scaled = original.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaled));
            } else {
                imageLabel.setIcon(new ImageIcon(original));
            }

        } catch (Exception e) {
            e.printStackTrace();
            imageLabel.setText("Errore nel caricamento dell'immagine.");
        }
    }
}
