package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UploadFilePanel extends JPanel {
    private final JLabel title = new JLabel();
    private final JTextField dispositivoField = new JTextField();
    private final JTextField dataScattoField = new JTextField();
    private final JTextField latField = new JTextField();
    private final JTextField lonField = new JTextField();
    private final JTextField luogoField = new JTextField();
    private final DefaultTableModel soggettiModel;
    private final JTable soggettiTable;

    private Runnable onBack = () -> {};
    private Consumer<DatiFotoInput> onSave = d -> {};

    public UploadFilePanel() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        int row = 0;
        addField(form, c, row++, "Dispositivo:", dispositivoField);
        addField(form, c, row++, "Data scatto (yyyy-MM-dd HH:mm:ss):", dataScattoField);
        addField(form, c, row++, "Coordinate - Spazio 1 (latitudine):", latField);
        addField(form, c, row++, "Coordinate - Spazio 2 (longitudine):", lonField);
        addField(form, c, row++, "Nome luogo:", luogoField);

        c.gridx = 0; c.gridy = row; c.gridwidth = 2;

        soggettiModel = new DefaultTableModel(new Object[]{"Soggetto", "Categoria"}, 0);
        soggettiTable = new JTable(soggettiModel);

        JPanel soggettiPanel = new JPanel(new BorderLayout(5, 5));
        soggettiPanel.setBorder(BorderFactory.createTitledBorder("Soggetti (ogni soggetto ha una categoria)"));
        soggettiPanel.add(new JScrollPane(soggettiTable), BorderLayout.CENTER);

        JButton addS = new JButton("Aggiungi soggetto");
        JButton remS = new JButton("Rimuovi soggetto");
        JPanel sogBtn = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sogBtn.add(addS); sogBtn.add(remS);
        soggettiPanel.add(sogBtn, BorderLayout.SOUTH);
        addS.addActionListener(e -> soggettiModel.addRow(new Object[]{"", ""}));
        remS.addActionListener(e -> { int r = soggettiTable.getSelectedRow(); if (r != -1) soggettiModel.removeRow(r); });

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.add(form, BorderLayout.NORTH);
        center.add(soggettiPanel, BorderLayout.CENTER);

        JButton save = new JButton("Salva");
        JButton back = new JButton("Annulla");
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(back); south.add(save);

        add(title, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        save.addActionListener(e -> handleSave());
        back.addActionListener(e -> onBack.run());
    }

    private void addField(JPanel form, GridBagConstraints c, int row, String label, JTextField field) {
        c.gridwidth = 1;
        c.gridx = 0; c.gridy = row; c.weightx = 0.35;
        form.add(new JLabel(label), c);
        c.gridx = 1; c.weightx = 0.65;
        form.add(field, c);
    }

    private void handleSave() {
        String dispositivo = dispositivoField.getText().trim();
        String data = dataScattoField.getText().trim();
        String lat = latField.getText().trim();
        String lon = lonField.getText().trim();
        String luogo = luogoField.getText().trim();

        if (dispositivo.isEmpty()) { JOptionPane.showMessageDialog(this, "Inserisci il dispositivo."); return; }
        if (lat.isEmpty() || lon.isEmpty()) { JOptionPane.showMessageDialog(this, "Inserisci le coordinate in 2 spazi (latitudine e longitudine)."); return; }
        if (luogo.isEmpty()) { JOptionPane.showMessageDialog(this, "Inserisci il nome del luogo."); return; }

        List<SubjectInput> soggetti = new ArrayList<>();
        for (int r = 0; r < soggettiModel.getRowCount(); r++) {
            String nome = String.valueOf(soggettiModel.getValueAt(r, 0) == null ? "" : soggettiModel.getValueAt(r, 0)).trim();
            String cat = String.valueOf(soggettiModel.getValueAt(r, 1) == null ? "" : soggettiModel.getValueAt(r, 1)).trim();
            if (nome.isEmpty() || cat.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ogni soggetto deve avere nome e categoria (riga " + (r + 1) + ").");
                return;
            }
            soggetti.add(new SubjectInput(nome, cat));
        }

        onSave.accept(new DatiFotoInput(dispositivo, data, lat, lon, luogo, soggetti));
        JOptionPane.showMessageDialog(this, "Foto inserita (demo).\nPuoi aprirla dalla galleria per vedere i dettagli.");
    }

    public void configure(String t, Runnable b, Consumer<DatiFotoInput> s) {
        title.setText(t);
        onBack = b != null ? b : () -> {};
        onSave = s != null ? s : d -> {};
        clear();
    }

    private void clear() {
        dispositivoField.setText("");
        dataScattoField.setText("");
        latField.setText("");
        lonField.setText("");
        luogoField.setText("");
        soggettiModel.setRowCount(0);
    }

    public record SubjectInput(String nome, String categoria) {}
    public record DatiFotoInput(String dispositivo, String dataScatto, String latitudine, String longitudine, String luogo, List<SubjectInput> soggetti) {}
}
