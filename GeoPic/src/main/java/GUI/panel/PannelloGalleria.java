package GUI.panel;

import GUI.utility.WrapLayout;
import Model.Fotografia;
import Model.Video;
import Controller.Controller;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public abstract class PannelloGalleria extends JPanel {

    private static final int IMAGE_SIZE = 160;

    protected final Controller controller;
    protected final Runnable onRefresh;
    private final ArrayList<Fotografia> fotografieMostrate;
    private final IntConsumer onImageClick;

    protected PannelloGalleria(ArrayList<Fotografia> fotografie,
                               IntConsumer onImageClick,
                               Controller controller,
                               Runnable onRefresh) {
        this.controller = controller;
        this.onRefresh = onRefresh;
        this.fotografieMostrate = fotografie != null ? new ArrayList<>(fotografie) : new ArrayList<>();
        this.onImageClick = onImageClick;
        setLayout(new BorderLayout());
    }

    protected final void inizializzaPannello(String customTitle,
                                             String customSubtitle,
                                             ArrayList<Video> slideshowList,
                                             Consumer<Video> onSlideshowClick) {
        String headerTitle = customTitle;
        String headerSubtitle = customSubtitle;
        if (headerTitle == null || headerSubtitle == null) {
            Model.Utente utenteCorrente = controller.getLoggedInUtente();
            String username = utenteCorrente != null ? utenteCorrente.getUsername() : "Sconosciuto";
            String idUtente = utenteCorrente != null ? String.valueOf(utenteCorrente.getIdUtente()) : "N/D";

            headerTitle = "Galleria Personale di " + username;
            headerSubtitle = "ID: " + idUtente;
        }

        JPanel topPanel = new JPanel(new BorderLayout());

        topPanel.add(creaHeader(headerTitle, headerSubtitle), BorderLayout.NORTH);

        if (slideshowList != null && !slideshowList.isEmpty()) {
            PannelloSelezioneSlideshow carousel = new PannelloSelezioneSlideshow(slideshowList, onSlideshowClick);
            topPanel.add(carousel, BorderLayout.SOUTH);
        }

        add(topPanel, BorderLayout.NORTH);
        add(creaGalleriaImmagini(this.fotografieMostrate, onImageClick), BorderLayout.CENTER);
    }

    /**
     * Crea il pannello informativo dell'utente e il pannello comandi laterale.
     * Il pannello include etichette con le info dell'utente e pulsanti per l'interazione
     * con le funzionalità di ricerca e inserimento dati.
     * @return {@link JPanel} configurato con layout BorderLayout.
     */
    private JPanel creaHeader(String headerTitle, String headerSubtitle) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 30, 15));
        headerPanel.setBackground(new Color(230, 230, 250));

        JPanel userInfoPanel = new JPanel(new GridLayout(2, 1));
        userInfoPanel.setOpaque(false);

        JLabel userLabel = new JLabel(headerTitle);
        userLabel.setFont(new Font("Arial", Font.BOLD, 30));

        JLabel idLabel = new JLabel(headerSubtitle);

        userInfoPanel.add(userLabel);
        userInfoPanel.add(idLabel);

        headerPanel.add(userInfoPanel, BorderLayout.WEST);

        JPanel pannelloComandi = creaPannelloComandiHeader();
        if (pannelloComandi != null) {
            headerPanel.add(pannelloComandi, BorderLayout.EAST);
        }
        return headerPanel;
    }

    protected abstract JPanel creaPannelloComandiHeader();

    /**
     * Utility method per creare bottoni uniformi nel pannello.
     * @param text Testo da visualizzare nel bottone
     * @param action Azione da eseguire al click (se null non viene aggiunta)
     * @return {@link JButton} formattato
     */
    protected JButton createStyledButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        if (action != null) {
            button.addActionListener(action);
        }
        return button;
    }

    /**
     * Costruisce un pannello a scorrimento contenente la griglia delle fotografie.
     * Utilizza {@link WrapLayout} per garantire che il layout sia responsivo
     * rispetto alla dimensione della finestra.
     * @param fotografie Lista delle fotografie da disporre nella griglia.
     * @param onImageClick Interfaccia funzionale per gestire l'evento di selezione immagine.
     * @return {@link JScrollPane} che contiene il pannello {@code imagePanel} con barra di scorrimento.
     */
    private JScrollPane creaGalleriaImmagini(ArrayList<Fotografia> fotografie, IntConsumer onImageClick) {
        JPanel imagePanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 5, 5));

        for (int i = 0; i < fotografie.size(); i++) {
            int index = i;
            JButton button = creaImmagine(fotografie.get(i));
            button.addActionListener(_ -> onImageClick.accept(index));
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
