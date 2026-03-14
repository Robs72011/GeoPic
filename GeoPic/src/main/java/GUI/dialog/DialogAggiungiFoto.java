package GUI.dialog;

import Controller.Controller;
import GUI.panel.PannelloAggiungiSoggetti;
import Model.Fotografia;
import Model.GalleriaCondivisa;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;

public class DialogAggiungiFoto extends DialogAggiungi {

    private JTextField txtDispositivo;
    private JCheckBox chkPrivata;
    private JList<GalleriaCondivisa> listGallerieCondivise;

    private JComboBox<String> cbSegnoLat;
    private JFormattedTextField txtLatitudine;
    private JComboBox<String> cbSegnoLon;
    private JFormattedTextField txtLongitudine;
    private JTextField txtToponimo;

    private final PannelloAggiungiSoggetti pannelloSoggetti;

    public DialogAggiungiFoto(Frame parentFrame, Controller controller) {
        super(parentFrame, "Aggiungi Nuova Foto", controller);
        configuraDimensioni(550, 700, 500, 650, true);

        pannelloSoggetti = new PannelloAggiungiSoggetti(controller.getUsernamesInMemory());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        buildFormDatiBase(formPanel);
        buildFormVisibilita(formPanel);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);

        JPanel mainPanel = creaMainPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(pannelloSoggetti, BorderLayout.CENTER);

