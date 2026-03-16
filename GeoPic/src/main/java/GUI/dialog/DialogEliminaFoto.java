package GUI.dialog;

import Controller.Controller;
import Model.Fotografia;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DialogEliminaFoto extends JDialog {
    private final Controller controller;
    private final JList<FotografiaListItem> listaFoto;

    public DialogEliminaFoto(Frame owner, Controller controller) {
        super(owner, "Elimina Foto", true);
        this.controller = controller;

        setSize(450, 350);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JLabel lblInfo = new JLabel("Seleziona una o più foto da eliminare:");
        lblInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(lblInfo, BorderLayout.NORTH);

        DefaultListModel<FotografiaListItem> listModel = new DefaultListModel<>();
        for (Fotografia foto : controller.getFotoGalleriaPersonale()) {
            listModel.addElement(new FotografiaListItem(foto, controller.formattaFotoPerEliminazione(foto)));
        }

        listaFoto = new JList<>(listModel);
        listaFoto.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(listaFoto);
        
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        JLabel help = new JLabel("Suggerimento: tieni premuto CTRL per selezione multipla.");
        help.setFont(new Font("Arial", Font.PLAIN, 12));
        help.setForeground(Color.DARK_GRAY);
        centerPanel.add(help, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        JPanel pnlBottoni = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAnnulla = new JButton("Annulla");
        JButton btnElimina = new JButton("Elimina Selezionate");

        btnAnnulla.addActionListener(_ -> dispose());
        btnElimina.addActionListener(_ -> eliminaFotoSelezionate());

        pnlBottoni.add(btnAnnulla);
        pnlBottoni.add(btnElimina);
        add(pnlBottoni, BorderLayout.SOUTH);
    }

    private void eliminaFotoSelezionate() {
        ArrayList<FotografiaListItem> selezionate = new ArrayList<>(listaFoto.getSelectedValuesList());
        if (selezionate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nessuna foto selezionata.", "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int conferma = JOptionPane.showConfirmDialog(this, "Sei sicuro di voler eliminare definitivamente " + selezionate.size() + " foto?", "Conferma eliminazione", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (conferma == JOptionPane.YES_OPTION) {
            ArrayList<Integer> idsDaEliminare = new ArrayList<>();
            for (FotografiaListItem item : selezionate) idsDaEliminare.add(item.foto().getIdFoto());
            controller.eliminazioneFotoGalleriaPrivata(idsDaEliminare);
            dispose();
        }
    }

    private record FotografiaListItem(Fotografia foto, String testoFormattato) {
        @Override
        public String toString() {
            return testoFormattato;
        }
    }
}