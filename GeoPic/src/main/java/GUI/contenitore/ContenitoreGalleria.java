package GUI.contenitore;

import Controller.Controller;
import GUI.frame.FinestraVisualizzatoreFoto;
import Model.Fotografia;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Classe base astratta per la visualizzazione delle gallerie sotto forma di contenitore.
 * Definisce l'infrastruttura di base per i pannelli delle foto.
 */
public abstract class ContenitoreGalleria extends JPanel {

    protected final Controller controller;

    public ContenitoreGalleria(Controller controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
    }

    protected void beginRefresh() {
        removeAll();
    }

    protected void finalizeRefresh() {
        revalidate();
        repaint();
    }

    /**
     * Metodo generico per aprire una finestra figlia (come un visualizzatore di foto o slideshow),
     * nascondendo la finestra genitore.
     * @param childWindow La finestra (JFrame, JDialog) da rendere visibile.
     */
    protected void apriFinestraFiglia(Window childWindow) {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parent == null || childWindow == null) {
            return;
        }
        childWindow.setLocationRelativeTo(parent);
        childWindow.setVisible(true);
        parent.setVisible(false);
    }

    /**
     * Apre la finestra di visualizzazione per una specifica foto.
     * Questo metodo è centralizzato qui per evitare duplicazione nelle sottoclassi.
     * @param foto La lista di foto da visualizzare.
     * @param clickedIndex L'indice della foto su cui si è fatto clic.
     */
    protected void apriFrameFoto(ArrayList<Fotografia> foto, int clickedIndex) {
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
        apriFinestraFiglia(dettaglio);
    }

    /**
     * Metodo astratto che deve essere implementato dalle classi derivate per
     * caricare i dati specifici e ricostruire l'interfaccia.
     */
    public abstract void refresh();
}
