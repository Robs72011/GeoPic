package GUI.panel;

import Controller.Controller;
import Model.Fotografia;
import Model.Video;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Pannello avanzato per la visualizzazione e la riproduzione di slideshow video.
 * Estende {@link PannelloVisualizzatoreFoto} aggiungendo componenti per la visualizzazione
 * della descrizione associata al video e una "thumbnail strip" (colonna laterale)
 * che elenca le fotografie che compongono lo slideshow.
 */
public class PannelloVisualizzatoreSlideshow extends PannelloVisualizzatoreFoto {
    private final JTextArea descriptionArea;
    private final JScrollPane thumbnailStrip;

    /**
     * Costruisce il pannello dello slideshow.
     * @param onBackClick Callback invocata al clic sul pulsante "Indietro" per
     * gestire il ritorno alla vista principale.
     */
    public PannelloVisualizzatoreSlideshow(Runnable onBackClick, Controller controller) {
        super(new ArrayList<>(), onBackClick, controller); // Inizializza con una lista vuota finché non viene scelto lo slideshow

        descriptionArea = createDescriptionArea();

        JPanel southContextPanel = new JPanel(new BorderLayout());
        southContextPanel.add(pannelloBottoni, BorderLayout.CENTER);

        JScrollPane descriptionScrollPane = createDescriptionScrollPanel(descriptionArea);
        descriptionScrollPane.setBorder(BorderFactory.createTitledBorder("Descrizione Slideshow"));
        southContextPanel.add(descriptionScrollPane, BorderLayout.EAST);

        impostaContenutoSud(southContextPanel);

        thumbnailStrip = new JScrollPane();
        thumbnailStrip.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        thumbnailStrip.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(thumbnailStrip, BorderLayout.EAST);
    }

    /**
     * Aggiorna il pannello con i dati del video selezionato, inclusi i contenuti
     * multimediali, la descrizione e la colonna delle miniature.
     * @param slideshow L'oggetto {@link Video} da visualizzare.
     */
    public void setSlideshow(Video slideshow) {
        if (slideshow == null) {
            return;
        }

        setContent(slideshow.getCompostoDaFoto());

        if (slideshow.getDescrizione() != null && !slideshow.getDescrizione().isEmpty()) {
            descriptionArea.setText(slideshow.getDescrizione());
        } else {
            descriptionArea.setText("Nessuna descrizione disponibile per questo video.");
        }

        thumbnailStrip.setViewportView(createThumbnailContent(slideshow.getCompostoDaFoto()));

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
        Component.setPreferredSize(new Dimension(400, 100));
        Component.setMinimumSize(new Dimension(200, 100));
        return descriptionScrollPane;
    }

    /**
     * Genera il contenuto della striscia laterale con l'elenco delle fotografie
     * che compongono lo slideshow.
     * @param fotografie Lista delle {@link Model.Fotografia} contenute nel video.
     * @return {@link JPanel} verticale contenente i metadati delle foto.
     */
    private JPanel createThumbnailContent(ArrayList<Fotografia> fotografie) {
        JPanel thumbPanel = new JPanel();
        thumbPanel.setLayout(new BoxLayout(thumbPanel, BoxLayout.Y_AXIS));
        thumbPanel.setPreferredSize(new Dimension(150, 0));
        thumbPanel.setBackground(Color.LIGHT_GRAY);

        if (fotografie != null) {
            for (Fotografia foto : new ArrayList<>(fotografie)) {
                JLabel thumbLabel = new JLabel(controller.formattaThumbnailVideoHtml(foto));
                thumbLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                thumbPanel.add(thumbLabel);
            }
        }
        return thumbPanel;
    }
}