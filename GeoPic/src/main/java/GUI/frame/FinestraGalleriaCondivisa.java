package GUI.frame;

import Controller.Controller;
import GUI.contenitore.ContenitoreGalleriaCondivisa;
import Model.GalleriaCondivisa;

import javax.swing.*;
import java.awt.*;

/**
 * Finestra dedicata alla visualizzazione di una singola galleria condivisa.
 */
public class FinestraGalleriaCondivisa extends JFrame {

    public FinestraGalleriaCondivisa(Controller controller, GalleriaCondivisa galleriaCondivisa) {
        setTitle("Galleria Condivisa - " + galleriaCondivisa.getNomeGalleria());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(FinestraUtente.FRAME_WIDTH, FinestraUtente.FRAME_HEIGHT);
        setMinimumSize(new Dimension(FinestraUtente.FRAME_MIN_WIDTH, FinestraUtente.FRAME_MIN_HEIGHT));

        add(new ContenitoreGalleriaCondivisa(controller, galleriaCondivisa));
    }
}
