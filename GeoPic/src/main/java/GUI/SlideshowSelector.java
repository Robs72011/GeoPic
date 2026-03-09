package GUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class SlideshowSelector extends ImageSelector {
    private final JTextArea descriptionArea;
    private JScrollPane thumbnailStrip;
    /**
     * Pannello per la visualizzazione di uno slideshow.
     * Estende {@link ImageSelector} aggiungendo una strip di miniature e la descrizione.
     *
     * @param onBackClick callback per tornare alla schermata precedente
     */
    public SlideshowSelector(Runnable onBackClick) {
        super(onBackClick);

        descriptionArea = createDescriptionArea("");
        JScrollPane descriptionScrollPane = createDescriptionScrollPanel(descriptionArea);
        footer.add(descriptionScrollPane);
        add(footer, BorderLayout.SOUTH);
    }

    public void setSlideshow(Slideshow slideshow) {
        if (slideshow == null) return;

        // Imposta immagini nello slideshow (metodo ereditato da ImageSelector)
        setContent(slideshow.getImageFiles());

        // Aggiorna descrizione
        descriptionArea.setText(slideshow.getDescrizione());

        // Aggiorna thumbnail strip
        if (thumbnailStrip != null) {
            remove(thumbnailStrip);
        }
        thumbnailStrip = createThumbnailStrip(slideshow.getImageFiles());
        add(thumbnailStrip, BorderLayout.EAST);

        revalidate();
        repaint();
    }

    /**
     * Crea un pannello JTextArea contentente la descrizione
     * @param descrizione il testo della descrizione
     * @return il pannello contenente la descrizione
     */
    private JTextArea createDescriptionArea(String descrizione) {
        JTextArea textArea = new JTextArea(descrizione);
        textArea.setBackground(new Color(230, 230, 250));
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setEditable(false); // Impedisce la modifica del testo
        textArea.setLineWrap(true); // Abilita il wrapping del testo
        textArea.setWrapStyleWord(true); // Impedisce che il testo venga spezzato a metà parola
        return textArea;
    }

    /**
     * Crea un pannello scrollabile per la descrizione
     * @param Component prende un componente JTextArea e la fa diventare scrollabile.
     * @return un JScrollPane contenente la descrizione
     */
    private JScrollPane createDescriptionScrollPanel(JTextArea Component) {
        JScrollPane descriptionScrollPane = new JScrollPane(Component);
        descriptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        descriptionScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        Component.setPreferredSize(new Dimension(400, 100)); // Adatta la dimensione orizzontale
        Component.setMinimumSize(new Dimension(200, 100)); // Imposta una dimensione minima per evitare che si schiacci
        return descriptionScrollPane;
    }

    /**
     * Crea una strip verticale con le miniature delle immagini dello slideshow.
     *
     * @param immagini array di file immagine
     * @return JScrollPane scrollabile contenente le miniature
     */
    private JScrollPane createThumbnailStrip(List<File> immagini) {
        JPanel thumbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        thumbPanel.setPreferredSize(new Dimension(120, 0));
        thumbPanel.setBackground(Color.LIGHT_GRAY);

        for (File imgFile : immagini) {
            try {
                ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
                Image scaled = icon.getImage().getScaledInstance(80, 60, Image.SCALE_SMOOTH);
                JLabel thumbLabel = new JLabel(new ImageIcon(scaled));
                thumbPanel.add(thumbLabel);
            } catch (Exception e) {
                thumbPanel.add(new JLabel("X"));
            }
        }

        JScrollPane scrollPane = new JScrollPane(thumbPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }
}
