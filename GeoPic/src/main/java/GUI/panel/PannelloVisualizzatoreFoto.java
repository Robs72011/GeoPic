package GUI.panel;

import Controller.Controller;
import Model.Fotografia;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Pannello dedicato alla visualizzazione dettagliata delle informazioni relative a una {@link Fotografia}.
 * Questa classe permette di navigare tra le fotografie di una lista, visualizzandone i metadati
 * (come ID, dispositivo, data e luogo) formattati in HTML. Supporta inoltre operazioni
 * di gestione, come la possibilità di rendere privata una foto selezionata.
 */
public class PannelloVisualizzatoreFoto extends JPanel {
    protected List<Fotografia> fotografie;
    protected int indiceCorrente = 0;
    protected final JLabel imageLabel = new JLabel("", SwingConstants.CENTER);
    protected final JPanel pannelloBottoni;
    private final JButton btnPrivatizza;
    private final Controller controller;


    /**
     * Costruisce il pannello di visualizzazione dettagliata.
     * @param foto La lista di oggetti {@link Fotografia} da mostrare nel dettaglio.
     * @param onBackClick Runnable eseguito al clic del tasto "Indietro", solitamente utilizzato
     */
    public PannelloVisualizzatoreFoto(List<Fotografia> foto,
                                      Runnable onBackClick,
                                      Controller controller) {
        this.fotografie = foto != null ? new ArrayList<>(foto) : List.of();
        this.controller = controller;
        
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        imageLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        this.add(new JScrollPane(imageLabel), BorderLayout.CENTER);

        this.btnPrivatizza = new JButton();
        this.pannelloBottoni = creaPannelloBottoni(onBackClick);
        this.add(this.pannelloBottoni, BorderLayout.SOUTH);
        
        // Inizializza la view sul primo elemento se disponibile
        if (!this.fotografie.isEmpty()) {
            mostraMetadati(0);
        }
    }

    /**
     * Crea e configura il pannello contenente i pulsanti di navigazione e gestione della risorsa.
     * @param onBackClick L'evento di ritorno alla vista precedente. (Per il bottone "Indietro")
     * @return {@link JPanel} contenente i bottoni di controllo.
     */
    private JPanel creaPannelloBottoni(Runnable onBackClick) {
        JButton btnIndietro = new JButton("⤬ Indietro");
        JButton btnPrecedente = new JButton("<< Precedente");
        JButton btnSuccessivo = new JButton("Successivo >>");

        btnPrecedente.addActionListener(_ -> {
            if (fotografie == null || fotografie.isEmpty()) {
                return;
            }
            mostraMetadati((indiceCorrente - 1 + fotografie.size()) % fotografie.size());
        });
        btnSuccessivo.addActionListener(_ -> {
            if (fotografie == null || fotografie.isEmpty()) {
                return;
            }
            mostraMetadati((indiceCorrente + 1) % fotografie.size());
        });
        btnIndietro.addActionListener(_ -> onBackClick.run());

        btnPrivatizza.addActionListener(_ -> toggleVisibilitaFoto());

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
        this.fotografie = foto != null ? new ArrayList<>(foto) : List.of();
        mostraMetadati(0);
    }

    private void toggleVisibilitaFoto() {
        if (fotografie == null || fotografie.isEmpty()) {
            return;
        }

        Fotografia fotoCorrente = fotografie.get(indiceCorrente);
        if (fotoCorrente == null || fotoCorrente.getIdFoto() == null) {
            return;
        }

        if (!controller.utenteLoggatoPuoGestireVisibilitaFoto(fotoCorrente.getIdFoto())) {
            return;
        }

        boolean fotoPubblica = fotoCorrente.isVisibile();

        String messaggio = fotoPubblica
                ? "Sei sicuro di voler rendere la foto privata?"
                : "Sei sicuro di voler rendere la foto pubblica?";
        int scelta = JOptionPane.showConfirmDialog(
            this,
                messaggio,
                "Conferma",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (scelta != JOptionPane.YES_OPTION) {
            return;
        }

        boolean aggiornamentoOk = controller.aggiornaVisibilitaFotografia(fotoCorrente.getIdFoto(), !fotoPubblica);
        if (!aggiornamentoOk) {
            JOptionPane.showMessageDialog(
                    this,
                    "Non e' stato possibile aggiornare la visibilita' della foto.",
                    "Operazione non completata",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        mostraMetadati(indiceCorrente);
    }

    private void aggiornaBottoneVisibilita(Fotografia foto) {
        if (foto == null) {
            btnPrivatizza.setText("\uD83D\uDD12 Gestione Visibilita");
            btnPrivatizza.setEnabled(false);
            btnPrivatizza.setVisible(false);
            return;
        }

        boolean utentePuoGestire = foto.getIdFoto() != null
            && controller.utenteLoggatoPuoGestireVisibilitaFoto(foto.getIdFoto());
        btnPrivatizza.setVisible(utentePuoGestire);
        btnPrivatizza.setEnabled(utentePuoGestire);
        if (!utentePuoGestire) {
            return;
        }

        if (foto.isVisibile()) {
            btnPrivatizza.setText("\uD83D\uDD12 Rendi Privata");
        } else {
            btnPrivatizza.setText("\uD83D\uDD13 Rendi Pubblica");
        }
    }

    /**
     * Aggiorna il contenuto della {@code imageLabel} visualizzando i dettagli
     * della fotografia situata all'indice specificato.
     * @param index L'indice della fotografia nella lista {@code fotografie}.
     */
    public void mostraMetadati(int index) {
        if (fotografie == null || fotografie.isEmpty()) {
            imageLabel.setText("Nessuna fotografia da mostrare.");
            return;
        }

        if (index < 0 || index >= fotografie.size()) {
            index = Math.max(0, Math.min(index, fotografie.size() - 1));
        }

        indiceCorrente = index;
        Fotografia foto = fotografie.get(indiceCorrente);
        aggiornaBottoneVisibilita(foto);

        // MODIFICA: Costruzione di una stringa HTML per visualizzare i metadati.
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='text-align: left; padding: 20px;'>");
        sb.append("<h1>Dettagli Fotografia</h1>");
        sb.append("<p><b>ID Foto:</b> ").append(foto.getIdFoto()).append("</p>");
        sb.append("<p><b>Autore:</b> ").append(foto.getAutore().getUsername()).append("</p>");
        sb.append("<p><b>Visibilità:</b> ").append(foto.isVisibile() ? "Pubblica" : "Privata").append("</p>");
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

        imageLabel.setText(sb.toString());
    }
}


