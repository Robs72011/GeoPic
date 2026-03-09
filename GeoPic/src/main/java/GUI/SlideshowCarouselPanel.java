package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;

public class SlideshowCarouselPanel extends JPanel {

    private static final int CARD_WIDTH = 240;
    private static final int CARD_HEIGHT = 135; // Aspect ratio 16:9

    /**
     * Crea una SlideshowCarouselPanel che mostra una serie di slideshow cards in un pannello scrollabile orizzontalmente.
     * Ogni card rappresenta uno slideshow e permette un interazione tramite click event.
     *
     * @param slideshows La lista degli slideshow da mostrare.
     * @param onClick una callback che viene chiamata quando viene cliccato una card {@link GalleryPanelContainer}.
     */
    public SlideshowCarouselPanel(List<Slideshow> slideshows, Consumer<Slideshow> onClick) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 15));

        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        for (Slideshow slideshow : slideshows) {
            JPanel card = creaCard(slideshow, onClick);
            container.add(card);
        }

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Crea una card component che rappresenta una slideshow. La card mostra il nome dello slideshow e
     * un'anteprima (se disponibile) e associa ad ogni card una callback action.
     *
     * @param slideshow lo {@link Slideshow} object contentente dettagli riguardo nome, immagini e descrizione da
     *                  mostrare poi nella visione in dettaglio {@link SlideshowSelector#mostraImmagine(int)}
     * @param onClick a {@link Consumer} callback attivata quando la card viene cliccata, con lo
     *                 {@link Slideshow} associato come parametro.
     * @return a {@link JPanel} ritorna la card dello slideshow.
     */
    private JPanel creaCard(Slideshow slideshow, Consumer<Slideshow> onClick) {
        File copertina = slideshow.getImageFiles().isEmpty() ? null : slideshow.getImageFiles().getFirst();
        ImageIcon icon = copertina != null ? new ImageIcon(copertina.getAbsolutePath()) : null;
        Image img = icon != null
                ? icon.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH)
                : null;

        JLabel background = new JLabel(icon != null ? new ImageIcon(img) : null);
        background.setLayout(new BorderLayout());

        String labelText = slideshow.getName(); // O slideshow.getAutore(), ecc.
        JLabel label = new JLabel(labelText, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setOpaque(true);
        label.setBackground(new Color(0, 0, 0, 120)); // semi-trasparente

        background.add(label, BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(background, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                onClick.accept(slideshow);
            }
        });

        return panel;
    }
}
