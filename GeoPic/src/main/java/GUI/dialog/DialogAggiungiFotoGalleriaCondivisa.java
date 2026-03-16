package GUI.dialog;

import Controller.Controller;
import Model.Fotografia;
import Model.GalleriaCondivisa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Dialog modale per aggiungere foto esistenti della galleria privata
 * alla galleria condivisa corrente.
 */
public class DialogAggiungiFotoGalleriaCondivisa extends DialogAggiungi {

    private final GalleriaCondivisa galleriaCondivisa;
    private final DefaultTableModel tableModel;
    private final ArrayList<Fotografia> fotoCandidabili;

    public DialogAggiungiFotoGalleriaCondivisa(Frame parentFrame, Controller controller, GalleriaCondivisa galleriaCondivisa) {
        super(parentFrame, "Aggiungi Foto a Galleria Condivisa", controller);
        this.galleriaCondivisa = galleriaCondivisa;
        this.fotoCandidabili = caricaFotoCandidabili();

        configuraDimensioni(700, 500, 650, 420, true);

        JPanel mainPanel = creaMainPanel();

        JLabel titolo = new JLabel("Seleziona le foto della tua galleria privata da aggiungere a: " + galleriaCondivisa.getNomeGalleria());
        mainPanel.add(titolo, BorderLayout.NORTH);

        tableModel = creaTableModel();
        JTable tabellaFoto = new JTable(tableModel);
        configuraTabella(tabellaFoto);

        JScrollPane scrollPane = new JScrollPane(tabellaFoto);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        montaContenutoConBottoniSalva(mainPanel, this::salvaFotoSelezionate);
    }

    private ArrayList<Fotografia> caricaFotoCandidabili() {
        if (galleriaCondivisa == null) {
            return new ArrayList<>();
        }
        return controller.getFotoCandidabiliCondivisione(galleriaCondivisa.getIdGalleria());
    }

    private DefaultTableModel creaTableModel() {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Seleziona", "ID", "Dispositivo", "Data Scatto", "Visibilità"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        for (Fotografia foto : fotoCandidabili) {
            Object[] d = controller.formattaRigaTabellaFoto(foto);
            model.addRow(new Object[]{
                    Boolean.FALSE,
                    d[0],
                    d[1],
                    d[2],
                    d[3]
            });
        }

        return model;
    }

    private void configuraTabella(JTable tabella) {
        tabella.setFillsViewportHeight(true);
        tabella.setRowHeight(24);
        tabella.getColumnModel().getColumn(0).setMaxWidth(90);
        tabella.getTableHeader().setReorderingAllowed(false);
    }

    private void salvaFotoSelezionate() {
        ArrayList<Fotografia> fotoSelezionate = new ArrayList<>();
        ArrayList<Fotografia> fotoPrivateSelezionate = new ArrayList<>();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object selected = tableModel.getValueAt(i, 0);
            if (Boolean.TRUE.equals(selected) && i < fotoCandidabili.size()) {
                Fotografia foto = fotoCandidabili.get(i);
                if (foto != null) {
                    fotoSelezionate.add(foto);
                    if (!foto.isVisibile()) {
                        fotoPrivateSelezionate.add(foto);
                    }
                }
            }
        }

        if (fotoSelezionate.isEmpty()) {
            mostraErrore("Seleziona almeno una foto da aggiungere.");
            return;
        }

        if (!fotoPrivateSelezionate.isEmpty()) {
            int scelta = JOptionPane.showConfirmDialog(
                    this,
                    creaPannelloConfermaPrivatizzate(fotoPrivateSelezionate),
                    "Conferma Cambio Visibilita",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (scelta != JOptionPane.YES_OPTION) {
                return;
            }
        }

        controller.AggiungiFotoCondivisa(new ArrayList<>(fotoSelezionate), galleriaCondivisa);

        String messaggio = "Foto aggiunte con successo alla galleria condivisa.";
        if (!fotoPrivateSelezionate.isEmpty()) {
            messaggio += "\nFoto rese pubbliche automaticamente: " + fotoPrivateSelezionate.size();
        }

        mostraSuccessoEChiudi(messaggio);
    }

    private JPanel creaPannelloConfermaPrivatizzate(ArrayList<Fotografia> fotoPrivateSelezionate) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel messaggio = new JLabel("Le seguenti foto sono private e verranno rese pubbliche se aggiunte:");
        panel.add(messaggio, BorderLayout.NORTH);

        JTable tabellaPrivate = new JTable(creaTableModelSoloLettura(fotoPrivateSelezionate));
        configuraTabella(tabellaPrivate);
        tabellaPrivate.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(tabellaPrivate);
        scrollPane.setPreferredSize(new Dimension(560, 180));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private DefaultTableModel creaTableModelSoloLettura(ArrayList<Fotografia> foto) {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Dispositivo", "Data Scatto", "Visibilità"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Fotografia f : foto) {
            Object[] d = controller.formattaRigaTabellaFoto(f);
            model.addRow(new Object[]{
                    d[0],
                    d[1],
                    d[2],
                    d[3]
            });
        }

        return model;
    }
}