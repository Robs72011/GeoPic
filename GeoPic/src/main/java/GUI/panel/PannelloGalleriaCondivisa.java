package GUI.panel;

import Controller.Controller;
import GUI.dialog.DialogAggiungiFotoGalleriaCondivisa;
import Model.Fotografia;
import Model.GalleriaCondivisa;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.IntConsumer;

/**
 * Implementazione concreta del pannello galleria per una galleria condivisa.
 */
public class PannelloGalleriaCondivisa extends PannelloGalleria {

    private final GalleriaCondivisa galleriaCondivisa;

    public PannelloGalleriaCondivisa(ArrayList<Fotografia> fotografie,
                                     IntConsumer onImageClick,
                                     Controller controller,
                                     Runnable onRefresh,
                                     GalleriaCondivisa galleriaCondivisa) {
        super(fotografie, onImageClick, controller, onRefresh);
        this.galleriaCondivisa = galleriaCondivisa;

        String titolo = controller.getTitoloGalleriaCondivisa(galleriaCondivisa);
        String sottotitolo = controller.getSottotitoloGalleriaCondivisa(galleriaCondivisa);

        inizializzaPannello(
                titolo,
                sottotitolo,
                new ArrayList<>(),
                _ -> {// Nessuno slideshow nel pannello condiviso.
                }
        );
    }

    @Override
    protected JPanel creaPannelloComandiHeader() {
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JButton btnAggiungiFoto = createStyledButton("Aggiungi Foto", _ -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            Frame owner = parentWindow instanceof Frame ? (Frame) parentWindow : null;

            DialogAggiungiFotoGalleriaCondivisa dialog = new DialogAggiungiFotoGalleriaCondivisa(owner, controller, this.galleriaCondivisa);
            dialog.setVisible(true);

            if (onRefresh != null) {
                onRefresh.run();
            }
        });
        btnAggiungiFoto.setAlignmentX(Component.RIGHT_ALIGNMENT);
        rightPanel.add(btnAggiungiFoto);

        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.setOpaque(false);
        rightContainer.add(rightPanel, BorderLayout.NORTH);
        return rightContainer;
    }
}
