package GUI.panel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Pannello separato per la gestione dinamica dei soggetti. (Usato nella dialog AggiungiFotoDialog)
 * Permette di aggiungere N righe, ognuna con la selezione della categoria e dell'entità.
 */
public class PannelloAggiungiSoggetti extends JPanel {
    private final JPanel containerSoggetti;
    private final List<SoggettoRowPanel> righeSoggetti;
    private final ArrayList<String> nomiUtentiDisponibili;

    public PannelloAggiungiSoggetti(ArrayList<String> nomiUtentiDisponibili) {
        this.nomiUtentiDisponibili = nomiUtentiDisponibili;
        this.righeSoggetti = new ArrayList<>();

        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Gestione Soggetti"));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel lblIstruzioni = new JLabel("Aggiungi i soggetti presenti nella foto:");
        JButton btnAddSoggetto = new JButton("+");
        btnAddSoggetto.setToolTipText("Aggiungi un nuovo soggetto");
        btnAddSoggetto.setMargin(new Insets(2, 6, 2, 6));

        headerPanel.add(lblIstruzioni, BorderLayout.WEST);
        headerPanel.add(btnAddSoggetto, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        containerSoggetti = new JPanel();
        containerSoggetti.setLayout(new BoxLayout(containerSoggetti, BoxLayout.Y_AXIS));

        // Aggiungo la prima riga di default
        aggiungiRigaSoggetto();

        JScrollPane scrollSoggetti = new JScrollPane(containerSoggetti);
        scrollSoggetti.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollSoggetti.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollSoggetti.setPreferredSize(new Dimension(500, 150));
        add(scrollSoggetti, BorderLayout.CENTER);

        btnAddSoggetto.addActionListener(_ -> {
            aggiungiRigaSoggetto();
            containerSoggetti.revalidate();
            
            // Faccio scorrere verso il basso
            SwingUtilities.invokeLater(() -> {
                JScrollBar vertical = scrollSoggetti.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
            });
        });
    }

    private void aggiungiRigaSoggetto() {
        SoggettoRowPanel riga = new SoggettoRowPanel(nomiUtentiDisponibili);
        righeSoggetti.add(riga);
        containerSoggetti.add(riga);
    }

    /**
     * Estrae i soggetti selezionati riempiendo le liste fornite.
     * Ritorna null se va tutto bene, o un messaggio d'errore.
     */
    public String popolaSoggettiSelezionati(ArrayList<String> nomi, ArrayList<String> categorie) {
        for (SoggettoRowPanel riga : righeSoggetti) {
            String cat = riga.getCategoria();
            if ("Nessuno".equals(cat)) continue;

            if ("Utente".equals(cat)) {
                String u = riga.getUtenteSelezionato();
                if (u.isEmpty()) return "Hai scelto la categoria 'Utente' ma non hai selezionato nessuno dalla lista.";
                nomi.add(u);
                categorie.add("Utente");
            } else {
                String n = riga.getNome();
                if (n.isEmpty()) return "Hai selezionato una categoria ('" + cat + "') ma non hai inserito il nome.";
                nomi.add(n);
                categorie.add(cat);
            }
        }
        return null;
    }

    /**
     * Classe interna per rappresentare graficamente una singola riga.
     * I campi sono più piccoli e allineati verso destra.
     */
    public class SoggettoRowPanel extends JPanel {
        private final JComboBox<String> cbCategoria;
        private final JTextField txtNome;
        private final JComboBox<String> cbUtente;

        public SoggettoRowPanel(ArrayList<String> utenti) {
            // Allineamento a destra come richiesto
            setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

            cbCategoria = new JComboBox<>(new String[]{"Nessuno", "Persona", "Animale", "Oggetto", "Paesaggio", "Utente"});
            cbCategoria.setPreferredSize(new Dimension(85, 22)); // Più piccolo (in precedenza 90x25)

            txtNome = new JTextField();
            txtNome.setPreferredSize(new Dimension(90, 22)); // Più piccolo (in precedenza 100x25)

            cbUtente = new JComboBox<>();
            cbUtente.addItem("Seleziona utente");
            for (String utente : utenti) {
                cbUtente.addItem(utente);
            }
            cbUtente.setPreferredSize(new Dimension(120, 22));

            cbCategoria.addActionListener(_ -> aggiornaStatoCampi());

            JButton btnRimuovi = new JButton("x");
            btnRimuovi.setToolTipText("Rimuovi riga");
            btnRimuovi.setMargin(new Insets(2, 5, 2, 5));
            btnRimuovi.setForeground(Color.RED);
            btnRimuovi.addActionListener(_ -> {
                containerSoggetti.remove(this);
                righeSoggetti.remove(this);
                containerSoggetti.revalidate();
                containerSoggetti.repaint();
            });

            add(cbCategoria);
            add(txtNome);
            add(cbUtente);
            add(btnRimuovi);

            cbCategoria.setSelectedItem("Nessuno"); // Innesca aggiornaStatoCampi
        }

        private void aggiornaStatoCampi() {
            String cat = (String) cbCategoria.getSelectedItem();
            if ("Utente".equals(cat)) {
                txtNome.setEnabled(false);
                txtNome.setText("");
                cbUtente.setEnabled(true);
            } else if ("Nessuno".equals(cat)) {
                txtNome.setEnabled(false);
                txtNome.setText("");
                cbUtente.setEnabled(false);
                cbUtente.setSelectedIndex(0);
            } else {
                txtNome.setEnabled(true);
                cbUtente.setEnabled(false);
                cbUtente.setSelectedIndex(0);
            }
        }

        public String getCategoria() {
            return (String) cbCategoria.getSelectedItem();
        }

        public String getNome() {
            return txtNome.getText().trim();
        }

        public String getUtenteSelezionato() {
            if (cbUtente.getSelectedIndex() <= 0) {
                return "";
            }
            return (String) cbUtente.getSelectedItem();
        }
    }
}