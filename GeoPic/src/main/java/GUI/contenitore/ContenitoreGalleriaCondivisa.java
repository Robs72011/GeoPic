package GUI.contenitore;

import GUI.panel.PannelloGalleria;
import GUI.panel.PannelloGalleriaCondivisa;
import Controller.Controller;
import Model.Fotografia;
import Model.GalleriaCondivisa;

import java.awt.BorderLayout;
import java.util.ArrayList;

/**
 * Contenitore per la navigazione interna di una galleria condivisa.
 * Mantiene la stessa interfaccia base della galleria personale (GalleryPanelContainer).
 */
public class ContenitoreGalleriaCondivisa extends ContenitoreGalleria {
    private final GalleriaCondivisa galleriaCondivisa;

    /**
     * Costruisce il contenitore per la galleria condivisa specificata.
     * @param controller        Il controller di sistema per le interazioni.
     * @param galleriaCondivisa L'istanza di {@link GalleriaCondivisa} da visualizzare.
     */
    public ContenitoreGalleriaCondivisa(Controller controller, GalleriaCondivisa galleriaCondivisa) {
        super(controller);
        this.galleriaCondivisa = galleriaCondivisa;
        refresh(); // Effettua la prima costruzione
    }

    /**
     * Aggiorna l'interfaccia grafica recuperando dal controller le fotografie
     * contenute nella galleria condivisa corrente e rigenerando il pannello di visualizzazione.
     */
    @Override
    public void refresh() {
        beginRefresh();

        ArrayList<Fotografia> foto = controller.getFotoDaGalleriaCondivisa(galleriaCondivisa);

        PannelloGalleria pannelloGalleria = new PannelloGalleriaCondivisa(
                foto,
                clickedIndex -> apriFrameFoto(foto, clickedIndex),
                controller,
                this::refresh,
                galleriaCondivisa
        );

        add(pannelloGalleria, BorderLayout.CENTER);

        finalizeRefresh();
    }

}
