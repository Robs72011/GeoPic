package GUI.frame;

import Controller.Controller;
import GUI.contenitore.ContenitoreGalleriaCondivisa;
import Model.GalleriaCondivisa;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Frame di dettaglio per una galleria condivisa.
 * Restituisce il controllo al frame lista gallerie condivise.
 */
public class FinestraGalleriaCondivisa extends JFrame {

    private final JFrame parentFrame;
    private final ContenitoreGalleriaCondivisa contenitore;

    public FinestraGalleriaCondivisa(Controller controller, JFrame parentFrame, GalleriaCondivisa galleriaCondivisa) {
        this.parentFrame = parentFrame;

        String nomeGalleria = controller.getNomeSempliceGalleriaCondivisa(galleriaCondivisa);
        setTitle("Galleria Condivisa - " + nomeGalleria);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(FinestraUtente.FRAME_WIDTH, FinestraUtente.FRAME_HEIGHT);
        setMinimumSize(new Dimension(FinestraUtente.FRAME_MIN_WIDTH, FinestraUtente.FRAME_MIN_HEIGHT));

        contenitore = new ContenitoreGalleriaCondivisa(controller, galleriaCondivisa);

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnIndietro = new JButton("< Indietro");
        btnIndietro.addActionListener(_ -> tornaAlPadre());

        JLabel titolo = new JLabel(" Torna alla selezione", SwingConstants.LEFT);
        titolo.setFont(new Font("Arial", Font.BOLD, 15));

        header.add(btnIndietro, BorderLayout.WEST);
        header.add(titolo, BorderLayout.CENTER);

        JPanel root = new JPanel(new BorderLayout());
        root.add(header, BorderLayout.NORTH);
        root.add(contenitore, BorderLayout.CENTER);
        add(root);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                tornaAlPadre();
            }
        });
    }

    private void tornaAlPadre() {
        contenitore.refresh();
        if (parentFrame != null) {
            parentFrame.repaint();
            parentFrame.setVisible(true);
        }
        dispose();
    }
}
