package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.BiConsumer;

public class UserHomePanel extends JPanel {

    private final DefaultTableModel model;
    private final JTable table;
    private final Runnable onRefresh;
    private final Runnable onLogout;
    private final TriConsumer<String, String, String> onOpenGallery;
    private final BiConsumer<String, String> onUploadFoto;

    public UserHomePanel(Runnable onRefresh, Runnable onLogout,
                         TriConsumer<String, String, String> onOpenGallery,
                         BiConsumer<String, String> onUploadFoto,
                         BiConsumer<String, String> onUploadVideo,
                         Runnable onImportGallery) {
        super(new BorderLayout(10, 10));
        this.onRefresh = onRefresh;
        this.onLogout = onLogout;
        this.onOpenGallery = onOpenGallery;
        this.onUploadFoto = onUploadFoto;

        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Le Mie Gallerie");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JButton refresh = new JButton("Aggiorna");
        JButton caricaFoto = new JButton("Aggiungi Foto");
        JButton logout = new JButton("Logout");

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(refresh);
        actions.add(caricaFoto);
        actions.add(logout);

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.WEST);
        top.add(actions, BorderLayout.EAST);

        model = new DefaultTableModel(new Object[]{"ID", "Nome Galleria", "Tipo Accesso"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Nascondi colonna ID per pulizia estetica
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refresh.addActionListener(e -> this.onRefresh.run());
        logout.addActionListener(e -> this.onLogout.run());

        caricaFoto.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Seleziona una galleria.");
                return;
            }
            onUploadFoto.accept((String)model.getValueAt(row, 0), (String)model.getValueAt(row, 1));
        });

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    int r = table.getSelectedRow();
                    onOpenGallery.accept(
                            (String)model.getValueAt(r, 0),
                            (String)model.getValueAt(r, 1),
                            (String)model.getValueAt(r, 2)
                    );
                }
            }
        });
    }

    public void reload(List<AppMain.GalleryItem> galleries) {
        model.setRowCount(0);
        for (var g : galleries) {
            model.addRow(new Object[]{g.id(), g.nome(), g.tipo()});
        }
    }

    @FunctionalInterface
    public interface TriConsumer<A, B, C> {
        void accept(A a, B b, C c);
    }
}