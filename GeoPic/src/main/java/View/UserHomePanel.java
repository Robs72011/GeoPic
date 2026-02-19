package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class UserHomePanel extends JPanel {

    private final DefaultTableModel model;
    private final JTable table;

    private final Runnable onRefresh;
    private final Runnable onLogout;
    private final TriConsumer<String, String, String> onOpenGallery;
    private final BiConsumer<String, String> onUploadFoto;   // (galleryId, galleryName)
    private final BiConsumer<String, String> onUploadVideo;  // (galleryId, galleryName)
    private final Runnable onImportGallery;

    public UserHomePanel(
            Runnable onRefresh,
            Runnable onLogout,
            TriConsumer<String, String, String> onOpenGallery,
            BiConsumer<String, String> onUploadFoto,
            BiConsumer<String, String> onUploadVideo,
            Runnable onImportGallery
    ) {
        super(new BorderLayout(10, 10));
        this.onRefresh = onRefresh;
        this.onLogout = onLogout;
        this.onOpenGallery = onOpenGallery;
        this.onUploadFoto = onUploadFoto;
        this.onUploadVideo = onUploadVideo;
        this.onImportGallery = onImportGallery;

        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Utente - Home");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        JButton refresh = new JButton("Aggiorna");
        JButton caricaFoto = new JButton("Carica Foto");
        JButton caricaGalleria = new JButton("Carica Galleria");
        JButton caricaVideo = new JButton("Carica Video");
        JButton logout = new JButton("Logout");

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(refresh);
        actions.add(caricaFoto);
        actions.add(caricaGalleria);
        actions.add(caricaVideo);
        actions.add(logout);
        top.add(actions, BorderLayout.EAST);

        model = new DefaultTableModel(new Object[]{"ID", "Nome", "Tipo"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        hideIdColumn(table, 0);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refresh.addActionListener(e -> this.onRefresh.run());
        logout.addActionListener(e -> this.onLogout.run());

        caricaGalleria.addActionListener(e -> this.onImportGallery.run());

        caricaFoto.addActionListener(e -> {
            var sel = getSelectedGallery();
            if (sel == null) {
                JOptionPane.showMessageDialog(this, "Seleziona una galleria prima di caricare una foto.");
                return;
            }
            onUploadFoto.accept(sel.id, sel.nome);
        });

        caricaVideo.addActionListener(e -> {
            var sel = getSelectedGallery();
            if (sel == null) {
                JOptionPane.showMessageDialog(this, "Seleziona una galleria prima di caricare un video.");
                return;
            }
            onUploadVideo.accept(sel.id, sel.nome);
        });

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    var sel = getSelectedGallery();
                    if (sel == null) return;
                    onOpenGallery.accept(sel.id, sel.nome, sel.tipo);
                }
            }
        });
    }

    public void reload(List<AppMain.GalleryItem> galleries) {
        model.setRowCount(0);
        for (var g : galleries) model.addRow(new Object[]{g.id(), g.nome(), g.tipo()});
    }

    private SelectedGallery getSelectedGallery() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) return null;
        int row = table.convertRowIndexToModel(viewRow);

        String id = (String) model.getValueAt(row, 0);
        String nome = (String) model.getValueAt(row, 1);
        String tipo = (String) model.getValueAt(row, 2);
        return new SelectedGallery(id, nome, tipo);
    }

    private record SelectedGallery(String id, String nome, String tipo) {}

    private static void hideIdColumn(JTable table, int colIndex) {
        var col = table.getColumnModel().getColumn(colIndex);
        col.setMinWidth(0);
        col.setMaxWidth(0);
        col.setPreferredWidth(0);
    }

    @FunctionalInterface
    public interface TriConsumer<A, B, C> {
        void accept(A a, B b, C c);
    }
}