package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class AdminHomePanel extends JPanel {

    private final DefaultTableModel model;
    private final JTable table;

    private final Runnable onRefresh;
    private final Consumer<String> onDeleteUserById;
    private final Runnable onLogout;

    public AdminHomePanel(Runnable onRefresh, Consumer<String> onDeleteUserById, Runnable onLogout) {
        super(new BorderLayout(10, 10));
        this.onRefresh = onRefresh;
        this.onDeleteUserById = onDeleteUserById;
        this.onLogout = onLogout;

        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Admin - Gestione Utenti");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JButton refresh = new JButton("Aggiorna");
        JButton delete = new JButton("Elimina Utente");
        JButton logout = new JButton("Logout");

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(refresh);
        actions.add(delete);
        actions.add(logout);

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.WEST);
        top.add(actions, BorderLayout.EAST);

        model = new DefaultTableModel(new Object[]{"ID", "Username"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        // Nascondiamo l'ID (CHAR 5) per estetica
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refresh.addActionListener(e -> this.onRefresh.run());
        logout.addActionListener(e -> this.onLogout.run());

        delete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String userId = (String) model.getValueAt(row, 0);
                String username = (String) model.getValueAt(row, 1);
                int confirm = JOptionPane.showConfirmDialog(this, "Eliminare " + username + "?", "Conferma", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) onDeleteUserById.accept(userId);
            } else {
                JOptionPane.showMessageDialog(this, "Seleziona un utente.");
            }
        });
    }

    // --- PUNTO CRITICO: USA I GETTER ---
    public void reload(List<AppMain.UserItem> users) {
        model.setRowCount(0);
        if (users == null) return;
        for (AppMain.UserItem u : users) {
            // Chiamiamo i metodi definiti nella classe UserItem in AppMain
            model.addRow(new Object[]{u.getId(), u.getUsername()});
        }
    }
}