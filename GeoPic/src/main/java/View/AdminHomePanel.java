
package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.function.BooleanSupplier;
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

        JLabel title = new JLabel("Admin - Gestione utenti");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        JButton refresh = new JButton("Aggiorna");
        JButton delete = new JButton("Elimina selezionato");
        JButton logout = new JButton("Logout");

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(refresh);
        actions.add(delete);
        actions.add(logout);
        top.add(actions, BorderLayout.EAST);

        model = new DefaultTableModel(new Object[]{"ID", "Username"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hideIdColumn(table, 0);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refresh.addActionListener(e -> this.onRefresh.run());
        logout.addActionListener(e -> this.onLogout.run());
        delete.addActionListener(e -> deleteSelected());
    }

    public void reload(List<AppMain.UserItem> users) {
        model.setRowCount(0);
        for (var u : users) model.addRow(new Object[]{u.id(), u.username()});
    }

    private void deleteSelected() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona un utente.");
            return;
        }
        int row = table.convertRowIndexToModel(viewRow);
        String userId = (String) model.getValueAt(row, 0);
        String username = (String) model.getValueAt(row, 1);

        int ok = JOptionPane.showConfirmDialog(
                this,
                "Eliminare l'utente '" + username + "'?",
                "Conferma",
                JOptionPane.YES_NO_OPTION
        );

        if (ok == JOptionPane.YES_OPTION) {
            onDeleteUserById.accept(userId);
            model.removeRow(row);
        }
    }

    private static void hideIdColumn(JTable table, int colIndex) {
        var col = table.getColumnModel().getColumn(colIndex);
        col.setMinWidth(0);
        col.setMaxWidth(0);
        col.setPreferredWidth(0);
    }
}