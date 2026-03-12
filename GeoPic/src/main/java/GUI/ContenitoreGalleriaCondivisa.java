package GUI;

import Controller.Controller;
import Model.Fotografia;
import Model.GalleriaCondivisa;

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
        removeAll();

        ArrayList<Fotografia> foto = galleriaCondivisa.getFotoContenute();

        VisualizzatoreFoto imageSelector = new VisualizzatoreFoto(foto, () -> cardLayout.show(this, GRID));

        PannelloGalleria pannelloGalleria = new PannelloGalleria(
                foto,
                clickedIndex -> {
                    cardLayout.show(this, DETAIL);
                    imageSelector.mostraMetadati(clickedIndex);
                },
                Collections.emptyList(),
                slideshow -> {
                    // Nessun video nei panel privati
                },
                controller,
                this::refresh, // OnRefresh
                "Galleria Condivisa: " + galleriaCondivisa.getNomeGalleria(),
                "ID: " + galleriaCondivisa.getIdGalleria(),
                false // Non mostrare bottone 'Aggiungi foto'
        );

        add(pannelloGalleria, GRID);
        add(imageSelector, DETAIL);

        cardLayout.show(this, GRID);
        
        revalidate();
        repaint();
    }
}