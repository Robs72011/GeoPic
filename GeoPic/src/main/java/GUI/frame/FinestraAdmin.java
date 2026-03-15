package GUI.frame;

import GUI.dialog.DialogAggiungiUtente;
import GUI.Main;

import Controller.Controller;

import Model.Utente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Finestra principale dedicata all'amministratore del sistema.
 * Permette la visualizzazione e la gestione degli utenti (aggiunta, rimozione).
 */
public class FinestraAdmin extends JFrame {
    
    private final Controller controller;
    private DefaultTableModel tableModel;
    private JTable tabellaUtenti;

    public FinestraAdmin(Controller controller) {
        this.controller = controller;

        setTitle("Pannello Amministratore - GeoPic");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        buildSezioneHeader(mainPanel);
        buildSezioneTabella(mainPanel);
        buildSezioneBottoni(mainPanel);

        add(mainPanel);
        setVisible(true);
    }

    private void buildSezioneHeader(JPanel mainPanel) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel lblTitolo = new JLabel("Gestione Utenti Sistema");
        lblTitolo.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(lblTitolo, BorderLayout.WEST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private void buildSezioneTabella(JPanel mainPanel) {
        String[] colonne = {"ID", "Username", "Admin", "Soggetto"};
        tableModel = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabella di sola lettura
            }
        };

        tabellaUtenti = new JTable(tableModel);
        tabellaUtenti.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabellaUtenti.getTableHeader().setReorderingAllowed(false);
        
        // Popola la tabella
        aggiornaTabella();

        JScrollPane scrollPane = new JScrollPane(tabellaUtenti);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void buildSezioneBottoni(JPanel mainPanel) {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnAggiungi = new JButton("Aggiungi Utente");
        JButton btnRimuovi = new JButton("Rimuovi Selezionato");
        JButton btnLogout = new JButton("Logout");

        btnAggiungi.addActionListener(_ -> {
            DialogAggiungiUtente dialog = new DialogAggiungiUtente(this, controller);
            dialog.setVisible(true);
            aggiornaTabella();
        });

        btnRimuovi.addActionListener(_ -> {
            int rigaSelezionata = tabellaUtenti.getSelectedRow();
            if (rigaSelezionata == -1) {
                JOptionPane.showMessageDialog(this, "Seleziona un utente dalla tabella prima di procedere.", "Nessuna selezione", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int idUtente = (int) tableModel.getValueAt(rigaSelezionata, 0);
            String username = (String) tableModel.getValueAt(rigaSelezionata, 1);

            if (controller.getLoggedInUtente() != null && controller.getLoggedInUtente().getIdUtente() == idUtente) {
                JOptionPane.showMessageDialog(this, "Non puoi rimuovere il tuo stesso account mentre sei loggato.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int scelta = JOptionPane.showConfirmDialog(this, 
                "Sei sicuro di voler rimuovere l'utente '" + username + "' (ID: " + idUtente + ")? L'operazione è irreversibile.",
                "Conferma Rimozione", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (scelta == JOptionPane.YES_OPTION) {
                controller.eliminazioneUtenteByAdmin(idUtente);
                aggiornaTabella();
                JOptionPane.showMessageDialog(this, "Utente rimosso con successo.", "Successo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnLogout.addActionListener(_ -> {
            dispose();
            Main.main(new String[]{}); // Rilancia il login
        });

        btnPanel.add(btnAggiungi);
        btnPanel.add(btnRimuovi);
        btnPanel.add(btnLogout);

        mainPanel.add(btnPanel, BorderLayout.SOUTH);
    }

    /**
     * Ricarica i dati degli utenti in memoria e aggiorna il modello della tabella.
     */
    private void aggiornaTabella() {
        tableModel.setRowCount(0); // Svuota la tabella
        ArrayList<Utente> utenti = controller.getUtentiInMemory();
        if (utenti != null) {
            for (Utente u : utenti) {
                tableModel.addRow(new Object[]{
                        u.getIdUtente(),
                        u.getUsername(),
                        u.isAdmin() ? "Sì" : "No",
                        u.isSoggetto() ? "Sì" : "No"
                });
            }
        }
    }
}
