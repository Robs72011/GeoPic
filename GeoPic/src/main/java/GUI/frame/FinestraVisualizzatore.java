package GUI.frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Base comune per i frame di visualizzazione media con ritorno automatico al frame padre.
 */
public abstract class FinestraVisualizzatore extends JFrame {

    private final JFrame parentFrame;
    private final Runnable onParentRefresh;

    protected FinestraVisualizzatore(String titolo,
                                     JFrame parentFrame,
                                     Runnable onParentRefresh) {
        this.parentFrame = parentFrame;
        this.onParentRefresh = onParentRefresh;

        setTitle(titolo);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(FinestraUtente.FRAME_WIDTH, FinestraUtente.FRAME_HEIGHT);
        setMinimumSize(new Dimension(FinestraUtente.FRAME_MIN_WIDTH, FinestraUtente.FRAME_MIN_HEIGHT));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                tornaAlPadre();
            }
        });
    }

    protected final Runnable createBackAction() {
        return this::tornaAlPadre;
    }

    protected final void setViewComponent(Component component) {
        add(component, BorderLayout.CENTER);
    }

    protected final void tornaAlPadre() {
        if (onParentRefresh != null) {
            onParentRefresh.run();
        }
        if (parentFrame != null) {
            parentFrame.setVisible(true);
        }
        dispose();
    }
}
