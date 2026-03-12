package GUI;

import Controller.Controller;
import Model.Utente;
import Model.GalleriaCondivisa;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;

/**
 * Dialogo modale per l'inserimento di una nuova fotografia nel sistema.
 * Fornisce un form con campi per il dispositivo, le coordinate geografiche,
 * la visibilità e la gestione dei soggetti associati alla foto.
 */
public class AggiungiFotoDialog extends JDialog {

    private final Controller controller;

    private JTextField txtDispositivo;
    private JCheckBox chkPrivata;
    private JList<GalleriaCondivisa> listGallerieCondivise;
    
    private JComboBox<String> cbSegnoLat;
    private JFormattedTextField txtLatitudine;
    private JComboBox<String> cbSegnoLon;
    private JFormattedTextField txtLongitudine;
    private JTextField txtToponimo;
    
    private JComboBox<String> cbCategoriaSoggetto;
    private JComboBox<String> cbUtenti;
    private JTextField txtNomeSoggetto;

    /**
     * Costruisce il dialog box di inserimento foto.
     * @param parentFrame Il frame padre che ha richiesto l'apertura.
     * @param controller Il riferimento al controller per le operazioni di business logic e persistenza.
     */
    public AggiungiFotoDialog(Frame parentFrame, Controller controller) {
        super(parentFrame, "Aggiungi Nuova Foto", true);
        this.controller = controller;

        setLayout(new BorderLayout(10, 10));
        setSize(500, 600);
        setLocationRelativeTo(parentFrame);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. Dispositivo e 2. Luogo
        buildSezioneDatiBaseELuogo(formPanel);

        // 3. Visibilità e Gallerie Condivise
        buildSezioneVisibilitaEGallerie(formPanel);

        buildSezioneCategoriaSoggetti(formPanel);

        add(formPanel, BorderLayout.CENTER);

        // Pulsanti in basso
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalva = new JButton("Salva");
        JButton btnAnnulla = new JButton("Annulla");

        btnAnnulla.addActionListener(_ -> dispose());
        btnSalva.addActionListener(_ -> salvaFoto());

        buttonPanel.add(btnSalva);
        buttonPanel.add(btnAnnulla);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void buildSezioneDatiBaseELuogo(JPanel formPanel) {
        // 1. Dispositivo
        formPanel.add(new JLabel("Dispositivo:"));
        txtDispositivo = new JTextField();
        formPanel.add(txtDispositivo);

        // 2. Luogo: Coordinate e Toponimo
        formPanel.add(new JLabel("Latitudine (es. +41.90): *"));
        cbSegnoLat = createSignComboBox();
        txtLatitudine = new JFormattedTextField();
        formPanel.add(buildCoordinatePanel(cbSegnoLat, txtLatitudine, "##.##"));

        formPanel.add(new JLabel("Longitudine (es. +012.49): *"));
        cbSegnoLon = createSignComboBox();
        txtLongitudine = new JFormattedTextField();
        formPanel.add(buildCoordinatePanel(cbSegnoLon, txtLongitudine, "###.##"));

        formPanel.add(new JLabel("Nome Luogo (Toponimo):"));
        txtToponimo = new JTextField();
        formPanel.add(txtToponimo);
    }

    private void buildSezioneVisibilitaEGallerie(JPanel formPanel) {
        // 3. Visibilità
        formPanel.add(new JLabel("Privata:"));
        chkPrivata = new JCheckBox();
        chkPrivata.setSelected(true);
        formPanel.add(chkPrivata);

        // Pannello gallerie condivise
        formPanel.add(new JLabel("Condividi in Gallerie (Ctrl+Click):"));
        listGallerieCondivise = new JList<>();
        ArrayList<GalleriaCondivisa> gallerieCondivise = controller.getGallerieCondiviseUtenteLoggato();
        DefaultListModel<GalleriaCondivisa> modelGallerie = new DefaultListModel<>();
        for(GalleriaCondivisa g : gallerieCondivise) modelGallerie.addElement(g);
        listGallerieCondivise.setModel(modelGallerie);
        listGallerieCondivise.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listGallerieCondivise.setEnabled(false); // Inizialmente è privata, quindi lista disabilitata

        listGallerieCondivise.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof GalleriaCondivisa) {
                    setText(((GalleriaCondivisa) value).getNomeGalleria());
                }
                return this;
            }
        });
        
        JScrollPane scrollGallerie = new JScrollPane(listGallerieCondivise);
        scrollGallerie.setPreferredSize(new Dimension(150, 60));
        formPanel.add(scrollGallerie);

        chkPrivata.addActionListener(_ -> {
            boolean isPrivata = chkPrivata.isSelected();
            listGallerieCondivise.setEnabled(!isPrivata);
            if (isPrivata) {
                listGallerieCondivise.clearSelection();
            } else if (!modelGallerie.isEmpty()) {
                // Seleziona la prima galleria di default se si toglie la spunta "Privata"
                listGallerieCondivise.setSelectedIndex(0); 
            }
        });
    }

    private void buildSezioneCategoriaSoggetti(JPanel formPanel) {
        // 4. Soggetto
        formPanel.add(new JLabel("Categoria Soggetto:"));
        cbCategoriaSoggetto = new JComboBox<>(new String[]{"Nessuno", "Persona", "Animale", "Oggetto", "Paesaggio", "Utente"});
        formPanel.add(cbCategoriaSoggetto);

        formPanel.add(new JLabel("Nome Soggetto:"));
        txtNomeSoggetto = new JTextField();
        formPanel.add(txtNomeSoggetto);

        formPanel.add(new JLabel("Soggetto Utente (se applicabile):"));
        cbUtenti = new JComboBox<>();
        cbUtenti.setEnabled(false);
        popolaComboUtenti();
        formPanel.add(cbUtenti);

        // Gestione tendina soggetti
        cbCategoriaSoggetto.addActionListener(_ -> {
            String selezionato = (String) cbCategoriaSoggetto.getSelectedItem();
            if ("Utente".equals(selezionato)) {
                cbUtenti.setEnabled(true);
                txtNomeSoggetto.setEnabled(false);
                txtNomeSoggetto.setText("");
            } else if ("Nessuno".equals(selezionato)) {
                cbUtenti.setEnabled(false);
                txtNomeSoggetto.setEnabled(false);
            } else {
                cbUtenti.setEnabled(false);
                txtNomeSoggetto.setEnabled(true);
            }
        });

        // Simula la selezione iniziale
        cbCategoriaSoggetto.setSelectedItem("Nessuno");
    }

    /**
     * Crea un JComboBox standardizzato per i segni delle coordinate.
     */
    private JComboBox<String> createSignComboBox() {
        JComboBox<String> combo = new JComboBox<>(new String[]{"+", "-"});
        // Rimuove lo sfondo per farlo sembrare più unito alla casella di testo
        combo.setBackground(Color.WHITE);
        return combo;
    }

    /**
     * Metodo di utility per costruire il pannello delle coordinate e applicare la maschera JFormattedTextField.
     */
    private JPanel buildCoordinatePanel(JComboBox<String> cbSegno, JFormattedTextField txtField, String mask) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(cbSegno, BorderLayout.WEST);
        try {
            MaskFormatter formatter = new MaskFormatter(mask);
            formatter.setPlaceholderCharacter('0');
            txtField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(formatter));
        } catch (ParseException e) {
            System.err.println("Errore nella formattazione della maschera: " + mask);
        }
        panel.add(txtField, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Popola la combobox degli utenti attingendo dalla lista in memoria del controller.
     */
    private void popolaComboUtenti() {
        ArrayList<Utente> utenti = controller.getUtentiInMemory();
        if (utenti != null) {
            for (Utente u : utenti) {
                cbUtenti.addItem(u.getUsername());
            }
        }
    }

    /**
     * Mostra un popup di messaggio generico.
     */
    private void mostraMessaggio(String messaggio, String titolo, int tipoMessaggio) {
        JOptionPane.showMessageDialog(this, messaggio, titolo, tipoMessaggio);
    }

    /**
     * Mostra un popup di errore generico di validazione (warning).
     */
    private void mostraErrore(String messaggio) {
        mostraMessaggio(messaggio, "Errore di Validazione", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Raccoglie i dati inseriti nei campi di testo, valida le informazioni
     * e invoca il metodo {@link Controller#creazioneNuovaFoto} per salvare la fotografia.
     * Mostra un messaggio di conferma o di errore all'utente.
     */
    private void salvaFoto() {
        String dispositivo = txtDispositivo.getText().trim();
        // Spunta Privata = false visibilita (non visibile). Spunta !Privata = true visibilita (pubblica)
        boolean visibilita = !chkPrivata.isSelected();
        
        List<Integer> idGallerieCondivise = new ArrayList<>();
        if (visibilita) {
            for (GalleriaCondivisa gc : listGallerieCondivise.getSelectedValuesList()) {
                idGallerieCondivise.add(gc.getIdGalleria());
            }
            if (idGallerieCondivise.isEmpty()) {
                mostraErrore("Hai deciso di renderla visibile ma non hai selezionato nessuna galleria da condividere.");
                return;
            }
        }
        
        if (dispositivo.isEmpty()) {
            mostraErrore("Il campo 'Dispositivo' è obbligatorio.");
            return;
        }

        String coordinate = checkAndBuildCoordinates();
        if (coordinate == null) return; // Errore già gestito

        String toponimo = valutaToponimo(coordinate);
        if (toponimo == null) return; // Creazione annullata dall'utente in fase di warning su toponimo

        String[] soggetti = recuperaDatiSoggetti();
        if (soggetti == null && !"Nessuno".equals(cbCategoriaSoggetto.getSelectedItem())) return; // Errore sui soggetti

        String categoria = (String) cbCategoriaSoggetto.getSelectedItem();

        try {
            boolean success = controller.creazioneNuovaFoto(dispositivo, visibilita, coordinate, toponimo, soggetti, categoria, idGallerieCondivise);
            if (success) {
                mostraMessaggio("Foto aggiunta con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Chiude la dialog e innesca il refresh
            } else {
                mostraMessaggio("Errore durante il salvataggio nel database.", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            mostraMessaggio("Eccezione Imprevista: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Valida ed estrae la stringa formattata delle coordinate unendo segni e valori.
     * @return La stringa "Lat, Lon" oppure null se c'è un errore di validazione.
     */
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

    /**
     * Controlla l'esistenza del luogo nel sistema. 
     * Ritorna il toponimo da salvare o blocca l'operazione in caso di conflitto.
     */
    private String valutaToponimo(String coordinate) {
        String toponimo = txtToponimo.getText().trim();
        if (toponimo.isEmpty()) {
            toponimo = "Sconosciuto";
        }

        // Interroghiamo il Controller sullo stato di questo luogo geografico
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

    /**
     * Raccoglie i dati del soggetto in base alla categoria selezionata.
     */
    private String[] recuperaDatiSoggetti() {
        String categoria = (String) cbCategoriaSoggetto.getSelectedItem();

        if ("Utente".equals(categoria)) {
            return new String[] { (String) cbUtenti.getSelectedItem() };
        } else if (!"Nessuno".equals(categoria)) {
            String nomeSoggetto = txtNomeSoggetto.getText().trim();
            if (nomeSoggetto.isEmpty()) {
                mostraErrore("Hai selezionato una categoria soggetto ma non hai inserito il nome.");
                return null;
            }
            return new String[] { nomeSoggetto };
        }
        return null;
    }
}