        montaContenutoConBottoniSalva(mainPanel, this::salvaFoto);
    }

    private void buildFormDatiBase(JPanel form) {
        form.add(new JLabel("Dispositivo:"));
        txtDispositivo = new JTextField();
        form.add(txtDispositivo);

        form.add(new JLabel("Latitudine (es. +41.90): *"));
        cbSegnoLat = createSignComboBox();
        txtLatitudine = new JFormattedTextField();
        form.add(buildCoordinatePanel(cbSegnoLat, txtLatitudine, "##.##"));

        form.add(new JLabel("Longitudine (es. +012.49): *"));
        cbSegnoLon = createSignComboBox();
        txtLongitudine = new JFormattedTextField();
        form.add(buildCoordinatePanel(cbSegnoLon, txtLongitudine, "###.##"));

        form.add(new JLabel("Nome Luogo (Toponimo):"));
        txtToponimo = new JTextField();
        form.add(txtToponimo);
    }

    private void buildFormVisibilita(JPanel form) {
        form.add(new JLabel("Privata:"));
        chkPrivata = new JCheckBox();
        chkPrivata.setSelected(true);
        form.add(chkPrivata);

        form.add(new JLabel("Condividi in Gallerie (Ctrl+Click):"));
        listGallerieCondivise = new JList<>();
        DefaultListModel<GalleriaCondivisa> modelGallerie = new DefaultListModel<>();
        controller.getGallerieCondiviseUtenteLoggato().forEach(modelGallerie::addElement);
        listGallerieCondivise.setModel(modelGallerie);
        listGallerieCondivise.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listGallerieCondivise.setEnabled(false);

        JScrollPane scrollGallerie = new JScrollPane(listGallerieCondivise);
        scrollGallerie.setPreferredSize(new Dimension(150, 60));
        form.add(scrollGallerie);

        chkPrivata.addActionListener(_ -> {
            listGallerieCondivise.setEnabled(!chkPrivata.isSelected());
            if (chkPrivata.isSelected()) listGallerieCondivise.clearSelection();
        });
    }

    private JComboBox<String> createSignComboBox() {
        JComboBox<String> combo = new JComboBox<>(new String[]{"+", "-"});
        combo.setBackground(Color.WHITE);
        return combo;
    }

    private JPanel buildCoordinatePanel(JComboBox<String> cbSegno, JFormattedTextField txtField, String mask) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(cbSegno, BorderLayout.WEST);
        try {
            MaskFormatter formatter = new MaskFormatter(mask);
            formatter.setPlaceholderCharacter('0');
            txtField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(formatter));
        } catch (ParseException e) {
            System.err.println("Errore formattazione: " + mask);
        }
        panel.add(txtField, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Raccoglie i dati inseriti nei campi di testo, valida le informazioni
     * e invoca il metodo {@link Controller#creazioneNuovaFoto} per salvare la fotografia.
     */
    private void salvaFoto() {
        String dispositivo = txtDispositivo.getText().trim();
        boolean visibilita = !chkPrivata.isSelected();
        List<GalleriaCondivisa> gallerieCondiviseSelezionate = getGallerieCondiviseSelezionate(visibilita);
        
        if (dispositivo.isEmpty()) {
            mostraErrore("Il campo 'Dispositivo' è obbligatorio.");
            return;
        }

        String coordinate = checkAndBuildCoordinates();
        if (coordinate == null) return; // Errore già gestito

        String toponimo = valutaToponimo(coordinate);
        if (toponimo == null) return;

        // Recuperiamo tutti i soggetti interrogando il nuovo pannello esterno
        ArrayList<String> nomiSoggettiFinali = new ArrayList<>();
        ArrayList<String> categorieSoggettiFinali = new ArrayList<>();

        String erroreSoggetti = pannelloSoggetti.popolaSoggettiSelezionati(nomiSoggettiFinali, categorieSoggettiFinali);
        if (erroreSoggetti != null) {
            mostraErrore(erroreSoggetti);
            return;
        }

        Fotografia nuovaFoto = controller.creazioneNuovaFotoConCondivisione(
            dispositivo,
            visibilita,
            coordinate,
            toponimo,
            nomiSoggettiFinali,
            categorieSoggettiFinali,
            gallerieCondiviseSelezionate
        );

        if (nuovaFoto != null) {
            mostraMessaggio("Foto aggiunta con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Chiude la dialog e innesca il refresh
        } else {
            mostraErrore("Errore durante il salvataggio nel database.");
        }
    }

    private List<GalleriaCondivisa> getGallerieCondiviseSelezionate(boolean visibilita) {
        if (!visibilita) {
            return new ArrayList<>();
        }
        return listGallerieCondivise.getSelectedValuesList();
    }

    private String checkAndBuildCoordinates() {
        String latVal = txtLatitudine.getText().replace(" ", "");
        String lonVal = txtLongitudine.getText().replace(" ", "");
        
        if (latVal.equals(".") || lonVal.equals(".") || latVal.isEmpty() || lonVal.isEmpty()) {
            mostraErrore("Le coordinate (Latitudine e Longitudine) sono obbligatorie e devono essere complete.");
            return null;
        }

        String latSign = (String) cbSegnoLat.getSelectedItem();
        String lonSign = (String) cbSegnoLon.getSelectedItem();

        // Deleghiamo al Controller la logica di calcolo per eventuale normalizzazione dello zero
        return controller.normalizzaCoordinate(latSign, latVal, lonSign, lonVal);
    }

    private String valutaToponimo(String coordinate) {
        String toponimo = txtToponimo.getText().trim();
        if (toponimo.isEmpty()) {
            toponimo = "Sconosciuto";
        }

        String topoEsistente = controller.getToponimoEsistente(coordinate);
        
        if (topoEsistente != null && !toponimo.equals(topoEsistente)) {
            int scelta = JOptionPane.showConfirmDialog(this,
                    "Attenzione: nel sistema esiste già un luogo con le coordinate " + coordinate + ".\n" +
                    "Il toponimo storico esistente è '" + topoEsistente + "'.\n" +
                    "Il toponimo nuovo ('" + toponimo + "') verrà ignorato.\n" +
                    "Vuoi procedere associando la foto al luogo storico esistente?",
                    "Luogo Già Esistente", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (scelta != JOptionPane.YES_OPTION) {
                return null; 
            }
        }
        return toponimo;
    }
}