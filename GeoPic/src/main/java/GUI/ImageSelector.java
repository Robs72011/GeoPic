package GUI;

import Controller.Controller;
import Model.Fotografia;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ImageSelector extends JPanel {
    protected List<Fotografia> fotografie = List.of();
    protected int indiceCorrente = 0;
    protected final JLabel metadataLabel = new JLabel("", SwingConstants.CENTER);
    protected final JPanel footer;
    private final Controller controller;


    /**
     * Pannello per la visualizzazione di immagini. Con gestione dinamica della visualizzazione del contenuto, utile
     * se si vuole accedere con diversi utenti e aggiornare le immagini disponibili.
     *
     * @param onBackClick azione da eseguire al clic su "Indietro"
     */
    public ImageSelector(Runnable onBackClick, Controller controller) {
        this.controller = controller;
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        metadataLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        this.add(new JScrollPane(metadataLabel), BorderLayout.CENTER);

        // Pannello footer
        this.footer = creaPannelloBottoni(onBackClick);
        this.add(this.footer, BorderLayout.SOUTH);
    }

    /**
     * Imposta il contenuto da visualizzare.
     *
     * @param foto la lista di oggetti Fotografia.
     */

    public void setContent(List<Fotografia> foto) {
        this.fotografie = foto != null ? foto : List.of();
        // MODIFICA: Cambiato il nome del metodo per chiarezza
        mostraMetadati(0);
    }


    /**
     * Crea un pannello bottoni per scorrere gli elementi.
     *
     * @param onBackClick l'evento da ritornare a {@link GalleryPanelContainer}
     *                    per uscire dalla visone in dettaglio
     * @return pannello bottoni
     * @see GalleryPanel
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
     * Mostra i metadati della fotografia all'indice specificato.
     * @param index l'indice della fotografia nella lista.
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
        // Aggiungere altro (es. soggetti)

        sb.append("</body></html>");

        metadataLabel.setText(sb.toString());
    }
}


