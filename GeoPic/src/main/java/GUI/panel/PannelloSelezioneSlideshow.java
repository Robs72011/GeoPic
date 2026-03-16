package GUI.panel;

import GUI.utility.CardFactory;
import Model.Video;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Pannello che implementa un carosello scorrevole orizzontalmente per la visualizzazione di slideshow video.
 * Ogni slideshow è rappresentato come una "card" grafica cliccabile che, all'interazione dell'utente,
 * attiva una {@link Consumer} callback definita per la gestione del passaggio alla vista dedicata.
 */
public class PannelloSelezioneSlideshow extends JPanel {

    /**
     * Crea un pannello carosello contenente le card per ogni slideshow presente nella lista.
     * @param slideshows La lista degli oggetti {@link Video} da visualizzare.
     * @param onClick Una callback che riceve il video selezionato per gestirne la riproduzione
     */
    public PannelloSelezioneSlideshow(List<Video> slideshows, Consumer<Video> onClick) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 15));

        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        container.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        for (Video slideshow : slideshows) {
            JPanel card = creaCard(slideshow, onClick);
            container.add(card);
        }

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Crea e configura un singolo componente grafico (card) per uno slideshow.
     * Per gestire il clic dell'utente.
     * @param slideshow Il video associato alla card.
     * @param onClick La callback da eseguire al clic.
     * @return {@link JPanel} configurato come card interattiva.
     */
    private JPanel creaCard(Video slideshow, Consumer<Video> onClick) {
        String titolo = slideshow.getTitolo() != null ? slideshow.getTitolo() : "Senza titolo";
        String titleHtml = "<html><div style='text-align: center;'><b>" + titolo + "</b></div></html>";
        String footerText = "Video ID: " + slideshow.getIdVideo();

        return CardFactory.createCard(
                titleHtml,
                new Font("Arial", Font.BOLD, 18),
                footerText,
                new Font("Arial", Font.BOLD, 14),
                () -> onClick.accept(slideshow)
        );
    }
}
