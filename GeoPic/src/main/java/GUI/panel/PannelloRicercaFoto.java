package GUI.panel;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Componente riusabile per ricerca foto con testo libero e tipo ricerca selezionabile.
 */
public class PannelloRicercaFoto extends JPanel {

    public static final String TIPO_LUOGO = "Luogo";
    public static final String TIPO_SOGGETTO = "Soggetto";

    private final JComboBox<String> comboTipoRicerca;
    private final JTextField campoRicerca;

    public PannelloRicercaFoto(Consumer<RichiestaRicerca> onSearch) {
        setOpaque(false);
        setLayout(new BorderLayout(8, 0));

        comboTipoRicerca = new JComboBox<>(new String[]{TIPO_LUOGO, TIPO_SOGGETTO});
        campoRicerca = new JTextField(18);
        JButton btnCerca = new JButton("Cerca");

        JPanel inputPanel = new JPanel(new BorderLayout(6, 0));
        inputPanel.setOpaque(false);
        inputPanel.add(comboTipoRicerca, BorderLayout.WEST);
        inputPanel.add(campoRicerca, BorderLayout.CENTER);

        add(inputPanel, BorderLayout.CENTER);
        add(btnCerca, BorderLayout.EAST);

        btnCerca.addActionListener(_ -> invocaRicerca(onSearch));
        campoRicerca.addActionListener(_ -> invocaRicerca(onSearch));
    }

    private void invocaRicerca(Consumer<RichiestaRicerca> onSearch) {
        if (onSearch == null) {
            return;
        }

        String tipo = Objects.toString(comboTipoRicerca.getSelectedItem(), TIPO_LUOGO);
        String query = campoRicerca.getText() != null ? campoRicerca.getText().trim() : "";
        onSearch.accept(new RichiestaRicerca(tipo, query));
    }

    public record RichiestaRicerca(String tipo, String query) {
    }
}
