package GUI.panel;

import Controller.Controller;
import GUI.WrapLayout;
import GUI.frame.FinestraGalleriaCondivisa;
import Model.GalleriaCondivisa;
import Model.Utente;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tab che visualizza le gallerie condivise dell'utente loggato tramite card a griglia.
 */
public class PannelloSelezioneGallerieCondivise extends JPanel {

    public PannelloSelezioneGallerieCondivise(Controller controller) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Gallerie Condivise");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 15, 15));

        List<GalleriaCondivisa> gallerieCondivise = controller.getGallerieCondiviseUtenteLoggato();
        for (GalleriaCondivisa galleria : gallerieCondivise) {
            gridPanel.add(createSharedGalleryCard(controller, galleria));
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
    }

    private JPanel createSharedGalleryCard(Controller controller, GalleriaCondivisa galleriaCondivisa) {
        String partecipantiText = formatPartecipanti(galleriaCondivisa.getPartecipanti());

        JPanel background = new JPanel(new BorderLayout());
        background.setBackground(new Color(220, 220, 220));

        // Nome della galleria al centro
        JLabel titleLabel = new JLabel("<html><div style='text-align: center;'>"
                + "<b>" + escapeHtml(galleriaCondivisa.getNomeGalleria()) + "</b>"
                + "</div></html>", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        background.add(titleLabel, BorderLayout.CENTER);

        // ID e Partecipanti in basso, grigio scuro con testo bianco (come i video)
        String infoHtml = "<html><div style='text-align: center;'>"
                + "ID: " + galleriaCondivisa.getIdGalleria() + "<br/>"
                + "Con: " + escapeHtml(partecipantiText)
                + "</div></html>";

        JLabel infoLabel = new JLabel(infoHtml, SwingConstants.CENTER);
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 12));
        infoLabel.setOpaque(true);
        infoLabel.setBackground(new Color(100, 100, 100));
        background.add(infoLabel, BorderLayout.SOUTH);

        // Pannello container principale della card
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.add(background, BorderLayout.CENTER);
        cardPanel.setPreferredSize(new Dimension(240, 135)); // Stesse dimensioni del carosello video (16:9)
        cardPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cardPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // Evento di clic sul pannello
        cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                FinestraGalleriaCondivisa frame = new FinestraGalleriaCondivisa(controller, galleriaCondivisa);
                frame.setLocationRelativeTo(SwingUtilities.getWindowAncestor(cardPanel));
                frame.setVisible(true);
            }
        });

        return cardPanel;
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
}
