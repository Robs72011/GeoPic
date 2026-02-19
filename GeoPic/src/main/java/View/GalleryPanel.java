package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GalleryPanel extends JPanel {

    private final JLabel title = new JLabel();
    private final DefaultTableModel model;
    private final JTable table;

    private String currentGalleryId;

    private final Runnable onBackToUser;
    private final Consumer<String> onRefreshPhotosByGalleryId;
    private final BiConsumer<String, String> onOpenPhoto; // (photoId, titolo)

    public GalleryPanel(Runnable onBackToUser,
                        Consumer<String> onRefreshPhotosByGalleryId,
                        BiConsumer<String, String> onOpenPhoto) {
        super(new BorderLayout(10, 10));
        this.onBackToUser = onBackToUser;
        this.onRefreshPhotosByGalleryId = onRefreshPhotosByGalleryId;
        this.onOpenPhoto = onOpenPhoto;

        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        JButton back = new JButton("Indietro");
        JButton refresh = new JButton("Aggiorna");

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(refresh);
        actions.add(back);
        top.add(actions, BorderLayout.EAST);

        model = new DefaultTableModel(new Object[]{"ID", "Titolo"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hideIdColumn(table, 0);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        back.addActionListener(e -> onBackToUser.run());
        refresh.addActionListener(e -> {
            if (currentGalleryId != null) onRefreshPhotosByGalleryId.accept(currentGalleryId);
        });

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int viewRow = table.getSelectedRow();
                    if (viewRow == -1) return;
                    int row = table.convertRowIndexToModel(viewRow);
                    String photoId = (String) model.getValueAt(row, 0);
                    String titolo = (String) model.getValueAt(row, 1);
                    onOpenPhoto.accept(photoId, titolo);
                }
            }
        });
    }

    public void setGalleryContext(String galleryId, String nome, String tipo) {
        this.currentGalleryId = galleryId;
        title.setText("Galleria: " + nome + " (" + tipo + ")");
    }

    public void reload(List<AppMain.PhotoItem> photos) {
        model.setRowCount(0);
        for (var p : photos) model.addRow(new Object[]{p.id(), p.titolo()});
    }

    private static void hideIdColumn(JTable table, int colIndex) {
        var col = table.getColumnModel().getColumn(colIndex);
        col.setMinWidth(0);
        col.setMaxWidth(0);
        col.setPreferredWidth(0);
    }
}