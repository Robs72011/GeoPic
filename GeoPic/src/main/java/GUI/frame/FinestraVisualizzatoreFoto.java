package GUI.frame;

import Controller.Controller;
import GUI.panel.PannelloVisualizzatoreFoto;
import Model.Fotografia;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Frame dedicato alla visualizzazione dettagliata delle fotografie.
 * Alla chiusura restituisce il controllo al frame padre.
 */
public class FinestraVisualizzatoreFoto extends JFrame {

    private final JFrame parentFrame;
    private final Runnable onParentRefresh;

    public FinestraVisualizzatoreFoto(Controller controller,
                                      JFrame parentFrame,
                                      List<Fotografia> foto,
                                      int indiceIniziale,
                                      Runnable onParentRefresh) {
        this.parentFrame = parentFrame;
        this.onParentRefresh = onParentRefresh;

        setTitle("Dettaglio Fotografia - GeoPic");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(FinestraUtente.FRAME_WIDTH, FinestraUtente.FRAME_HEIGHT);
        setMinimumSize(new Dimension(FinestraUtente.FRAME_MIN_WIDTH, FinestraUtente.FRAME_MIN_HEIGHT));

        PannelloVisualizzatoreFoto visualizzatore = new PannelloVisualizzatoreFoto(
                foto,
                this::tornaAlPadre,
            controller
        );
        visualizzatore.mostraMetadati(indiceIniziale);

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
