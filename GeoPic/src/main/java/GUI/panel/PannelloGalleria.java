package GUI.panel;

import GUI.utility.WrapLayout;
import GUI.dialog.DialogAggiungiFotoGalleriaCondivisa;
import GUI.dialog.DialogAggiungiFoto;
import GUI.dialog.DialogAggiungiVideo;
import Model.Fotografia;
import Model.GalleriaCondivisa;
import Model.Video;
import Model.Soggetto;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class PannelloGalleria extends JPanel {

    private static final int IMAGE_SIZE = 160;

    private final Runnable onRefresh;
    private final List<Fotografia> fotografieMostrate;

    /**
     * Costruisce il pannello principale della galleria, organizzando header,
     * carosello degli slideshow e la griglia delle immagini.
     * @param fotografie Lista delle entità {@link Fotografia} da visualizzare.
     * @param onImageClick Callback che riceve l'indice della foto cliccata per gestire la navigazione.
     * @param slideshowList Lista delle entità {@link Video} (slideshow) disponibili.
     * @param onSlideshowClick Callback che gestisce il clic su un elemento del carosello.
     * @param controller Il {@link Controller.Controller} di riferimento per le operazioni utente.
     * @param onRefresh Callback per aggiornare la galleria dopo l'aggiunta di una foto.
     */
    public PannelloGalleria(List<Fotografia> fotografie,
                            IntConsumer onImageClick,
                            List<Video> slideshowList,
                            Consumer<Video> onSlideshowClick,
                            Controller.Controller controller,
                            Runnable onRefresh) {
        this(fotografie,
                onImageClick,
                slideshowList,
                onSlideshowClick,
                controller,
                onRefresh,
                null,
                null,
                true,
                true,
                null);
    }

    /**
     * Costruisce il pannello galleria consentendo la personalizzazione dell'header
     * e la visibilità del pulsante di aggiunta foto.
     */
    public PannelloGalleria(List<Fotografia> fotografie,
                            IntConsumer onImageClick,
                            List<Video> slideshowList,
                            Consumer<Video> onSlideshowClick,
                            Controller.Controller controller,
                            Runnable onRefresh,
                            String customTitle,
                            String customSubtitle,
                            boolean showAddPhotoButton,
                            boolean showSearchButtons,
                            Runnable onAddPhotoCustomAction) {
        this.onRefresh = onRefresh;
                    this.fotografieMostrate = fotografie != null ? new ArrayList<>(fotografie) : List.of();
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());

        String headerTitle = customTitle;
        String headerSubtitle = customSubtitle;
        if (headerTitle == null || headerSubtitle == null) {
            Model.Utente utenteCorrente = controller.getLoggedInUtente();
            String username = utenteCorrente != null ? utenteCorrente.getUsername() : "Sconosciuto";
            String idUtente = utenteCorrente != null ? String.valueOf(utenteCorrente.getIdUtente()) : "N/D";

            headerTitle = "Galleria Personale di " + username;
            headerSubtitle = "ID: " + idUtente;
        }

        topPanel.add(creaHeader(headerTitle,
                headerSubtitle,
                controller,
                showAddPhotoButton,
                showSearchButtons,
                onAddPhotoCustomAction), BorderLayout.NORTH);

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
     * @param controller Riferimento al controller per attivare i dialoghi.
     * @return {@link JPanel} configurato con layout BorderLayout.
     */
    private JPanel creaHeader(String headerTitle,
                              String headerSubtitle,
                              Controller.Controller controller,
                              boolean showAddPhotoButton,
                              boolean showSearchButtons,
                              Runnable onAddPhotoCustomAction) {
        // Pannello principale che contiene le info utente e il pannello comandi
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 30, 15));
        headerPanel.setBackground(new Color(230, 230, 250));

        // Pannello per visualizzare i dati dell'utente (allineato a sinistra)
        JPanel userInfoPanel = new JPanel(new GridLayout(2, 1));
        userInfoPanel.setOpaque(false);

        JLabel userLabel = new JLabel(headerTitle);
        userLabel.setFont(new Font("Arial", Font.BOLD, 30));

        JLabel idLabel = new JLabel(headerSubtitle);

        userInfoPanel.add(userLabel);
        userInfoPanel.add(idLabel);

        headerPanel.add(userInfoPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JButton menuAzioni = createStyledButton("Azioni", _ -> {});
        JPopupMenu popupAzioni = new JPopupMenu();

        if (showAddPhotoButton) {
            JMenuItem itemAggiungiFoto = new JMenuItem("Aggiungi Foto");
            itemAggiungiFoto.addActionListener(_ -> {
                if (onAddPhotoCustomAction != null) {
                    onAddPhotoCustomAction.run();
                } else {
                    apriDialogAggiungiFoto(controller);
                }
            });
            popupAzioni.add(itemAggiungiFoto);

            JMenuItem itemCreaVideo = new JMenuItem("Crea Video");
            itemCreaVideo.addActionListener(_ -> apriDialogCreaVideo(controller));
            popupAzioni.add(itemCreaVideo);

            popupAzioni.addSeparator();
        }

        JMenuItem itemTop3 = new JMenuItem("Top 3 Luoghi");
        itemTop3.addActionListener(_ ->
                JOptionPane.showMessageDialog(this,
                        "Funzionalità 'Top 3 Luoghi' da implementare!",
                        "Classifica",
                        JOptionPane.INFORMATION_MESSAGE));
        popupAzioni.add(itemTop3);

        menuAzioni.addActionListener(_ -> popupAzioni.show(menuAzioni, 0, menuAzioni.getHeight()));
        menuAzioni.setAlignmentX(Component.RIGHT_ALIGNMENT);
        rightPanel.add(menuAzioni);

        if (showSearchButtons) {
            rightPanel.add(Box.createVerticalStrut(8));
            PannelloRicercaFoto pannelloRicerca = new PannelloRicercaFoto(richiesta ->
                    eseguiRicercaFoto(richiesta.getTipo(), richiesta.getQuery()));
            pannelloRicerca.setMaximumSize(new Dimension(380, 36));
            pannelloRicerca.setAlignmentX(Component.RIGHT_ALIGNMENT);
            rightPanel.add(pannelloRicerca);
        }

        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.setOpaque(false);
        rightContainer.add(rightPanel, BorderLayout.NORTH);

        headerPanel.add(rightContainer, BorderLayout.EAST);
        return headerPanel;
    }

    /**
     * Utility method per creare bottoni uniformi nel pannello.
     * @param text Testo da visualizzare nel bottone
     * @param action Azione da eseguire al click (se null non viene aggiunta)
     * @return {@link JButton} formattato
     */
    private JButton createStyledButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        if (action != null) {
            button.addActionListener(action);
        }
        return button;
    }

    private void apriDialogAggiungiFoto(Controller.Controller controller) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        Frame owner = parentWindow instanceof Frame ? (Frame) parentWindow : null;

        DialogAggiungiFoto dialog = new DialogAggiungiFoto(owner, controller);
        dialog.setVisible(true);

        if (onRefresh != null) {
            onRefresh.run();
        }
    }

    private void apriDialogCreaVideo(Controller.Controller controller) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        Frame owner = parentWindow instanceof Frame ? (Frame) parentWindow : null;

        DialogAggiungiVideo dialog = new DialogAggiungiVideo(owner, controller);
        dialog.setVisible(true);

        if (onRefresh != null) {
            onRefresh.run();
        }
    }

    private void eseguiRicercaFoto(String tipoRicerca, String query) {
        String filtro = query != null ? query.trim().toLowerCase() : "";
        if (filtro.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Inserisci un testo da cercare.",
                    "Ricerca",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Fotografia> risultati = new ArrayList<>();

        for (Fotografia foto : fotografieMostrate) {
            if (foto == null) {
                continue;
            }

            if (PannelloRicercaFoto.TIPO_LUOGO.equalsIgnoreCase(tipoRicerca)) {
                if (foto.getLuogo() == null) {
                    continue;
                }
                String nomeLuogo = foto.getLuogo().getNomeMnemonico() != null
                        ? foto.getLuogo().getNomeMnemonico().toLowerCase()
                        : "";
                String coordinate = foto.getLuogo().getCoordinate() != null
                        ? foto.getLuogo().getCoordinate().toLowerCase()
                        : "";

                if (nomeLuogo.contains(filtro) || coordinate.contains(filtro)) {
                    risultati.add(foto);
                }
            } else {
                if (foto.getSoggetti() == null || foto.getSoggetti().isEmpty()) {
                    continue;
                }

                for (Soggetto soggetto : foto.getSoggetti()) {
                    String nome = soggetto.getNomeSoggetto() != null
                            ? soggetto.getNomeSoggetto().toLowerCase()
                            : "";
                    String categoria = soggetto.getCategoria() != null
                            ? soggetto.getCategoria().toLowerCase()
                            : "";
                    if (nome.contains(filtro) || categoria.contains(filtro)) {
                        risultati.add(foto);
                        break;
                    }
                }
            }
        }

        if (risultati.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nessun risultato trovato per: " + query,
                    "Ricerca",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Risultati trovati: ").append(risultati.size()).append("\n\n");

        int max = Math.min(20, risultati.size());
        for (int i = 0; i < max; i++) {
            Fotografia foto = risultati.get(i);
            sb.append("- ID ").append(foto.getIdFoto())
                    .append(" | Dispositivo: ")
                    .append(foto.getDispositivo() != null ? foto.getDispositivo() : "N/D")
                    .append("\n");
        }
        if (risultati.size() > max) {
            sb.append("\n...");
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(420, 260));

        JOptionPane.showMessageDialog(this, scroll, "Risultati Ricerca", JOptionPane.INFORMATION_MESSAGE);
    }

    public static Runnable createAddPhotoToSharedGalleryAction(JComponent source,
                                                                Controller.Controller controller,
                                                                GalleriaCondivisa galleriaCondivisa,
                                                                Runnable onRefresh) {
        return () -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(source);
            Frame owner = parentWindow instanceof Frame ? (Frame) parentWindow : null;

            DialogAggiungiFotoGalleriaCondivisa dialog = new DialogAggiungiFotoGalleriaCondivisa(owner, controller, galleriaCondivisa);
            dialog.setVisible(true);

            if (onRefresh != null) {
                onRefresh.run();
            }
        };
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
