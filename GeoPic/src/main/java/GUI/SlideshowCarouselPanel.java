package GUI;

import Model.Video;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    public SlideshowCarouselPanel(List<Video> slideshows, Consumer<Video> onClick) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 15));

        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        for (Video slideshow : slideshows) {
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
     * un'anteprima e associa ad ogni card una callback action.
     *
     * @param slideshow video associato alla card
     * @param onClick a {@link Consumer} callback attivata quando la card viene cliccata
     * @return a {@link JPanel} ritorna la card dello slideshow.
     */
    private JPanel creaCard(Video slideshow, Consumer<Video> onClick) {
        JPanel background = new JPanel();
        background.setLayout(new BorderLayout());
        background.setBackground(new Color(220, 220, 220));

        JLabel titleLabel = new JLabel("<html><div style='text-align: center;'><b>" + slideshow.getTitolo() + "</b></div></html>", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        background.add(titleLabel, BorderLayout.CENTER);

        String labelText = "Video ID: " + slideshow.getIdVideo();
        JLabel label = new JLabel(labelText, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setOpaque(true);
        label.setBackground(new Color(100, 100, 100));

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
