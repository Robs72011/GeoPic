package View;

import Model.Soggetto;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;

public class UploadFilePanel extends JPanel {

    private final JLabel title = new JLabel();

    private Runnable onBack = () -> {};

    // callback verso il controller:
    // dispositivo, luogo, soggetti inseriti
    private Consumer<DatiFotoInput> onSave = (d) -> {};

    // campi form
    private final JTextField dispositivoField = new JTextField();
    private final JTextField luogoField = new JTextField();

    // inserimento soggetti
    private final JTextField nomeSoggettoField = new JTextField();
    private final JTextField categoriaField = new JTextField();

    private final DefaultListModel<Soggetto> soggettiModel = new DefaultListModel<>();
    private final JList<Soggetto> soggettiList = new JList<>(soggettiModel);

    public UploadFilePanel() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        JButton back = new JButton("Indietro");
        JButton addSoggetto = new JButton("Aggiungi soggetto");
        JButton save = new JButton("Salva Foto");

        // --- TOP ---
        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.WEST);
        top.add(back, BorderLayout.EAST);

        // --- FORM ---
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        form.add(new JLabel("Dispositivo (obbligatorio):"));
        form.add(dispositivoField);

        form.add(Box.createVerticalStrut(10));

        form.add(new JLabel("Luogo (opzionale):"));
        form.add(luogoField);

        form.add(Box.createVerticalStrut(20));
        form.add(new JLabel("Inserisci soggetto"));

        form.add(new JLabel("Nome:"));
        form.add(nomeSoggettoField);

        form.add(new JLabel("Categoria:"));
        form.add(categoriaField);

        form.add(addSoggetto);

        form.add(Box.createVerticalStrut(10));
        form.add(new JLabel("Soggetti inseriti:"));
        form.add(new JScrollPane(soggettiList));

        // --- BOTTOM ---
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(save);

        add(top, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // --- AZIONI ---
        back.addActionListener(e -> onBack.run());

        addSoggetto.addActionListener(e -> aggiungiSoggetto());

        save.addActionListener(e -> salva());
    }

    private void aggiungiSoggetto() {
        String nome = nomeSoggettoField.getText().trim();
        String categoria = categoriaField.getText().trim();

        if (nome.isEmpty() || categoria.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inserisci nome e categoria");
            return;
        }

        // uso direttamente il tuo Model
        Soggetto s = new Soggetto(nome, categoria);
        soggettiModel.addElement(s);

        nomeSoggettoField.setText("");
        categoriaField.setText("");
    }

    private void salva() {
        String dispositivo = dispositivoField.getText().trim();
        String luogo = luogoField.getText().trim();

        if (dispositivo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Il dispositivo è obbligatorio.");
            return;
        }

        ArrayList<Soggetto> soggetti = new ArrayList<>();
        for (int i = 0; i < soggettiModel.size(); i++) {
            soggetti.add(soggettiModel.get(i));
        }

        // passo i dati al controller
        onSave.accept(new DatiFotoInput(dispositivo, luogo, soggetti));

        JOptionPane.showMessageDialog(this, "Foto inserita!");
        reset();
    }

    public void configure(String titleText,
                          Runnable onBack,
                          Consumer<DatiFotoInput> onSave) {

        this.title.setText(titleText);
        this.onBack = onBack;
        this.onSave = onSave;
        reset();
    }

    public void reset() {
        dispositivoField.setText("");
        luogoField.setText("");
        nomeSoggettoField.setText("");
        categoriaField.setText("");
        soggettiModel.clear();
    }

    /**
     * Piccolo contenitore SOLO per passare i dati alla controller.
     * NON è un DTO di dominio, serve solo come evento della GUI.
     */
    public static class DatiFotoInput {
        public final String dispositivo;
        public final String luogo;
        public final ArrayList<Soggetto> soggetti;

        public DatiFotoInput(String dispositivo, String luogo, ArrayList<Soggetto> soggetti) {
            this.dispositivo = dispositivo;
            this.luogo = luogo;
            this.soggetti = soggetti;
        }
    }
}