package GUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;
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
    public GalleryPanelContainer(List<File> immagini, List<Slideshow> slideshows) {
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        //logica di caricamento immagini da sostituire successivamente
        if (immagini == null || immagini.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nessuna immagine trovata nella cartella.");
            return;
        }

        // Pannello per visualizzare le immagini ingrandite
        ImageSelector imageSelector = new ImageSelector(() -> cardLayout.show(this, GRID));
        imageSelector.setContent(immagini); //set dinamico del contenuto da mostrare


        // Pannello per visualizzare le slideshow
        SlideshowSelector slideshowSelector = new SlideshowSelector(() -> cardLayout.show(this, GRID));

        // Pannello galleria con listener per immagini e slideshow
        GalleryPanel galleryPanel = new GalleryPanel(
                immagini,
                clickedIndex -> {
                    cardLayout.show(this, DETAIL);
                    imageSelector.mostraImmagine(clickedIndex);
                },
                slideshows,
                slideshow -> {
                    cardLayout.show(this, SLIDESHOW);
                    slideshowSelector.setSlideshow(slideshow);
                }
        );

        add(galleryPanel, GRID);
        add(imageSelector, DETAIL);
        add(slideshowSelector, SLIDESHOW);

        cardLayout.show(this, GRID);
    }
}