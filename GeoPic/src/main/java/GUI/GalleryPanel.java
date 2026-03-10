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
     * Costruisce il pannello principale della galleria, organizzando header,
     * carosello degli slideshow e la griglia delle immagini.
     * @param fotografie Lista delle entità {@link Fotografia} da visualizzare.
     * @param onImageClick Callback che riceve l'indice della foto cliccata per gestire la navigazione.
     * @param slideshowList Lista delle entità {@link Video} (slideshow) disponibili.
     * @param onSlideshowClick Callback che gestisce il clic su un elemento del carosello.
     * @param controller Il {@link Controller.Controller} di riferimento per le operazioni utente.
     */
    public GalleryPanel(List<Fotografia> fotografie, IntConsumer onImageClick, List<Video> slideshowList, Consumer<Video> onSlideshowClick, Controller.Controller controller) {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        
        Model.Utente utenteCorrente = controller.getLoggedInUtente();
        String username = utenteCorrente != null ? utenteCorrente.getUsername() : "Sconosciuto";
        String idUtente = utenteCorrente != null ? String.valueOf(utenteCorrente.getIdUtente()) : "N/D";

        topPanel.add(creaHeader(username, idUtente, controller), BorderLayout.NORTH);

        if (slideshowList != null && !slideshowList.isEmpty()) {
            SlideshowCarouselPanel carousel = new SlideshowCarouselPanel(slideshowList, onSlideshowClick);
            topPanel.add(carousel, BorderLayout.SOUTH);
        }

        add(topPanel, BorderLayout.NORTH);
        add(creaGalleriaImmagini(fotografie, onImageClick), BorderLayout.CENTER);
    }

    /**
     * Crea il pannello informativo dell'utente e il pannello comandi laterale.
     * Il pannello include etichette con le info dell'utente e pulsanti per l'interazione
     * con le funzionalità di ricerca e inserimento dati.
     * @param username Nome utente del proprietario della galleria.
     * @param id Identificativo univoco dell'utente.
     * @param controller Riferimento al controller per attivare i dialoghi.
     * @return {@link JPanel} configurato con layout BorderLayout.
     */
    private JPanel creaHeader(String username, String id, Controller.Controller controller) {
        // Pannello principale che contiene le info utente e il pannello comandi
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 30, 15));
        headerPanel.setBackground(new Color(230, 230, 250));

        // Pannello per visualizzare i dati dell'utente (allineato a sinistra)
        JPanel userInfoPanel = new JPanel(new GridLayout(2, 1));
        userInfoPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Galleria Personale di " + username);
        userLabel.setFont(new Font("Arial", Font.BOLD, 30));

        JLabel idLabel = new JLabel("ID: " + id);

        userInfoPanel.add(userLabel);
        userInfoPanel.add(idLabel);

        headerPanel.add(userInfoPanel, BorderLayout.WEST);

        // Pannello a griglia verticale per mettere tutti i bottoni impilati e uguali
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 0, 5));
        buttonPanel.setOpaque(false);

        // Utility method al volo per creare bottoni uniformi
        JButton addPhotoButton = new JButton("Aggiungi Foto");
        addPhotoButton.setFocusPainted(false);
        addPhotoButton.addActionListener(e -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            AggiungiFotoDialog dialog = new AggiungiFotoDialog((Frame) parentWindow, controller);
            dialog.setVisible(true);
        });

        JButton btnFotoStessoLuogo = new JButton("Foto per Luogo");
        btnFotoStessoLuogo.setFocusPainted(false);
        btnFotoStessoLuogo.addActionListener(e -> JOptionPane.showMessageDialog(this, "Funzionalità 'Foto per Luogo' da implementare!", "Ricerca", JOptionPane.INFORMATION_MESSAGE));

        JButton btnFotoStessoSoggetto = new JButton("Foto per Soggetto");
        btnFotoStessoSoggetto.setFocusPainted(false);
        btnFotoStessoSoggetto.addActionListener(e -> JOptionPane.showMessageDialog(this, "Funzionalità 'Foto per Soggetto' da implementare!", "Ricerca", JOptionPane.INFORMATION_MESSAGE));

        JButton btnTop3Luoghi = new JButton("Top 3 Luoghi");
        btnTop3Luoghi.setFocusPainted(false);
        btnTop3Luoghi.addActionListener(e -> JOptionPane.showMessageDialog(this, "Funzionalità 'Top 3 Luoghi' da implementare!", "Classifica", JOptionPane.INFORMATION_MESSAGE));

        buttonPanel.add(btnFotoStessoLuogo);
        buttonPanel.add(btnFotoStessoSoggetto);
        buttonPanel.add(btnTop3Luoghi);
        buttonPanel.add(addPhotoButton);

        // Contenitore per non stretchare a dismisura i bottoni nel BorderLayout.EAST
        JPanel buttonContainer = new JPanel(new BorderLayout());
        buttonContainer.setOpaque(false);
        buttonContainer.add(buttonPanel, BorderLayout.NORTH);

        headerPanel.add(buttonContainer, BorderLayout.EAST);
        return headerPanel;
    }

    /**
     * Costruisce un pannello a scorrimento contenente la griglia delle fotografie.
     * Utilizza {@link WrapLayout} per garantire che il layout sia responsivo
     * rispetto alla dimensione della finestra.
     * @param fotografie Lista delle fotografie da disporre nella griglia.
     * @param onImageClick Interfaccia funzionale per gestire l'evento di selezione immagine.
     * @return {@link JScrollPane} che contiene il pannello {@code imagePanel} con barra di scorrimento.
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
     * Crea un componente bottone che rappresenta graficamente una singola fotografia.
     * Include una breve descrizione testuale (ID e dispositivo) visualizzata tramite HTML.
     * @param Foto Oggetto {@link Fotografia} da rappresentare.
     * @return {@link JButton} formattato per la visualizzazione nella griglia.
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
