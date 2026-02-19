package View;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.function.Consumer;

public class UploadFilePanel extends JPanel {

    private final JLabel title = new JLabel();
    private final JLabel hint = new JLabel();
    private final JLabel selected = new JLabel("Nessun file selezionato");

    private Runnable onBack = () -> {};
    private Consumer<String> onUpload = (p) -> {};

    private String selectedPath = null;

    public UploadFilePanel() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        JButton back = new JButton("Indietro");
        JButton choose = new JButton("Scegli file...");
        JButton upload = new JButton("Carica");

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.WEST);
        top.add(back, BorderLayout.EAST);

        JPanel center = new JPanel(new GridLayout(3, 1, 8, 8));
        center.add(hint);
        center.add(choose);
        center.add(selected);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(upload);

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        back.addActionListener(e -> onBack.run());

        choose.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int res = fc.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                selectedPath = f.getAbsolutePath();
                selected.setText("Selezionato: " + selectedPath);
            }
        });

        upload.addActionListener(e -> {
            if (selectedPath == null) {
                JOptionPane.showMessageDialog(this, "Seleziona prima un file.");
                return;
            }
            onUpload.accept(selectedPath);
            JOptionPane.showMessageDialog(this, "Operazione completata!");
            // reset dopo upload
            reset();
        });
    }

    public void configure(String titleText, String hintText, Runnable onBack, Consumer<String> onUpload) {
        this.title.setText(titleText);
        this.hint.setText(hintText);
        this.onBack = onBack;
        this.onUpload = onUpload;
        reset();
    }

    public void reset() {
        selectedPath = null;
        selected.setText("Nessun file selezionato");
    }
}