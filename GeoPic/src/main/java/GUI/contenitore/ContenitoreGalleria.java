package GUI.contenitore;

import Controller.Controller;

import javax.swing.*;
import java.awt.*;

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
     * Metodo astratto che deve essere implementato dalle classi derivate per
     * caricare i dati specifici e ricostruire l'interfaccia.
     */
    public abstract void refresh();
}