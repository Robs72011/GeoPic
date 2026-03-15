package GUI.dialog;

import Controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Dialog modale per la creazione di una nuova galleria condivisa.
 */
public class DialogAggiungiGalleriaCondivisa extends DialogAggiungi {

    private final JTextField campoNomeGalleria = new JTextField(28);
    private final JList<String> listaPartecipanti;

    public DialogAggiungiGalleriaCondivisa(Frame parentFrame, Controller controller) {
        super(parentFrame, "Nuova Galleria Condivisa", controller);

        DefaultListModel<String> modelPartecipanti = new DefaultListModel<>();
        ArrayList<String> usernames = controller.getUsernamesInMemory();
        String utenteLoggato = controller.getLoggedInUtente() != null
                ? controller.getLoggedInUtente().getUsername()
                : null;

        for (String username : usernames) {
            if (username == null) {
                continue;
            }
            if (utenteLoggato != null && utenteLoggato.equals(username)) {
                continue;
            }
            modelPartecipanti.addElement(username);
        }

        listaPartecipanti = new JList<>(modelPartecipanti);
        listaPartecipanti.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaPartecipanti.setVisibleRowCount(8);

        JPanel mainPanel = creaMainPanel();
        mainPanel.add(creaPannelloForm(), BorderLayout.CENTER);

        montaContenutoConBottoniSalva(mainPanel, this::salvaGalleriaCondivisa);
        configuraDimensioni(560, 420, 500, 360, true);
    }

    private JPanel creaPannelloForm() {
        JPanel form = new JPanel(new BorderLayout(8, 8));

        JPanel rigaNome = new JPanel(new BorderLayout(8, 0));
        rigaNome.add(new JLabel("Nome galleria:"), BorderLayout.WEST);
        rigaNome.add(campoNomeGalleria, BorderLayout.CENTER);

        JPanel bloccoPartecipanti = new JPanel(new BorderLayout(6, 6));
        bloccoPartecipanti.add(new JLabel("Partecipanti (opzionale):"), BorderLayout.NORTH);
        bloccoPartecipanti.add(new JScrollPane(listaPartecipanti), BorderLayout.CENTER);

        JLabel help = new JLabel("Suggerimento: tieni premuto CTRL per selezione multipla.");
        help.setFont(new Font("Arial", Font.PLAIN, 12));
        help.setForeground(Color.DARK_GRAY);

        form.add(rigaNome, BorderLayout.NORTH);
        form.add(bloccoPartecipanti, BorderLayout.CENTER);
        form.add(help, BorderLayout.SOUTH);

        return form;
    }

    private void salvaGalleriaCondivisa() {
        String nomeGalleria = trimToEmpty(campoNomeGalleria.getText());

        if (isBlank(nomeGalleria)) {
            mostraErrore("Inserisci il nome della galleria condivisa.");
            return;
        }

        java.util.List<String> selezionati = listaPartecipanti.getSelectedValuesList();
        String[] partecipanti = selezionati.isEmpty() ? null : selezionati.toArray(new String[0]);

        controller.creazioneNuovaGalleriaCondivisa(nomeGalleria, partecipanti);
        mostraSuccessoEChiudi("Galleria condivisa creata con successo.", "Operazione completata");
    }
}
