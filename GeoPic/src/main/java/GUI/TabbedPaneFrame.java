package GUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


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
    public TabbedPaneFrame() {
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

        File cartella = new File("GeoPic/FOTO");
        List<File> immagini = caricaImmaginiDaCartella(cartella);

        String descrizione = "DESCRIZIONE SLIDESHOW";

        List<Slideshow> raccolte = List.of(
                new Slideshow("1", "Gita al parco", caricaImmaginiDaCartella( new File("GeoPic/FOTO/SLIDESHOW1")) , descrizione+"1"),
                new Slideshow("2", "Festa feega", caricaImmaginiDaCartella(new File ("GeoPic/FOTO/SLIDESHOW2")), descrizione+"2")
        );

        //Schede del frame
        tabbedPane.addTab("Galleria Personale", new GalleryPanelContainer(immagini, raccolte));
        tabbedPane.addTab("Galleria Condivisa", SharedGalleryContainer);

        add(tabbedPane);
        setVisible(true);
    }
    //questo metodo andrà reimplementato per il caricamento da db
    private List<File> caricaImmaginiDaCartella(File cartella) {
        File[] files = cartella.listFiles((_, name) ->
                name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png")
        );
        if (files == null) return new ArrayList<>();
        return List.of(files);
    }
}
