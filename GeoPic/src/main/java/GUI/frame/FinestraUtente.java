package GUI.frame;

import Controller.Controller;
import GUI.contenitore.ContenitoreGalleriaPrivata;
import GUI.panel.PannelloSelezioneGallerieCondivise;
import Model.GalleriaCondivisa;

import javax.swing.*;
import java.awt.*;

/**
 * Finestra utente principale con navigazione iniziale a tab.
 */
public class FinestraUtente extends JFrame {
    //Costanti per le grandezze del frame
    public static final int FRAME_WIDTH = 1050;
    public static final int FRAME_HEIGHT = 700;
    public static final int FRAME_MIN_WIDTH = 700;
    public static final int FRAME_MIN_HEIGHT = 400;

    private final Controller controller;
    private final ContenitoreGalleriaPrivata contenitoreGalleriaPrivata;
    private final PannelloSelezioneGallerieCondivise pannelloGallerieCondivise;

    /**
     * Costruisce la finestra principale dell'applicazione.
     * Inizializza il layout, imposta le dimensioni minime e aggiunge le schede
     * @param controller Il {@link Controller} di riferimento utilizzato dalle
     * viste figlie per le operazioni di recupero dati.
     */
    public FinestraUtente(Controller controller) {
        this.controller = controller;

        setTitle("GeoPic");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setMinimumSize(new Dimension(FRAME_MIN_WIDTH, FRAME_MIN_HEIGHT));

        JTabbedPane tabbedPane = new JTabbedPane();

        contenitoreGalleriaPrivata = new ContenitoreGalleriaPrivata(this.controller);
        pannelloGallerieCondivise = new PannelloSelezioneGallerieCondivise(this.controller, this::apriDettaglioGalleriaCondivisa);

        tabbedPane.addTab("Galleria Personale", contenitoreGalleriaPrivata);
        tabbedPane.addTab("Galleria Condivisa", pannelloGallerieCondivise);

        tabbedPane.addChangeListener(_ -> {
            int idx = tabbedPane.getSelectedIndex();
            if (idx == 0) {
                contenitoreGalleriaPrivata.refresh();
            } else if (idx == 1) {
                pannelloGallerieCondivise.refresh();
            }
        });

        add(tabbedPane);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void apriDettaglioGalleriaCondivisa(GalleriaCondivisa galleriaCondivisa) {
        if (galleriaCondivisa == null) {
            return;
        }

        FinestraGalleriaCondivisa dettaglio = new FinestraGalleriaCondivisa(controller, this, galleriaCondivisa);
        dettaglio.setLocationRelativeTo(this);
        dettaglio.setVisible(true);
        setVisible(false);
    }
}
