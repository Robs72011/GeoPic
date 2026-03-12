package GUI.dialog;

import Controller.Controller;
import GUI.panel.PannelloAggiungiSoggetti;
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

    /**
     * Costruisce il dialog box di inserimento foto.
     * @param parentFrame Il frame padre che ha richiesto l'apertura.
     * @param controller Il riferimento al controller per le operazioni di business logic e persistenza.
     */
    public DialogAggiungiFoto(Frame parentFrame, Controller controller) {
        super(parentFrame, "Aggiungi Nuova Foto", controller);
        ArrayList<String> nomiUtentiDisponibili = controller.getUsernamesInMemory();

        setSize(550, 700);
        setMinimumSize(new Dimension(500, 650));

        JPanel mainPanel = creaMainPanel();

        // Form base a griglia
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        buildSezioneDatiBaseELuogo(formPanel);
        buildSezioneVisibilitaEGallerie(formPanel);

        // Sezione Soggetti Dinamica (Ora estratta nella classe separata GestioneSoggettiPanel)
        pannelloSoggetti = new PannelloAggiungiSoggetti(nomiUtentiDisponibili);

        // Pannello superiore con Form Base + Spazio Vuoto
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(pannelloSoggetti, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
        add(creaPannelloBottoni(this::salvaFoto), BorderLayout.SOUTH);
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
                listGallerieCondivise.setSelectedIndex(0); 
            }
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
        if (toponimo == null) return;

        // Recuperiamo tutti i soggetti interrogando il nuovo pannello esterno
        List<String> nomiSoggettiFinali = new ArrayList<>();
        List<String> categorieSoggettiFinali = new ArrayList<>();

        for (PannelloAggiungiSoggetti.SoggettoRowPanel riga : pannelloSoggetti.getRigheSoggetti()) {
            String categoria = riga.getCategoria();
            if ("Nessuno".equals(categoria)) continue;

            if ("Utente".equals(categoria)) {
                List<String> selezionati = riga.getUtentiSelezionati();
                if (selezionati.isEmpty()) {
                    mostraErrore("Hai scelto la categoria 'Utente' ma non hai selezionato nessuno dalla lista.");
                    return;
                }
                for (String utente : selezionati) {
                    nomiSoggettiFinali.add(utente);
                    categorieSoggettiFinali.add("Utente");
                }
            } else {
                String nomeSoggetto = riga.getNome();
                if (nomeSoggetto.isEmpty()) {
                    mostraErrore("Hai selezionato una categoria ('" + categoria + "') ma non hai inserito il nome.");
                    return;
                }
                nomiSoggettiFinali.add(nomeSoggetto);
                categorieSoggettiFinali.add(categoria);
            }
        }

        try {
            boolean success = controller.creazioneNuovaFoto(
                dispositivo, 
                visibilita, 
                coordinate, 
                toponimo, 
                nomiSoggettiFinali, 
                categorieSoggettiFinali, 
                idGallerieCondivise
            );
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