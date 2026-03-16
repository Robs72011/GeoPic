package GUI.dialog;

import Controller.Controller;
import Model.Fotografia;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Dialog modale per la creazione di un nuovo video a partire da foto della galleria personale.
 */
public class DialogAggiungiVideo extends DialogAggiungi {

    private final JTextField campoTitolo;
    private final JTextArea areaDescrizione;
    private final JList<String> listaFoto;
    private final ArrayList<Integer> idFotoDisponibili;

    public DialogAggiungiVideo(Frame parentFrame, Controller controller) {
        super(parentFrame, "Crea Nuovo Video", controller);
        configuraDimensioni(620, 520, 560, 460, true);

        campoTitolo = new JTextField();
        areaDescrizione = new JTextArea(4, 28);
        areaDescrizione.setLineWrap(true);
        areaDescrizione.setWrapStyleWord(true);

        idFotoDisponibili = new ArrayList<>();
        listaFoto = new JList<>(new DefaultListModel<>());
        listaFoto.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JPanel mainPanel = creaMainPanel();
        mainPanel.add(creaForm(), BorderLayout.NORTH);
        mainPanel.add(creaSezioneFoto(), BorderLayout.CENTER);

        montaContenutoConBottoniSalva(mainPanel, this::salvaVideo);
    }

    private JPanel creaForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Titolo:*"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(campoTitolo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(new JLabel("Descrizione:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JScrollPane scrollDescrizione = new JScrollPane(areaDescrizione);
        scrollDescrizione.setPreferredSize(new Dimension(320, 90));
        formPanel.add(scrollDescrizione, gbc);

        return formPanel;
    }

    private JPanel creaSezioneFoto() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(new JLabel("Seleziona le foto da includere:*"), BorderLayout.NORTH);

        caricaFotoDisponibili();

        JScrollPane scrollFoto = new JScrollPane(listaFoto);
        panel.add(scrollFoto, BorderLayout.CENTER);

        JLabel help = new JLabel("Suggerimento: tieni premuto CTRL per selezione multipla.");
        help.setFont(new Font("Arial", Font.PLAIN, 12));
        help.setForeground(Color.DARK_GRAY);
        panel.add(help, BorderLayout.SOUTH);

        return panel;
    }

    private void caricaFotoDisponibili() {
        DefaultListModel<String> model = (DefaultListModel<String>) listaFoto.getModel();
        model.clear();
        idFotoDisponibili.clear();

        ArrayList<Fotografia> fotoDisponibili = controller.getFotoGalleriaPersonale();
        if (fotoDisponibili == null) {
            return;
        }

        for (Fotografia foto : fotoDisponibili) {
            if (foto == null || foto.getIdFoto() == null) {
                continue;
            }
            idFotoDisponibili.add(foto.getIdFoto());
            model.addElement(controller.formattaFotoPerLista(foto));
        }
    }

    private void salvaVideo() {
        String titolo = trimToEmpty(campoTitolo.getText());
        String descrizione = trimToEmpty(areaDescrizione.getText());

        if (isBlank(titolo)) {
            mostraErrore("Il titolo del video e' obbligatorio.");
            return;
        }

        int[] selectedIndices = listaFoto.getSelectedIndices();
        if (selectedIndices.length == 0) {
            mostraErrore("Seleziona almeno una foto per creare il video.");
            return;
        }

        Integer[] fotoSelezionate = new Integer[selectedIndices.length];
        for (int i = 0; i < selectedIndices.length; i++) {
            fotoSelezionate[i] = idFotoDisponibili.get(selectedIndices[i]);
        }

        boolean success = controller.creazioneVideo(titolo, descrizione, fotoSelezionate);
        if (success) {
            mostraSuccessoEChiudi("Richiesta di creazione video completata.", "Creazione Video");
        } else {
            mostraErrore("Errore durante la creazione del video.");
        }
    }
}
