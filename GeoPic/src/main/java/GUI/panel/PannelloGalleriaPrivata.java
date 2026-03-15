package GUI.panel;

import Controller.Controller;
import GUI.dialog.DialogAggiungiFoto;
import GUI.dialog.DialogAggiungiVideo;
import GUI.dialog.DialogEliminaFoto;
import Model.Fotografia;
import Model.Luogo;
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

        JMenuItem itemEliminaFoto = new JMenuItem("Elimina Foto");
        itemEliminaFoto.addActionListener(_ -> apriDialogEliminaFoto());
        popupAzioni.add(itemEliminaFoto);

        JMenuItem itemCreaVideo = new JMenuItem("Crea Video");
        itemCreaVideo.addActionListener(_ -> apriDialogCreaVideo());
        popupAzioni.add(itemCreaVideo);

        popupAzioni.addSeparator();

        JMenuItem itemTop3 = new JMenuItem("Top 3 Luoghi");
        itemTop3.addActionListener(_ -> mostraTop3Luoghi());
        popupAzioni.add(itemTop3);

        menuAzioni.addActionListener(_ -> popupAzioni.show(menuAzioni, 0, menuAzioni.getHeight()));
        menuAzioni.setAlignmentX(Component.RIGHT_ALIGNMENT);
        PannelloRicercaFoto pannelloRicerca = new PannelloRicercaFoto(richiesta ->
                eseguiRicercaFoto(richiesta.getTipo(), richiesta.getQuery()));
        pannelloRicerca.setMaximumSize(new Dimension(380, 36));
        pannelloRicerca.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightPanel.add(pannelloRicerca);
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(menuAzioni);

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

    private void apriDialogEliminaFoto() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        Frame owner = parentWindow instanceof Frame ? (Frame) parentWindow : null;

        DialogEliminaFoto dialog = new DialogEliminaFoto(owner, controller);
        dialog.setVisible(true);

        if (onRefresh != null) {
            onRefresh.run();
        }
    }

    private void mostraTop3Luoghi() {
        ArrayList<Luogo> topLuoghi = controller.getTop3Luoghi();

        if (topLuoghi.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nessun luogo disponibile per la classifica.",
                    "Top 3 Luoghi",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("I 3 luoghi più fotografati:\n\n");

        for (int i = 0; i < topLuoghi.size(); i++) {
            Luogo luogo = topLuoghi.get(i);
            sb.append(i + 1).append(". ");
                    if(!luogo.getNomeMnemonico().isEmpty()) {
                                sb.append(luogo.getNomeMnemonico() != null ? luogo.getNomeMnemonico() : "Sconosciuto")
                                .append(" (Coordinate: ").append(luogo.getCoordinate()).append(")\n");
                    }
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(350, 150));

        JOptionPane.showMessageDialog(this, scroll, "Top 3 Luoghi", JOptionPane.INFORMATION_MESSAGE);
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
