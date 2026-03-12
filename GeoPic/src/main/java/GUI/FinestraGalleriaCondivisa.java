package GUI;

import Controller.Controller;
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
        setSize(FinestraPrincipale.FRAME_WIDTH, FinestraPrincipale.FRAME_HEIGHT);
        setMinimumSize(new Dimension(FinestraPrincipale.FRAME_MIN_WIDTH, FinestraPrincipale.FRAME_MIN_HEIGHT));

        add(new ContenitoreGalleriaCondivisa(controller, galleriaCondivisa));
    }
}
