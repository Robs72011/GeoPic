package GUI.panel;

import Controller.Controller;
import GUI.dialog.DialogAggiungiFoto;
import GUI.dialog.DialogAggiungiVideo;
import GUI.dialog.DialogEliminaFoto;
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

    private final String TIPO_LUOGO = "Luogo";

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

        JPanel pannelloRicerca = creaPannelloRicerca();
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

    private JPanel creaPannelloRicerca() {
        JPanel pannelloRicerca = new JPanel(new BorderLayout(8, 0));
        pannelloRicerca.setOpaque(false);

        String TIPO_SOGGETTO = "Soggetto";
        JComboBox<String> comboTipoRicerca = new JComboBox<>(new String[]{TIPO_LUOGO, TIPO_SOGGETTO});
        JTextField campoRicerca = new JTextField(18);
        JButton btnCerca = new JButton("Cerca");

        JPanel inputPanel = new JPanel(new BorderLayout(6, 0));
        inputPanel.setOpaque(false);
        inputPanel.add(comboTipoRicerca, BorderLayout.WEST);
        inputPanel.add(campoRicerca, BorderLayout.CENTER);

        Runnable azioneRicerca = () -> eseguiRicercaFoto(
                (String) comboTipoRicerca.getSelectedItem(),
                campoRicerca.getText()
        );

        btnCerca.addActionListener(_ -> azioneRicerca.run());
        campoRicerca.addActionListener(_ -> azioneRicerca.run());

        pannelloRicerca.add(inputPanel, BorderLayout.CENTER);
        pannelloRicerca.add(btnCerca, BorderLayout.EAST);

        return pannelloRicerca;
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
        String testoTop3 = controller.formattaTop3LuoghiTestuale();

        if (testoTop3 == null) {
            JOptionPane.showMessageDialog(this,
                    "Nessun luogo disponibile per la classifica.",
                    "Top 3 Luoghi",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTextArea area = new JTextArea(testoTop3);
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
        if (TIPO_LUOGO.equalsIgnoreCase(tipoRicerca)) {
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

        // Deleghiamo la formattazione al Controller nel rispetto del pattern BCE
        String testoRisultati = controller.formattaRisultatiRicercaTestuale(risultati);
        JTextArea area = new JTextArea(testoRisultati);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(420, 260));

        JOptionPane.showMessageDialog(this, scroll, "Risultati Ricerca", JOptionPane.INFORMATION_MESSAGE);
    }
}
