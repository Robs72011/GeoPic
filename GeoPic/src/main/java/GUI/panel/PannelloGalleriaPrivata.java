package GUI.panel;

import Controller.Controller;
import GUI.dialog.DialogAggiungiFoto;
import GUI.dialog.DialogAggiungiVideo;
import Model.Fotografia;
import Model.Video;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * Implementazione concreta del pannello galleria per la galleria personale.
 */
public class PannelloGalleriaPrivata extends PannelloGalleria {

    public PannelloGalleriaPrivata(ArrayList<Fotografia> fotografie,
                                   IntConsumer onImageClick,
                                   ArrayList<Video> slideshowList,
                                   Consumer<Video> onSlideshowClick,
                                   Controller controller,
                                   Runnable onRefresh) {
        super(fotografie, onImageClick, controller, onRefresh);

        inizializzaPannello(
                null,
                null,
                slideshowList,
                onSlideshowClick
        );
    }

    @Override
    protected JPanel creaPannelloComandiHeader() {
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JButton menuAzioni = createStyledButton("Azioni", _ -> {});
        JPopupMenu popupAzioni = new JPopupMenu();

        JMenuItem itemAggiungiFoto = new JMenuItem("Aggiungi Foto");
        itemAggiungiFoto.addActionListener(_ -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            Frame owner = parentWindow instanceof Frame ? (Frame) parentWindow : null;

            DialogAggiungiFoto dialog = new DialogAggiungiFoto(owner, controller);
            dialog.setVisible(true);

            // onRefresh is a protected field from the base class
            if (onRefresh != null) {
                onRefresh.run();
            }
        });
        popupAzioni.add(itemAggiungiFoto);

        JMenuItem itemCreaVideo = new JMenuItem("Crea Video");
        itemCreaVideo.addActionListener(_ -> apriDialogCreaVideo());
        popupAzioni.add(itemCreaVideo);

        popupAzioni.addSeparator();

        JMenuItem itemTop3 = new JMenuItem("Top 3 Luoghi");
        itemTop3.addActionListener(_ ->
                JOptionPane.showMessageDialog(this,
                        "Funzionalità 'Top 3 Luoghi' da implementare!",
                        "Classifica",
                        JOptionPane.INFORMATION_MESSAGE));
        popupAzioni.add(itemTop3);

        menuAzioni.addActionListener(_ -> popupAzioni.show(menuAzioni, 0, menuAzioni.getHeight()));
        menuAzioni.setAlignmentX(Component.RIGHT_ALIGNMENT);
        rightPanel.add(menuAzioni);

        rightPanel.add(Box.createVerticalStrut(8));
        PannelloRicercaFoto pannelloRicerca = new PannelloRicercaFoto(richiesta ->
                eseguiRicercaFoto(richiesta.getTipo(), richiesta.getQuery()));
        pannelloRicerca.setMaximumSize(new Dimension(380, 36));
        pannelloRicerca.setAlignmentX(Component.RIGHT_ALIGNMENT);
        rightPanel.add(pannelloRicerca);

        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.setOpaque(false);
        rightContainer.add(rightPanel, BorderLayout.NORTH);
        return rightContainer;
    }

    private void apriDialogCreaVideo() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        Frame owner = parentWindow instanceof Frame ? (Frame) parentWindow : null;

        DialogAggiungiVideo dialog = new DialogAggiungiVideo(owner, controller);
        dialog.setVisible(true);

        if (onRefresh != null) {
            onRefresh.run();
        }
    }

    private void eseguiRicercaFoto(String tipoRicerca, String query) {
        String filtro = query != null ? query.trim().toLowerCase() : "";
        if (filtro.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Inserisci un testo da cercare.",
                    "Ricerca",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ArrayList<Fotografia> risultati;
        if (PannelloRicercaFoto.TIPO_LUOGO.equalsIgnoreCase(tipoRicerca)) {
            risultati = controller.ricercaFotoPersonalePerLuogo(filtro);
        } else {
            risultati = controller.ricercaFotoPersonalePerSoggetto(filtro);
        }

        if (risultati.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nessun risultato trovato per: " + query,
                    "Ricerca",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Risultati trovati: ").append(risultati.size()).append("\n\n");

        int max = Math.min(20, risultati.size());
        for (int i = 0; i < max; i++) {
            Fotografia foto = risultati.get(i);
            sb.append("- ID ").append(foto.getIdFoto())
                    .append(" | Dispositivo: ")
                    .append(foto.getDispositivo() != null ? foto.getDispositivo() : "N/D")
                    .append("\n");
        }
        if (risultati.size() > max) {
            sb.append("\n...");
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(420, 260));

        JOptionPane.showMessageDialog(this, scroll, "Risultati Ricerca", JOptionPane.INFORMATION_MESSAGE);
    }
}
