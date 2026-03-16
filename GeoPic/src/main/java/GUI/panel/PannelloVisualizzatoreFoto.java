package GUI.panel;

import Controller.Controller;
import Model.Fotografia;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Pannello dedicato alla visualizzazione dettagliata delle informazioni relative a una {@link Fotografia}.
 * Questa classe permette di navigare tra le fotografie di una lista, visualizzandone i metadati
 * (come ID, dispositivo, data e luogo) formattati in HTML. Supporta inoltre operazioni
 * di gestione, come la possibilità di rendere privata una foto selezionata.
 */
public class PannelloVisualizzatoreFoto extends JPanel {
    protected ArrayList<Fotografia> fotografie;
    protected int indiceCorrente = 0;
    protected final JLabel imageLabel = new JLabel("", SwingConstants.CENTER);
    protected final JPanel pannelloBottoni;
    private final JPanel pannelloSud;
    private final JButton btnPrivatizza;
    protected final Controller controller;


    /**
     * Costruisce il pannello di visualizzazione dettagliata.
     * @param foto La lista di oggetti {@link Fotografia} da mostrare nel dettaglio.
     * @param onBackClick Runnable eseguito al clic del tasto "Indietro", solitamente utilizzato
     */
    public PannelloVisualizzatoreFoto(ArrayList<Fotografia> foto,
                                      Runnable onBackClick,
                                      Controller controller) {
        this.fotografie = foto != null ? new ArrayList<>(foto) : new ArrayList<>();
        this.controller = controller;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        imageLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        add(new JScrollPane(imageLabel), BorderLayout.CENTER);

        btnPrivatizza = new JButton();
        pannelloBottoni = creaPannelloBottoni(onBackClick);
        pannelloSud = new JPanel(new BorderLayout());
        pannelloSud.add(pannelloBottoni, BorderLayout.CENTER);
        add(pannelloSud, BorderLayout.SOUTH);

        if (!fotografie.isEmpty()) {
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

        btnPrecedente.addActionListener(_ -> naviga(-1));
        btnSuccessivo.addActionListener(_ -> naviga(1));
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
    public void setContent(ArrayList<Fotografia> foto) {
        this.fotografie = foto != null ? new ArrayList<>(foto) : new ArrayList<>();
        mostraMetadati(0);
    }

    protected void impostaContenutoSud(Component component) {
        pannelloSud.removeAll();
        if (component != null) {
            pannelloSud.add(component, BorderLayout.CENTER);
        }
        pannelloSud.revalidate();
        pannelloSud.repaint();
    }

    private boolean haFotografie() {
        return fotografie.isEmpty();
    }

    private void naviga(int delta) {
        if (haFotografie()) {
            return;
        }
        mostraMetadati((indiceCorrente + delta + fotografie.size()) % fotografie.size());
    }

    private void toggleVisibilitaFoto() {
        if (haFotografie()) {
            return;
        }

        Fotografia fotoCorrente = fotografie.get(indiceCorrente);
        if (fotoCorrente == null || fotoCorrente.getIdFoto() == null) {
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
                    "Non è stato possibile aggiornare la visibilità della foto.\nL'operazione non è consentita o si è verificato un errore.",
                    "Operazione non completata",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        mostraMetadati(indiceCorrente);
    }

    private void aggiornaBottoneVisibilita(Fotografia foto) {
        if (foto == null || controller.getLoggedInUtente() == null) {
            btnPrivatizza.setVisible(false);
            return;
        }

        boolean isOwner = controller.isUtenteLoggatoAutoreDi(foto);
        btnPrivatizza.setVisible(isOwner);

        if (isOwner) {
            if (foto.isVisibile()) {
                btnPrivatizza.setText("\uD83D\uDD12 Rendi Privata");
            } else {
                btnPrivatizza.setText("\uD83D\uDD13 Rendi Pubblica");
            }
        }
    }

    /**
     * Aggiorna il contenuto della {@code imageLabel} visualizzando i dettagli
     * della fotografia situata all'indice specificato.
     * @param index L'indice della fotografia nella lista {@code fotografie}.
     */
    public void mostraMetadati(int index) {
        if (haFotografie()) {
            imageLabel.setText("Nessuna fotografia da mostrare.");
            return;
        }

        if (index < 0 || index >= fotografie.size()) {
            index = Math.max(0, Math.min(index, fotografie.size() - 1));
        }

        indiceCorrente = index;
        Fotografia foto = fotografie.get(indiceCorrente);
        aggiornaBottoneVisibilita(foto);

        imageLabel.setText(controller.formattaDettagliFotografiaHtml(foto));
    }
}
