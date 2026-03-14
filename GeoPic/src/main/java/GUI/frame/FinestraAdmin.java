package GUI.frame;

import GUI.dialog.DialogAggiungiUtente;
import GUI.Main;

import Controller.Controller;

import Model.Utente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class FinestraAdmin extends JFrame {

    private final Controller controller;
    private final DefaultTableModel tableModel;

    public FinestraAdmin(Controller controller) {
        this.controller = controller;
        this.tableModel = buildTableModel();

        setTitle("Pannello Amministratore - GeoPic");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitolo = new JLabel("Gestione Utenti Sistema");
        lblTitolo.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(lblTitolo, BorderLayout.NORTH);
        mainPanel.add(buildTabella(), BorderLayout.CENTER);
        mainPanel.add(buildBottoni(), BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private DefaultTableModel buildTableModel() {
        return new DefaultTableModel(new String[]{"Seleziona", "ID", "Username", "Admin", "Soggetto"}, 0) {
            @Override public Class<?> getColumnClass(int col) { return col == 0 ? Boolean.class : String.class; }
            @Override public boolean isCellEditable(int row, int col) { return col == 0; }
        };
    }

    private JScrollPane buildTabella() {
        JTable tabella = new JTable(tableModel);
        tabella.getTableHeader().setReorderingAllowed(false);
        tabella.getColumnModel().getColumn(0).setMaxWidth(90);
        aggiornaTabella();
        return new JScrollPane(tabella);
    }

    private JPanel buildBottoni() {
        JButton btnAggiungi = new JButton("Aggiungi Utente");
        JButton btnRimuovi = new JButton("Rimuovi Selezionati");
        JButton btnLogout = new JButton("Logout");

        btnAggiungi.addActionListener(_ -> {
            new DialogAggiungiUtente(this, controller).setVisible(true);
            aggiornaTabella();
        });

        btnRimuovi.addActionListener(_ -> confermaEliminazione());

        btnLogout.addActionListener(_ -> {
            dispose();
            Main.main(new String[]{});
        });

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(btnAggiungi);
        panel.add(btnRimuovi);
        panel.add(btnLogout);
        return panel;
    }

    private void confermaEliminazione() {
        ArrayList<Integer> idSelezionati = new ArrayList<>();
        DefaultTableModel anteprima = new DefaultTableModel(new String[]{"ID", "Username", "Admin"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (Boolean.TRUE.equals(tableModel.getValueAt(i, 0)) && tableModel.getValueAt(i, 1) instanceof Integer id) {
                idSelezionati.add(id);
                anteprima.addRow(new Object[]{ tableModel.getValueAt(i, 1), tableModel.getValueAt(i, 2), tableModel.getValueAt(i, 3) });
            }
        }

        if (idSelezionati.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleziona almeno un utente dalla tabella prima di procedere.", "Nessuna selezione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTable tabellaAnteprima = new JTable(anteprima);
        tabellaAnteprima.setEnabled(false);
        JScrollPane scroll = new JScrollPane(tabellaAnteprima);
        scroll.setPreferredSize(new Dimension(350, Math.min(150, idSelezionati.size() * 26 + 30)));

        JPanel pannello = new JPanel(new BorderLayout(0, 8));
        pannello.add(new JLabel("Stai per eliminare i seguenti utenti (operazione irreversibile):"), BorderLayout.NORTH);
        pannello.add(scroll, BorderLayout.CENTER);

        if (JOptionPane.showConfirmDialog(this, pannello, "Conferma Rimozione", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            controller.eliminazioneUtentiByAdmin(idSelezionati);
            aggiornaTabella();
        }
    }

    private void aggiornaTabella() {
        tableModel.setRowCount(0);
        ArrayList<Utente> utenti = controller.getUtentiInMemory();
        if (utenti != null) {
            for (Utente u : utenti) {
                tableModel.addRow(new Object[]{ Boolean.FALSE, u.getIdUtente(), u.getUsername(), u.isAdmin() ? "Sì" : "No", u.isSoggetto() ? "Sì" : "No" });
            }
        }
    }
}
