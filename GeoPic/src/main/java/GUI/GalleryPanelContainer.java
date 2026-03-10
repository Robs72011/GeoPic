package GUI;

import Controller.Controller;
import Model.Fotografia;
import Model.Video;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GalleryPanelContainer extends JPanel {
    //costanti per definire i pannelli nel cardlayout
    private static final String GRID = "grid";
    private static final String DETAIL = "detail";
    private static final String SLIDESHOW = "slideshow";

    private final CardLayout cardLayout;

    /**
     * Gestisce il passaggio di schermata da {@link GalleryPanel}(Dashboard Galleria) a
     * {@link ImageSelector}(Visione Contenuto in dettaglio)
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