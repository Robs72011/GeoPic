package GUI;

import Controller.Controller;
import Model.Utente;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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

        // 2. Data Scatto
        formPanel.add(new JLabel("Data scatto (YYYY-MM-DD):"));
        txtDataScatto = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        txtDataScatto.setColumns(10);
        formPanel.add(txtDataScatto);

        // 3. Luogo: Coordinate e Toponimo
        formPanel.add(new JLabel("Latitudine (es. +41.90):"));
        txtLatitudine = new JTextField();
        formPanel.add(txtLatitudine);

        formPanel.add(new JLabel("Longitudine (es. +012.49):"));
        txtLongitudine = new JTextField();
        formPanel.add(txtLongitudine);

        formPanel.add(new JLabel("Nome Luogo (Toponimo):"));
        txtToponimo = new JTextField();
        formPanel.add(txtToponimo);

        // 4. Visibilità
        formPanel.add(new JLabel("Pubblica (Visibilità):"));
        chkVisibilita = new JCheckBox();
        chkVisibilita.setSelected(true);
        formPanel.add(chkVisibilita);

        // 5. Soggetto
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
        JButton btnSalva = new JButton("Salva nel DB");
        JButton btnAnnulla = new JButton("Annulla");

        btnAnnulla.addActionListener(e -> dispose());
        btnSalva.addActionListener(e -> salvaFoto());

        buttonPanel.add(btnSalva);
        buttonPanel.add(btnAnnulla);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void popolaComboUtenti() {
        ArrayList<Utente> utenti = controller.getUtentiInMemory();
        if (utenti != null) {
            for (Utente u : utenti) {
                cbUtenti.addItem(u.getUsername());
            }
        }
    }

    private void salvaFoto() {
        // TODO: Aggiungere logica di salvataggio passandola al controller 
        // e richiamando i DAO necessari
        JOptionPane.showMessageDialog(this, "Acquisizione completata! Manca l'implementazione del salvataggio effettivo nel Controller/DAO.", "Info", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}