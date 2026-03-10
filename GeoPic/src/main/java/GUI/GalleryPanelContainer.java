package GUI;

import Controller.Controller;
import Model.Fotografia;
import Model.Video;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Gestisce la navigazione tra le diverse viste della galleria tramite un {@link CardLayout}.
 * Questa classe agisce come un contenitore principale che alterna i pannelli:
 * {@link GalleryPanel}, la dashboard principale.
 * {@link ImageSelector}, per la visualizzazione in dettaglio delle foto.
 * {@link SlideshowSelector}, per la riproduzione di contenuti video.
 */
public class GalleryPanelContainer extends JPanel {
    //costanti per definire i pannelli nel cardlayout
    private static final String GRID = "grid";
    private static final String DETAIL = "detail";
    private static final String SLIDESHOW = "slideshow";

    private final CardLayout cardLayout;

    /**
     * Inizializza il contenitore configurando i vari pannelli e definendo la logica
     * di transizione tra di essi tramite callback.
     * @param controller Il {@link Controller} utilizzato per il recupero delle
     * risorse multimediali dell'utente loggato.
     */
    public GalleryPanelContainer(Controller controller) {
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        //logica di caricamento immagini da
        List<Fotografia> foto = controller.getFotoGalleriaPersonale();
        List<Video> video = controller.getVideoGalleriaPersonale();

        if (foto == null || video == null) {
            JOptionPane.showMessageDialog(null,
                    "Nessuna fotografia o video trovato nella galleria personale",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }

        // Pannello per visualizzare le immagini ingrandite
        ImageSelector imageSelector = new ImageSelector(() -> cardLayout.show(this, GRID), controller);
        imageSelector.setContent(foto); //set dinamico del contenuto da mostrare


        // Pannello per visualizzare le slideshow
        SlideshowSelector slideshowSelector = new SlideshowSelector(() -> cardLayout.show(this, GRID), controller);

        // Pannello galleria con listener per immagini e slideshow
        GalleryPanel galleryPanel = new GalleryPanel(
                foto,
                clickedIndex -> {
                    cardLayout.show(this, DETAIL);
                    imageSelector.mostraMetadati(clickedIndex);
                },
                video,
                slideshow -> {
                    cardLayout.show(this, SLIDESHOW);
                    slideshowSelector.setSlideshow(slideshow);
                },
                controller
        );

        add(galleryPanel, GRID);
        add(imageSelector, DETAIL);
        add(slideshowSelector, SLIDESHOW);

        cardLayout.show(this, GRID);
    }
}