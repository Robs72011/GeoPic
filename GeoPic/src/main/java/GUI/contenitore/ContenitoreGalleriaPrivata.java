package GUI.contenitore;

import GUI.panel.PannelloGalleria;
import GUI.visualizzatore.VisualizzatoreFoto;
import GUI.visualizzatore.VisualizzatoreSlideshow;

import Controller.Controller;
import Model.Fotografia;
import Model.Video;

import javax.swing.*;
import java.util.List;

public class ContenitoreGalleriaPrivata extends ContenitoreGalleria {
    private static final String SLIDESHOW = "slideshow";

    public ContenitoreGalleriaPrivata(Controller controller) {
        super(controller);
        refresh(); // Effettua la prima costruzione
    }

    @Override
    public void refresh() {
        removeAll();

        List<Fotografia> foto = controller.getFotoGalleriaPersonale();
        List<Video> video = controller.getVideoGalleriaPersonale();

        if (foto == null || video == null) {
            JOptionPane.showMessageDialog(null,
                    "Nessuna fotografia o video trovato nella galleria personale",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        VisualizzatoreFoto imageSelector = new VisualizzatoreFoto(foto, () -> cardLayout.show(this, GRID));
        VisualizzatoreSlideshow slideshowSelector = new VisualizzatoreSlideshow(() -> cardLayout.show(this, GRID));

        PannelloGalleria pannelloGalleria = new PannelloGalleria(
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

        add(pannelloGalleria, GRID);
        add(imageSelector, DETAIL);
        add(slideshowSelector, SLIDESHOW);

        cardLayout.show(this, GRID);
        
        revalidate();
        repaint();
    }
}
