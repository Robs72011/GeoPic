package GUI.frame;

import Controller.Controller;
import GUI.panel.PannelloVisualizzatoreSlideshow;
import Model.Video;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Frame dedicato alla visualizzazione di uno slideshow video.
 * Alla chiusura restituisce il controllo al frame padre.
 */
public class FinestraVisualizzatoreSlideshow extends JFrame {

    private final JFrame parentFrame;
    private final Runnable onParentRefresh;

    public FinestraVisualizzatoreSlideshow(Controller controller,
                                           JFrame parentFrame,
                                           Video slideshow,
                                           Runnable onParentRefresh) {
        this.parentFrame = parentFrame;
        this.onParentRefresh = onParentRefresh;

        setTitle("Visualizzazione Slideshow - GeoPic");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(FinestraUtente.FRAME_WIDTH, FinestraUtente.FRAME_HEIGHT);
        setMinimumSize(new Dimension(FinestraUtente.FRAME_MIN_WIDTH, FinestraUtente.FRAME_MIN_HEIGHT));

        PannelloVisualizzatoreSlideshow visualizzatore = new PannelloVisualizzatoreSlideshow(
                this::tornaAlPadre,
            controller
        );
        visualizzatore.setSlideshow(slideshow);

        add(visualizzatore, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                tornaAlPadre();
            }
        });
    }

    private void tornaAlPadre() {
        if (onParentRefresh != null) {
            onParentRefresh.run();
        }
        if (parentFrame != null) {
            parentFrame.setVisible(true);
        }
        dispose();
    }
}
