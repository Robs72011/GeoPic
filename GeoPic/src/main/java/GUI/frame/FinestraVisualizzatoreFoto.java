package GUI.frame;

import Controller.Controller;
import GUI.panel.PannelloVisualizzatoreFoto;
import Model.Fotografia;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Frame dedicato alla visualizzazione dettagliata delle fotografie.
 * Alla chiusura restituisce il controllo al frame padre.
 */
public class FinestraVisualizzatoreFoto extends FinestraVisualizzatore {

    public FinestraVisualizzatoreFoto(Controller controller,
                                      JFrame parentFrame,
                                      ArrayList<Fotografia> foto,
                                      int indiceIniziale,
                                      Runnable onParentRefresh) {
        super("Dettaglio Fotografia - GeoPic", parentFrame, onParentRefresh);

        PannelloVisualizzatoreFoto visualizzatore = new PannelloVisualizzatoreFoto(
                foto,
                createBackAction(),
                controller
        );
        visualizzatore.mostraMetadati(indiceIniziale);

        setViewComponent(visualizzatore);
    }
}
