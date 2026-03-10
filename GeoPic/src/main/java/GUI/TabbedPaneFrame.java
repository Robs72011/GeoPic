package GUI;

import Controller.Controller;

import javax.swing.*;
import java.awt.*;



public class TabbedPaneFrame extends JFrame {
    //Costanti per le grandezze del frame
    public static final int FRAME_WIDTH = 1050;
    public static final int FRAME_HEIGHT = 700;
    public static final int FRAME_MIN_WIDTH = 700;
    public static final int FRAME_MIN_HEIGHT = 400;

    /**
     * Crea la finestra principale della GUI che contiene al suo interno un JTabbedPane.
     * Si tratta di un componente che permette di gestire le schermate (JPanel) trattandole come delle schede.
     * Nello specifico, una scheda dedicata per gestire la galleria personale({@link GalleryPanelContainer}),
     * un'altra per gestire le gallerie condivise.
     */
    public TabbedPaneFrame(Controller controller) {
        setTitle("GeoPic");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setMinimumSize(new Dimension(FRAME_MIN_WIDTH, FRAME_MIN_HEIGHT));
        JTabbedPane tabbedPane = new JTabbedPane();

        //Dovrà essere sostituito da un apposito container.
        JPanel SharedGalleryContainer = new JPanel(); //
        SharedGalleryContainer.add(new JLabel("Galleria Condivisa"));

        //Questa è una descrizione fittizzia per provare se il windget per la degli slideshow funzioni.
        //è temporanea dovrà poi essere sostituita dalla logica di recupero dal db.
        //String descrizione = "DESCRIZIONE SLIDESHOW";

        //Schede del frame
        tabbedPane.addTab("Galleria Personale", new GalleryPanelContainer(controller));
        tabbedPane.addTab("Galleria Condivisa", SharedGalleryContainer);

        add(tabbedPane);
        setVisible(true);
    }
}
