package GUI.contenitore;

import GUI.frame.FinestraVisualizzatoreFoto;
import GUI.frame.FinestraVisualizzatoreSlideshow;
import GUI.panel.PannelloGalleria;

import Controller.Controller;
import Model.Fotografia;
import Model.Video;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

public class ContenitoreGalleriaPrivata extends ContenitoreGalleria {
    public ContenitoreGalleriaPrivata(Controller controller) {
        super(controller);
        refresh(); // Effettua la prima costruzione
    }

    @Override
    public void refresh() {
        beginRefresh();

        List<Fotografia> foto = new ArrayList<>(controller.getFotoGalleriaPersonale());
        List<Video> video = controller.getVideoGalleriaPersonale();

        PannelloGalleria pannelloGalleria = new PannelloGalleria(
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

    private void apriFrameFoto(List<Fotografia> foto, int clickedIndex) {
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

    private void apriFrameSlideshow(Video slideshow) {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parent == null || slideshow == null) {
            return;
        }

        FinestraVisualizzatoreSlideshow frameSlideshow = new FinestraVisualizzatoreSlideshow(
                controller,
                parent,
                slideshow,
                this::refresh
        );
        frameSlideshow.setLocationRelativeTo(parent);
        frameSlideshow.setVisible(true);
        parent.setVisible(false);
    }
}
