package GUI;

import Model.Fotografia;
import Model.Video;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class GalleryPanel extends JPanel {

    private static final int IMAGE_SIZE = 160;

    /**
     * Questo è il pannello principale della galleria,
     * contiene tutti gli elementi grafici inerenti alla dashboard iniziale.
     * @param fotografie Lista di fotografie da db
     * @param onImageClick Indice ritornato dalla callback action al clic sull'immagine per aprire la visuale in dettaglio.
     * @see GalleryPanelContainer
     */

    public GalleryPanel(List<Fotografia> fotografie, IntConsumer onImageClick, List<Video> slideshowList, Consumer<Video> onSlideshowClick) {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(creaHeader("ADMIN", "0000000", "17/04/2025"), BorderLayout.NORTH);

        if (slideshowList != null && !slideshowList.isEmpty()) {
            SlideshowCarouselPanel carousel = new SlideshowCarouselPanel(slideshowList, onSlideshowClick);
            topPanel.add(carousel, BorderLayout.SOUTH);
        }

        add(topPanel, BorderLayout.NORTH);
        add(creaGalleriaImmagini(fotografie, onImageClick), BorderLayout.CENTER);
    }

    /**
     * Crea un pannello che visualizza i dati dell'utente come:
     * @param username Nome utente
     * @param id Id utente
     * @param dataCreazione Data creazione Account
     * @return {@link JPanel} Pannello Header
     */
    private JPanel creaHeader(String username, String id, String dataCreazione) {
        JPanel headerPanel = new JPanel(new GridLayout(3, 1));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 30, 15));
        headerPanel.setBackground(new Color(230, 230, 250));

        JLabel userLabel = new JLabel("Galleria Personale di " + username );
        userLabel.setFont(new Font("Arial", Font.BOLD, 30));

        headerPanel.add(userLabel);
        headerPanel.add(new JLabel("ID: " + id));
        headerPanel.add(new JLabel("Data creazione: " + dataCreazione));

        return headerPanel;
    }

    /**
     * Crea la griglia di fotografie e associa ad ogni singola fotografia un indice.
     * @param fotografie Array di fotografie
     * @param onImageClick Indice da ritornare al costruttore di GalleryPanel definito in
     * {@link GalleryPanelContainer#GalleryPanelContainer}.
     * @see WrapLayout Layout per la gestire la griglia in maniera responsive.
     * @return {@link JPanel}Pannello griglia di immagini con scoll verticale.
     */
    private JScrollPane creaGalleriaImmagini(List<Fotografia> fotografie, IntConsumer onImageClick) {
        JPanel imagePanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 5, 5));

        for (int i = 0; i < fotografie.size(); i++) {
            int index = i;
            JButton button = creaImmagine(fotografie.get(i));
            button.addActionListener(e -> onImageClick.accept(index));
            imagePanel.add(button);
        }
        JScrollPane scrollPane = new JScrollPane(imagePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }

    /**
     * Crea i bottoni (dati) da mettere all'interno della griglia.
     * @param Foto Foto passata da {@link GalleryPanel#creaGalleriaImmagini}
     * @return {@link JButton} bottone con foto.
     */
    private JButton creaImmagine(Fotografia Foto) {
        JButton button = new JButton("<html><div style='text-align: center;'><b>ID Foto: " + Foto.getIdFoto() + "</b><br/>" + "Dispositivo:<br/>" + Foto.getDispositivo() + "</div></html>");
        button.setPreferredSize(new Dimension(IMAGE_SIZE, IMAGE_SIZE));
        button.setBackground(new Color(240, 240, 240));
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        button.setFocusPainted(false);

        return button;
    }
}
