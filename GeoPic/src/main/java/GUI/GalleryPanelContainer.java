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

    private final Controller controller;

    /**
     * Inizializza il contenitore configurando i vari pannelli e definendo la logica
     * di transizione tra di essi tramite callback.
     * @param controller Il {@link Controller} utilizzato per il recupero delle
     * risorse multimediali dell'utente loggato.
     */
    public GalleryPanelContainer(Controller controller) {
        this.controller = controller;
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        // Effettua la prima costruzione grafica del contenitore
        refresh();
    }

    /**
     * Aggiorna l'intero contenitore della galleria ricostruendo le sue viste interne.
     * Viene chiamato inizialmente dal costruttore e, successivamente, 
     * come callback (onRefresh) dopo eventuali azioni che alterano i dati
     * (es. l'aggiunta di una nuova foto tramite AggiungiFotoDialog).
     * Il metodo:
     * 1) Svuota l'interfaccia corrente eliminando i vecchi componenti.
     * 2) Interroga il controller per ottenere le liste (di foto e video) allo stato attuale.
     * 3) Reinizializza tutti i pannelli interni passandogli i nuovi dati.
     * 4) Rende visibile il pannello a "griglia" e comunica allo Swing manager di ridisegnare l'area.
     */
    public void refresh() {
        // Rimuove dal CardLayout le istanze precedenti di GalleryPanel, ImageSelector e SlideshowSelector
        removeAll();

        // Recupera i dati freschi direttamente dal Data Graph in memoria tramite il Controller
        List<Fotografia> foto = controller.getFotoGalleriaPersonale();
        List<Video> video = controller.getVideoGalleriaPersonale();

        if (foto == null || video == null) {
            JOptionPane.showMessageDialog(null,
                    "Nessuna fotografia o video trovato nella galleria personale",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }

        // --- Ricostruisce le istanze dei tre sotto-pannelli passando i dati e le callback di navigazione ---

        // Pannello per visualizzare le immagini ingrandite (Detail View)
        ImageSelector imageSelector = new ImageSelector(() -> cardLayout.show(this, GRID), controller);
        imageSelector.setContent(foto); // set dinamico del contenuto aggiornato da mostrare

        // Pannello per visualizzare le slideshow
        SlideshowSelector slideshowSelector = new SlideshowSelector(() -> cardLayout.show(this, GRID), controller);

        // Pannello galleria (la griglia vera e propria) con listener per i clic su immagini e slideshow.
        // Gli viene passata this::refresh come 'Runnable onRefresh', che permetterà ai componenti
        // annidati (es. Header) di triggerare nuovamente la ricostruzione della griglia ai successivi inserimenti.
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
                controller,
                this::refresh
        );

        // Aggiunge nuovamente le view aggiornate alla pila del CardLayout
        add(galleryPanel, GRID);
        add(imageSelector, DETAIL);
        add(slideshowSelector, SLIDESHOW);

        // Mostra immediatamente la view a griglia (quella predefinita)
        cardLayout.show(this, GRID);
        
        // Forza a livello Swing il ricalcolo dei layout e il rendering a schermo
        revalidate();
        repaint();
    }
}