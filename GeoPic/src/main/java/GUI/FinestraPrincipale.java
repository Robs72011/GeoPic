package GUI;

import Controller.Controller;

import javax.swing.*;
import java.awt.*;

/**
 * Finestra principale dell'applicazione che gestisce la navigazione tra le macro-aree
 * tramite un {@link JTabbedPane}.
 * La classe organizza l'interfaccia utente in schede distinte, permettendo all'utente
 * di passare rapidamente tra la propria galleria personale e le gallerie condivise.
 */
public class FinestraPrincipale extends JFrame {
    //Costanti per le grandezze del frame
    public static final int FRAME_WIDTH = 1050;
    public static final int FRAME_HEIGHT = 700;
    public static final int FRAME_MIN_WIDTH = 700;
    public static final int FRAME_MIN_HEIGHT = 400;

    /**
     * Costruisce la finestra principale dell'applicazione.
     * Inizializza il layout, imposta le dimensioni minime e aggiunge le schede
     * {@link ContenitoreGalleria} per la gestione dei contenuti multimediali.
     * @param controller Il {@link Controller} di riferimento utilizzato dalle
     * viste figlie per le operazioni di recupero dati.
     */
    public FinestraPrincipale(Controller controller) {
        setTitle("GeoPic");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setMinimumSize(new Dimension(FRAME_MIN_WIDTH, FRAME_MIN_HEIGHT));
        JTabbedPane tabbedPane = new JTabbedPane();

        //Schede del frame
        tabbedPane.addTab("Galleria Personale", new ContenitoreGalleriaPrivata(controller));
        tabbedPane.addTab("Galleria Condivisa", new SchedaSelezioneGallerieCondivise(controller));

        add(tabbedPane);
        setVisible(true);
    }
}
