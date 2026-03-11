package GUI;

import Model.Fotografia;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Pannello dedicato alla visualizzazione dettagliata delle informazioni relative a una {@link Fotografia}.
 * Questa classe permette di navigare tra le fotografie di una lista, visualizzandone i metadati
 * (come ID, dispositivo, data e luogo) formattati in HTML. Supporta inoltre operazioni
 * di gestione, come la possibilità di rendere privata una foto selezionata.
 */
public class ImageSelector extends JPanel {
    protected List<Fotografia> fotografie;
    protected int indiceCorrente = 0;
    protected final JLabel metadataLabel = new JLabel("", SwingConstants.CENTER);
    protected final JPanel footer;


    /**
     * Costruisce il pannello di visualizzazione dettagliata.
     * @param foto La lista di oggetti {@link Fotografia} da mostrare nel dettaglio.
     * @param onBackClick Runnable eseguito al clic del tasto "Indietro", solitamente utilizzato
     * per gestire la transizione tra le card del {@link GalleryPanelContainer}.
     */
    public ImageSelector(List<Fotografia> foto, Runnable onBackClick) {
        this.fotografie = foto != null ? foto : List.of();
        
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        metadataLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        this.add(new JScrollPane(metadataLabel), BorderLayout.CENTER);

        // Pannello footer
        this.footer = creaPannelloBottoni(onBackClick);
        this.add(this.footer, BorderLayout.SOUTH);
        
        // Inizializza la view sul primo elemento se disponibile
        if (!this.fotografie.isEmpty()) {
            mostraMetadati(0);
        }
    }

    /**
     * Crea e configura il pannello contenente i pulsanti di navigazione e gestione della risorsa.
     * @param onBackClick L'evento di ritorno alla vista precedente
     *                    (In GalleryPanelContainer sarebbe GRID del cardlayout). (Per il bottone "Indietro")
     * @return {@link JPanel} contenente i bottoni di controllo.
     */
    private JPanel creaPannelloBottoni(Runnable onBackClick) {
        JButton btnIndietro = new JButton("⤬ Indietro");
        JButton btnPrecedente = new JButton("<< Precedente");
        JButton btnSuccessivo = new JButton("Successivo >>");
        JButton btnPrivatizza = new JButton("\uD83D\uDD12 Rendi Privata");

        btnPrecedente.addActionListener(_ -> mostraMetadati((indiceCorrente - 1 + fotografie.size()) % fotografie.size()));
        btnSuccessivo.addActionListener(_ -> mostraMetadati((indiceCorrente + 1) % fotografie.size()));
        btnIndietro.addActionListener(_ -> onBackClick.run());

        btnPrivatizza.addActionListener(_ -> {
            int scelta = JOptionPane.showConfirmDialog(
                    null,
                    "Sei sicuro di voler rendere la foto privata?",
                    "Conferma",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (scelta == JOptionPane.YES_OPTION) {
                // Qui andrà la logica per chiamare un metodo del controller, ad esempio:
                // controller.setFotografiaPrivata(fotoCorrente.getId());
                JOptionPane.showMessageDialog(this, "Operazione da implementare.");
            }
        });

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonsPanel.add(btnPrecedente);
        buttonsPanel.add(btnSuccessivo);
        buttonsPanel.add(btnIndietro);
        buttonsPanel.add(btnPrivatizza);
        return buttonsPanel;
    }

    /**
     * Aggiorna la lista delle fotografie disponibili e posiziona la vista sul primo elemento.
     * Necessario per componenti dinamici come SlideshowSelector che ricaricano i dati a runtime.
     * @param foto La nuova lista di oggetti {@link Fotografia} da mostrare.
     */
    public void setContent(List<Fotografia> foto) {
        this.fotografie = foto != null ? foto : List.of();
        mostraMetadati(0);
    }

    /**
     * Aggiorna il contenuto della {@code metadataLabel} visualizzando i dettagli
     * della fotografia situata all'indice specificato.
     * @param index L'indice della fotografia nella lista {@code fotografie}.
     */
    public void mostraMetadati(int index) {
        if (fotografie == null || fotografie.isEmpty()) {
            metadataLabel.setText("Nessuna fotografia da mostrare.");
            return;
        }

        indiceCorrente = index;
        Fotografia foto = fotografie.get(indiceCorrente);

        // MODIFICA: Costruzione di una stringa HTML per visualizzare i metadati.
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='text-align: left; padding: 20px;'>");
        sb.append("<h1>Dettagli Fotografia</h1>");
        sb.append("<p><b>ID Foto:</b> ").append(foto.getIdFoto()).append("</p>");
        sb.append("<p><b>Dispositivo:</b> ").append(foto.getDispositivo()).append("</p>");
        sb.append("<p><b>Data Scatto:</b> ").append(foto.getDataDiScatto()).append("</p>");
        sb.append("<hr>");

        if (foto.getLuogo() != null) {
            sb.append("<h2>Dettagli Luogo</h2>");
            sb.append("<p><b>Nome Luogo:</b> ").append(foto.getLuogo().getNomeMnemonico()).append("</p>");
            sb.append("<p><b>Coordinate:</b> ").append(foto.getLuogo().getCoordinate()).append("</p>");
        } else {
            sb.append("<p>Nessun luogo associato.</p>");
        }
        sb.append("<hr>");
        
        sb.append("<h2>Soggetti</h2>");
        if (foto.getSoggetti() != null && !foto.getSoggetti().isEmpty()) {
            sb.append("<ul>");
            for (Model.Soggetto s : foto.getSoggetti()) {
                sb.append("<li>").append(s.getNomeSoggetto());
                if (s.getCategoria() != null && !s.getCategoria().isEmpty()) {
                    sb.append(" (").append(s.getCategoria()).append(")");
                }
                sb.append("</li>");
            }
            sb.append("</ul>");
        } else {
            sb.append("<p>Nessun soggetto presente in questa foto.</p>");
        }

        sb.append("</body></html>");

        metadataLabel.setText(sb.toString());
    }
}


