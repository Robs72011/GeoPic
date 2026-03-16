package GUI.dialog;

import Controller.Controller;
import GUI.panel.PannelloAggiungiSoggetti;
import Model.Fotografia;
import Model.GalleriaCondivisa;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
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
    private int gridyForm = 0;

    public DialogAggiungiFoto(Frame parentFrame, Controller controller) {
        super(parentFrame, "Aggiungi Nuova Foto", controller);
        configuraDimensioni(550, 700, 500, 650, true);

        pannelloSoggetti = new PannelloAggiungiSoggetti(controller.getUsernamesInMemory());

        JPanel formPanel = new JPanel(new GridBagLayout());
        buildFormDatiBase(formPanel);
        buildFormVisibilita(formPanel);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);

        JPanel mainPanel = creaMainPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(pannelloSoggetti, BorderLayout.CENTER);

        montaContenutoConBottoniSalva(mainPanel, this::salvaFoto);
    }

    private void addFormField(JPanel form, String labelText, Component component) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = gridyForm;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        form.add(new JLabel(labelText), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = gridyForm;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        form.add(component, gbc);

        gridyForm++;
    }

    private void buildFormDatiBase(JPanel form) {
        txtDispositivo = new JTextField();
        addFormField(form, "Dispositivo:", txtDispositivo);

        cbSegnoLat = createSignComboBox();
        txtLatitudine = new JFormattedTextField();
        addFormField(form, "Latitudine (es. +41.90): *", buildCoordinatePanel(cbSegnoLat, txtLatitudine, "##.##"));

        cbSegnoLon = createSignComboBox();
        txtLongitudine = new JFormattedTextField();
        addFormField(form, "Longitudine (es. +012.49): *", buildCoordinatePanel(cbSegnoLon, txtLongitudine, "###.##"));

        txtToponimo = new JTextField();
        addFormField(form, "Nome Luogo (Toponimo):", txtToponimo);
    }

    private void buildFormVisibilita(JPanel form) {
        chkPrivata = new JCheckBox();
        chkPrivata.setSelected(true);
        addFormField(form, "Privata:", chkPrivata);

        listGallerieCondivise = new JList<>();
        DefaultListModel<GalleriaCondivisa> modelGallerie = new DefaultListModel<>();
        controller.getGallerieCondiviseUtenteLoggato().forEach(modelGallerie::addElement);
        listGallerieCondivise.setModel(modelGallerie);
        listGallerieCondivise.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listGallerieCondivise.setEnabled(false);

        JScrollPane scrollGallerie = new JScrollPane(listGallerieCondivise);
        scrollGallerie.setPreferredSize(new Dimension(150, 60));
        
        JPanel panelGallerie = new JPanel(new BorderLayout(0, 5));
        panelGallerie.add(scrollGallerie, BorderLayout.CENTER);
        JLabel help = new JLabel("Suggerimento: CTRL per selezione multipla.");
        help.setFont(new Font("Arial", Font.PLAIN, 11));
        help.setForeground(Color.DARK_GRAY);
        panelGallerie.add(help, BorderLayout.SOUTH);
        addFormField(form, "Condividi in Gallerie:", panelGallerie);

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
        ArrayList<GalleriaCondivisa> gallerieCondiviseSelezionate = getGallerieCondiviseSelezionate(visibilita);

        if (isBlank(dispositivo)) {
            mostraErrore("Il campo 'Dispositivo' è obbligatorio.");
            return;
        }

        String coordinate = checkAndBuildCoordinates();
        if (coordinate == null) return; // Errore già gestito

        String toponimo = txtToponimo.getText().trim();
        if (toponimo.isEmpty()) {
            toponimo = "Sconosciuto";
        }

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
            mostraSuccessoEChiudi("Foto aggiunta con successo!");
        } else {
            mostraErrore("Errore durante il salvataggio nel database.");
        }
    }

    private ArrayList<GalleriaCondivisa> getGallerieCondiviseSelezionate(boolean visibilita) {
        if (!visibilita) {
            return new ArrayList<>();
        }
        return new ArrayList<>(listGallerieCondivise.getSelectedValuesList());
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


}