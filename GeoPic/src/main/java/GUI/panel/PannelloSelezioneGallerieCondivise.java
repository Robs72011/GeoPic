package GUI.panel;

import Controller.Controller;
import GUI.dialog.DialogAggiungiGalleriaCondivisa;
import GUI.utility.CardFactory;
import GUI.utility.WrapLayout;
import Model.GalleriaCondivisa;
import Model.Utente;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Tab che visualizza le gallerie condivise dell'utente loggato tramite card a griglia.
 */
public class PannelloSelezioneGallerieCondivise extends JPanel {
    private final Controller controller;
    private final Consumer<GalleriaCondivisa> onGalleriaSelected;

    public PannelloSelezioneGallerieCondivise(Controller controller, Consumer<GalleriaCondivisa> onGalleriaSelected) {
        this.controller = controller;
        this.onGalleriaSelected = onGalleriaSelected;
        refresh();
    }

    public void refresh() {
        removeAll();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Gallerie Condivise");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JButton btnNuovaGalleria = new JButton("+ Nuova galleria condivisa");
        btnNuovaGalleria.addActionListener(_ -> apriDialogAggiungiGalleriaCondivisa());

        header.add(title, BorderLayout.WEST);
        header.add(btnNuovaGalleria, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 15, 15));

        List<GalleriaCondivisa> gallerieCondivise = controller.getGallerieCondiviseUtenteLoggato();
        for (GalleriaCondivisa galleria : gallerieCondivise) {
            gridPanel.add(createSharedGalleryCard(galleria));
        }

        if (gallerieCondivise.isEmpty()) {
            JLabel emptyLabel = new JLabel("Nessuna galleria condivisa disponibile.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            add(emptyLabel, BorderLayout.CENTER);
        } else {
            JScrollPane scrollPane = new JScrollPane(gridPanel);
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            add(scrollPane, BorderLayout.CENTER);
        }

        revalidate();
        repaint();
    }

    private JPanel createSharedGalleryCard(GalleriaCondivisa galleriaCondivisa) {
        String partecipantiText = formatPartecipanti(galleriaCondivisa.getPartecipanti());

        String titleHtml = "<html><div style='text-align: center;'>"
            + "<b>" + escapeHtml(galleriaCondivisa.getNomeGalleria()) + "</b>"
            + "</div></html>";

        String infoHtml = "<html><div style='text-align: center;'>"
            + "ID: " + galleriaCondivisa.getIdGalleria() + "<br/>"
            + "Con: " + escapeHtml(partecipantiText)
            + "</div></html>";

        return CardFactory.createCard(
            titleHtml,
            new Font("Arial", Font.BOLD, 18),
            infoHtml,
            new Font("Arial", Font.BOLD, 12),
            () -> {
                if (onGalleriaSelected == null) {
                JOptionPane.showMessageDialog(
                    PannelloSelezioneGallerieCondivise.this,
                    "Navigazione non configurata: impossibile aprire la galleria.",
                    "Azione non disponibile",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
                }
                onGalleriaSelected.accept(galleriaCondivisa);
            }
        );
    }

    private String formatPartecipanti(ArrayList<Utente> partecipanti) {
        if (partecipanti == null || partecipanti.isEmpty()) {
            return "Nessun partecipante";
        }

        return partecipanti.stream()
                .map(Utente::getUsername)
                .collect(Collectors.joining(", "));
    }

    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }

        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private void apriDialogAggiungiGalleriaCondivisa() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        Frame parentFrame = owner instanceof Frame ? (Frame) owner : null;

        DialogAggiungiGalleriaCondivisa dialog = new DialogAggiungiGalleriaCondivisa(parentFrame, controller);
        dialog.setVisible(true);

        refresh();
    }
}
