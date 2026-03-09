package GUI;

import javax.swing.*;
import java.awt.*;

public class MetadataPanel extends JPanel {
    /**
     * Crea un pannello da mostrare in {@link ImageSelector}/{@link SlideshowSelector}
     * per i metadati della Foto/Slideshow mostrata.
     * @param id Identificativo alfanumerico della foto o slidewhow
     * @param dispositivo Dispositivo con cui è stata scattata la foto
     * @param data Data di scatto della foto
     * @param luogo Luogo dove è stata scattata la foto
     */
    public MetadataPanel(String id, String dispositivo, String autore, String data, String luogo) {
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));


        Font font = new Font("SansSerif", Font.PLAIN, 12);

        add(createTagLabel("ID: " + id, font));
        add(createTagLabel("Dispositivo: " + dispositivo, font));
        add(createTagLabel("Autore: " + autore, font));
        add(createTagLabel("Data: " + data, font));
        add(createTagLabel("Luogo: " + luogo, font));
    }

    /**
     * Crea gli elementi grafici per le etichette
     * @param text testo dell'etichetta
     * @param font tipo di carattere dell'etichetta
     * @return un etichetta JLabel
     */

    private JLabel createTagLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(new Color(220, 220, 220));
        label.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        label.setFont(font);
        return label;
    }
}
