package GUI.frame;

import Controller.Controller;
import GUI.panel.PannelloVisualizzatoreSlideshow;
import Model.Video;

import javax.swing.*;

/**
 * Frame dedicato alla visualizzazione di uno slideshow video.
 * Alla chiusura restituisce il controllo al frame padre.
 */
public class FinestraVisualizzatoreSlideshow extends FinestraVisualizzatore {

    public FinestraVisualizzatoreSlideshow(Controller controller,
                                           JFrame parentFrame,
                                           Video slideshow,
                                           Runnable onParentRefresh) {
        super("Visualizzazione Slideshow - GeoPic", parentFrame, onParentRefresh);

        PannelloVisualizzatoreSlideshow visualizzatore = new PannelloVisualizzatoreSlideshow(
                createBackAction(),
                controller
        );
        visualizzatore.setSlideshow(slideshow);

        setViewComponent(visualizzatore);
    }
}
