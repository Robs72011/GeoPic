package GUI.panel;

import Controller.Controller;
import Model.Video;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Pannello avanzato per la visualizzazione e la riproduzione di slideshow video.
 * Estende {@link PannelloVisualizzatoreFoto} aggiungendo componenti per la visualizzazione
 * della descrizione associata al video e una "thumbnail strip" (colonna laterale)
 * che elenca le fotografie che compongono lo slideshow.
 */
public class PannelloVisualizzatoreSlideshow extends PannelloVisualizzatoreFoto {
    private final JTextArea descriptionArea;
    private JScrollPane thumbnailStrip;

    /**
     * Costruisce il pannello dello slideshow.
     * @param onBackClick Callback invocata al clic sul pulsante "Indietro" per
     * gestire il ritorno alla vista principale.
     */
    public PannelloVisualizzatoreSlideshow(Runnable onBackClick, Controller controller, Runnable onContenutoMutato) {
        super(List.of(), onBackClick, controller, onContenutoMutato); // Inizializza con una lista vuota finché non viene scelto lo slideshow

        descriptionArea = createDescriptionArea();
        
        // Creiamo un pannello "sud" diviso in due: a sinistra/centro i bottoni (pannelloBottoni), a destra la descrizione
        JPanel southContextPanel = new JPanel(new BorderLayout());
        southContextPanel.add(pannelloBottoni, BorderLayout.CENTER);
        
        JScrollPane descriptionScrollPane = createDescriptionScrollPanel(descriptionArea);
        descriptionScrollPane.setBorder(BorderFactory.createTitledBorder("Descrizione Slideshow"));
        southContextPanel.add(descriptionScrollPane, BorderLayout.EAST);
        
        add(southContextPanel, BorderLayout.SOUTH);
    }

    /**
     * Aggiorna il pannello con i dati del video selezionato, inclusi i contenuti
     * multimediali, la descrizione e la colonna delle miniature.
     * @param slideshow L'oggetto {@link Video} da visualizzare.
     */
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
     * Crea un'area di testo non modificabile per visualizzare la descrizione del video.
     * @return {@link JTextArea} configurata.
     */
    private JTextArea createDescriptionArea() {
        JTextArea textArea = new JTextArea();
        textArea.setBackground(new Color(230, 230, 250));
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setEditable(false); // Impedisce la modifica del testo
        textArea.setLineWrap(true); // Abilita il wrapping del testo
        textArea.setWrapStyleWord(true); // Impedisce che il testo venga spezzato a metà parola
        
        return textArea;
    }

    /**
     * Crea un contenitore scrollabile per il componente di descrizione.
     * @param Component Il {@link JTextArea} da rendere scrollabile.
     * @return {@link JScrollPane} configurato.
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
     * Genera una striscia laterale (thumbnail strip) con l'elenco delle fotografie
     * che compongono lo slideshow.
     * @param fotografie Lista delle {@link Model.Fotografia} contenute nel video.
     * @return {@link JScrollPane} verticale contenente i metadati delle foto.
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