package GUI.utility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Utility per creare card cliccabili con stile coerente.
 */
public final class CardFactory {

    public static final int CARD_WIDTH = 240;
    public static final int CARD_HEIGHT = 135;

    private CardFactory() {
    }

    public static JPanel createCard(String titleHtml, Font titleFont,
                                    String footerHtmlOrText, Font footerFont,
                                    Runnable onClick) {
        JPanel background = new JPanel(new BorderLayout());
        background.setBackground(new Color(220, 220, 220));

        JLabel titleLabel = new JLabel(titleHtml, SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        background.add(titleLabel, BorderLayout.CENTER);

        JLabel footerLabel = new JLabel(footerHtmlOrText, SwingConstants.CENTER);
        footerLabel.setForeground(Color.WHITE);
        footerLabel.setFont(footerFont);
        footerLabel.setOpaque(true);
        footerLabel.setBackground(new Color(100, 100, 100));
        background.add(footerLabel, BorderLayout.SOUTH);

        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.add(background, BorderLayout.CENTER);
        cardPanel.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        cardPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cardPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        cardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) {
                    onClick.run();
                }
            }
        });

        return cardPanel;
    }
}