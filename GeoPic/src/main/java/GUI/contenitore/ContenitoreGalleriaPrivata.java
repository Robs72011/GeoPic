package GUI.contenitore;

import GUI.frame.FinestraVisualizzatoreSlideshow;
import GUI.panel.PannelloGalleriaPrivata;

import Controller.Controller;
import Model.Fotografia;
import Model.Video;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.ArrayList;

/**
 * Componente grafico che gestisce la visualizzazione della galleria privata di un utente.
 * Estende {@link ContenitoreGalleria} per ereditare la logica comune di gestione dei contenitori.
 * Questa classe si occupa di recuperare dal controller le fotografie e i video personali,
 * inizializzare il {@link PannelloGalleriaPrivata} e gestire l'apertura delle finestre
 * di dettaglio per il visualizzatore di slideshow.
 */
public class ContenitoreGalleriaPrivata extends ContenitoreGalleria {

    /**
     * Costruisce il contenitore per la galleria privata e avvia il primo rendering.
     * @param controller Il controller di sistema utilizzato per interagire con il modello.
     */
    public ContenitoreGalleriaPrivata(Controller controller) {
        super(controller);
        refresh(); // Effettua la prima costruzione
    }

    /**
     * Aggiorna l'interfaccia grafica del contenitore recuperando i dati aggiornati
     * dal controller e rigenerando il pannello interno {@link PannelloGalleriaPrivata}.
     */
    @Override
    public void refresh() {
        beginRefresh();

        ArrayList<Fotografia> foto = new ArrayList<>(controller.getFotoGalleriaPersonale());
        ArrayList<Video> video = controller.getVideoGalleriaPersonale();

        PannelloGalleriaPrivata pannelloGalleria = new PannelloGalleriaPrivata(
                foto,
                clickedIndex -> apriFrameFoto(foto, clickedIndex),
                video,
                this::apriFrameSlideshow,
                controller,
                this::refresh
        );

        add(pannelloGalleria, BorderLayout.CENTER);

        finalizeRefresh();
    }

    /**
     * Apre una finestra di visualizzazione per uno slideshow video specifico.
     * * @param slideshow L'oggetto {@link Video} da visualizzare.
     */
    private void apriFrameSlideshow(Video slideshow) {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (slideshow == null) {
            return;
        }

        FinestraVisualizzatoreSlideshow frameSlideshow = new FinestraVisualizzatoreSlideshow(
                controller,
                parent,
                slideshow,
                this::refresh
        );
        apriFinestraFiglia(frameSlideshow);
    }
}