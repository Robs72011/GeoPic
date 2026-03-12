package GUI.contenitore;

import Controller.Controller;

import javax.swing.*;
import java.awt.*;

/**
 * Classe base astratta per la visualizzazione delle gallerie sotto forma di contenitore 
 * con layout a schede (CardLayout).
 * Definisce l'infrastruttura di base per i pannelli delle foto.
 */
public abstract class ContenitoreGalleria extends JPanel {
    // Costanti per definire i pannelli comuni nel CardLayout
    protected static final String GRID = "grid";
    protected static final String DETAIL = "detail";

    protected final CardLayout cardLayout;
    protected final Controller controller;

    public ContenitoreGalleria(Controller controller) {
        this.controller = controller;
        cardLayout = new CardLayout();
        setLayout(cardLayout);
    }

    /**
     * Metodo astratto che deve essere implementato dalle classi derivate per
     * caricare i dati specifici (personali o condivisi) e ricostruire i pannelli.
     */
    public abstract void refresh();
}