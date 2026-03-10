package GUI;

import Controller.Controller;
import Model.Utente;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Dialogo modale per l'inserimento di una nuova fotografia nel sistema.
 * Fornisce un form con campi per il dispositivo, le coordinate geografiche,
 * la visibilità e la gestione dei soggetti associati alla foto.
 */
public class AggiungiFotoDialog extends JDialog {

    private Controller controller;

    private JTextField txtDispositivo;
    private JFormattedTextField txtDataScatto;
    private JCheckBox chkVisibilita;
    
    private JTextField txtLatitudine;
    private JTextField txtLongitudine;
    private JTextField txtToponimo;
    
    private JComboBox<String> cbCategoriaSoggetto;
    private JComboBox<String> cbUtenti;
    private JTextField txtNomeSoggetto;

    /**
     * Costruisce il dialogo di inserimento foto.
     * @param owner Il frame padre che ha richiesto l'apertura.
     * @param controller Il riferimento al controller per le operazioni di business logic e persistenza.
     */
    public AggiungiFotoDialog(Frame owner, Controller controller) {
        super(owner, "Aggiungi Nuova Foto", true);
        this.controller = controller;

        setLayout(new BorderLayout(10, 10));
        setSize(400, 500);
        setLocationRelativeTo(owner);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. Dispositivo
        formPanel.add(new JLabel("Dispositivo:"));
        txtDispositivo = new JTextField();
        formPanel.add(txtDispositivo);

        // 2. Luogo: Coordinate e Toponimo
        formPanel.add(new JLabel("Latitudine (es. +41.90):"));
        txtLatitudine = new JTextField();
        formPanel.add(txtLatitudine);

        formPanel.add(new JLabel("Longitudine (es. +012.49):"));
        txtLongitudine = new JTextField();
        formPanel.add(txtLongitudine);

        formPanel.add(new JLabel("Nome Luogo (Toponimo):"));
        txtToponimo = new JTextField();
        formPanel.add(txtToponimo);

        // 3. Visibilità
        formPanel.add(new JLabel("Pubblica (Visibilità):"));
        chkVisibilita = new JCheckBox();
        chkVisibilita.setSelected(true);
        formPanel.add(chkVisibilita);

        // 4. Soggetto
        formPanel.add(new JLabel("Categoria Soggetto:"));
        String[] categorie = {"Nessuno", "Persona", "Animale", "Oggetto", "Paesaggio", "Utente"};
        cbCategoriaSoggetto = new JComboBox<>(categorie);
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
        cbCategoriaSoggetto.addActionListener(e -> {
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

        add(formPanel, BorderLayout.CENTER);

        // Pulsanti in basso
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalva = new JButton("Salva");
        JButton btnAnnulla = new JButton("Annulla");

        btnAnnulla.addActionListener(e -> dispose());
        btnSalva.addActionListener(e -> salvaFoto());

        buttonPanel.add(btnSalva);
        buttonPanel.add(btnAnnulla);

        add(buttonPanel, BorderLayout.SOUTH);
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
     * Raccoglie i dati inseriti nei campi di testo, valida le informazioni
     * e invoca il metodo {@link Controller#creazioneNuovaFoto} per salvare la fotografia.
     * Mostra un messaggio di conferma o di errore all'utente.
     */
    private void salvaFoto() {
        String dispositivo = txtDispositivo.getText();
        boolean visibilita = chkVisibilita.isSelected();
        
        // Formatta coordinate come richiesto (+41.90,+012.49)
        String lat = txtLatitudine.getText().trim();
        String lon = txtLongitudine.getText().trim();
        String coordinate = (lat.isEmpty() || lon.isEmpty()) ? null : lat + "," + lon;

        String toponimo = txtToponimo.getText().trim();
        if (toponimo.isEmpty()) {
            toponimo = null;
        }

        // Gestione soggetti (l'array richiesto dal controller)
        String[] soggetti = null;
        String categoria = (String) cbCategoriaSoggetto.getSelectedItem();

        if ("Utente".equals(categoria)) {
            soggetti = new String[] { (String) cbUtenti.getSelectedItem() };
        } else if (!"Nessuno".equals(categoria) && !txtNomeSoggetto.getText().trim().isEmpty()) {
            soggetti = new String[] { txtNomeSoggetto.getText().trim() };
        }

        try {
            controller.creazioneNuovaFoto(dispositivo, visibilita, coordinate, toponimo, soggetti);
            JOptionPane.showMessageDialog(this, "Foto aggiunta con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Errore nel salvataggio: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
}