package GUI.contenitore;

import GUI.frame.FinestraVisualizzatoreFoto;
import GUI.frame.FinestraVisualizzatoreSlideshow;
import GUI.panel.PannelloGalleriaPrivata;

import Controller.Controller;
import Model.Fotografia;
import Model.Video;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.ArrayList;

public class ContenitoreGalleriaPrivata extends ContenitoreGalleria {
    public ContenitoreGalleriaPrivata(Controller controller) {
        super(controller);
        refresh(); // Effettua la prima costruzione
    }

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