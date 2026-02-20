package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

public class UserHomePanel extends JPanel {

    private final DefaultTableModel model;
    private final JTable table;
    private Consumer<String> onSearchLoc = s -> {};
    private Consumer<String> onSearchSog = s -> {};
    private Runnable onShowTop3 = () -> {};

    public UserHomePanel(Runnable onRefresh,
                         Runnable onLogout,
                         AppMain.TriConsumer<String, String, String> onOpenGallery,
                         Runnable onCreateSharedGallery) {

        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel top = new JPanel(new BorderLayout());
        JLabel title = new JLabel("GeoPic - Home Utente");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> onLogout.run());
        top.add(title, BorderLayout.WEST);
        top.add(btnLogout, BorderLayout.EAST);

        model = new DefaultTableModel(new Object[]{"ID", "Nome", "Tipo", "Partecipanti"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);

        JPanel southPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNewShared = new JButton("Nuova Galleria Condivisa");
        JButton btnOpen = new JButton("Apri Galleria");
        JButton btnRefresh = new JButton("Aggiorna");
        JButton btnT3 = new JButton("Top 3 Luoghi");
        btnNewShared.addActionListener(e -> onCreateSharedGallery.run());
        btnOpen.addActionListener(e -> openSelectedGallery(onOpenGallery));
        btnRefresh.addActionListener(e -> onRefresh.run());
        btnT3.addActionListener(e -> onShowTop3.run());
        row1.add(btnNewShared); row1.add(btnOpen); row1.add(btnRefresh); row1.add(btnT3);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField search = new JTextField(18);
        JButton bLoc = new JButton("Cerca Coordinate");
        JButton bSog = new JButton("Cerca Soggetto");
        bLoc.addActionListener(e -> onSearchLoc.accept(search.getText()));
        bSog.addActionListener(e -> onSearchSog.accept(search.getText()));
        row2.add(new JLabel("Cerca: ")); row2.add(search); row2.add(bLoc); row2.add(bSog);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row3.add(new JLabel("Nota: i video si creano e si visualizzano nella galleria personale."));

        southPanel.add(row1); southPanel.add(row2); southPanel.add(row3);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) openSelectedGallery(onOpenGallery);
            }
        });
    }

    private void openSelectedGallery(AppMain.TriConsumer<String, String, String> onOpenGallery) {
        int r = table.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona una galleria.");
            return;
        }
        onOpenGallery.accept((String) model.getValueAt(r, 0), (String) model.getValueAt(r, 1), (String) model.getValueAt(r, 2));
    }

    public void setSearchActions(Consumer<String> loc, Consumer<String> sog, Runnable t3) {
        this.onSearchLoc = loc != null ? loc : s -> {};
        this.onSearchSog = sog != null ? sog : s -> {};
        this.onShowTop3 = t3 != null ? t3 : () -> {};
    }

    public void reload(List<AppMain.GalleryItem> galleries) {
        model.setRowCount(0);
        if (galleries != null) {
            for (AppMain.GalleryItem g : galleries) {
                model.addRow(new Object[]{g.getId(), g.getNome(), g.getTipo(), String.join(", ", g.getPartecipanti())});
            }
        }
    }
}
