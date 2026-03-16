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

    public ContenitoreGalleriaCondivisa(Controller controller, GalleriaCondivisa galleriaCondivisa) {
        super(controller);
        this.galleriaCondivisa = galleriaCondivisa;
        refresh(); // Effettua la prima costruzione
    }

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
