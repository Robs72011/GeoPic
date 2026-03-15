package GUI.contenitore;

import GUI.frame.FinestraVisualizzatoreFoto;
import GUI.panel.PannelloGalleria;
import Controller.Controller;
import Model.Fotografia;
import Model.GalleriaCondivisa;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;

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

        ArrayList<Fotografia> foto = new ArrayList<>(galleriaCondivisa.getFotoContenute());

        PannelloGalleria pannelloGalleria = new PannelloGalleria(
                foto,
                clickedIndex -> apriFrameFoto(foto, clickedIndex),
                Collections.emptyList(),
                _ -> {
                    // Nessun video nei panel privati
                },
                controller,
                this::refresh,
                "Galleria Condivisa: " + galleriaCondivisa.getNomeGalleria(),
                "ID: " + galleriaCondivisa.getIdGalleria(),
                true,
                false,
                PannelloGalleria.createAddPhotoToSharedGalleryAction(
                    this,
                    controller,
                    galleriaCondivisa,
                    this::refresh
                )
        );

        add(pannelloGalleria, BorderLayout.CENTER);

        finalizeRefresh();
    }

    private void apriFrameFoto(ArrayList<Fotografia> foto, int clickedIndex) {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parent == null) {
            return;
        }

        FinestraVisualizzatoreFoto dettaglio = new FinestraVisualizzatoreFoto(
                controller,
                parent,
                new ArrayList<>(foto),
                clickedIndex,
                this::refresh
        );
        dettaglio.setLocationRelativeTo(parent);
        dettaglio.setVisible(true);
        parent.setVisible(false);
    }
}