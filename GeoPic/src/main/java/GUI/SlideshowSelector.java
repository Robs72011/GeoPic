package GUI;

import Controller.Controller;
import Model.Video;

import javax.swing.*;
import java.awt.*;
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
    public SlideshowSelector(Runnable onBackClick, Controller controller) {
        super(onBackClick, controller);

        descriptionArea = createDescriptionArea("");
        
        // Creiamo un pannello "sud" diviso in due: a sinistra/centro i bottoni (footer), a destra la descrizione
        JPanel southContextPanel = new JPanel(new BorderLayout());
        southContextPanel.add(footer, BorderLayout.CENTER);
        
        JScrollPane descriptionScrollPane = createDescriptionScrollPanel(descriptionArea);
        descriptionScrollPane.setBorder(BorderFactory.createTitledBorder("Descrizione Slideshow"));
        southContextPanel.add(descriptionScrollPane, BorderLayout.EAST);
        
        add(southContextPanel, BorderLayout.SOUTH);
    }

    public void setSlideshow(Video slideshow) {
        if (slideshow == null) return;

        // Imposta foto nello slideshow (metodo ereditato da ImageSelector)
        setContent(slideshow.getCompostoDaFoto());

        // Aggiorna descrizione, usando il dato dal database
        if (slideshow.getDescrizione() != null && !slideshow.getDescrizione().isEmpty()) {
            descriptionArea.setText(slideshow.getDescrizione());
        } else {
            descriptionArea.setText("Nessuna descrizione disponibile per questo video.");
        }

        // Aggiorna thumbnail strip
        if (thumbnailStrip != null) {
            remove(thumbnailStrip);
        }
        thumbnailStrip = createThumbnailStrip(slideshow.getCompostoDaFoto());
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
     * Crea una strip verticale con i dati delle foto dello slideshow.
     *
     * @param fotografie array di fotografie
     * @return JScrollPane scrollabile contenente le label
     */
    private JScrollPane createThumbnailStrip(List<Model.Fotografia> fotografie) {
        JPanel thumbPanel = new JPanel();
        thumbPanel.setLayout(new BoxLayout(thumbPanel, BoxLayout.Y_AXIS));
        thumbPanel.setPreferredSize(new Dimension(150, 0));
        thumbPanel.setBackground(Color.LIGHT_GRAY);

        if (fotografie != null) {
            for (Model.Fotografia foto : fotografie) {
                JLabel thumbLabel = new JLabel("<html><b>Foto ID:</b> " + foto.getIdFoto() + " <br><i>" + foto.getDataDiScatto() + "</i></html>");
                thumbLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                thumbPanel.add(thumbLabel);
            }
        }

        JScrollPane scrollPane = new JScrollPane(thumbPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }
}