package GUI.utility;

import java.awt.*;

/**
 * Un gestore di layout (LayoutManager) che estende {@link FlowLayout}.
 * A differenza del {@code FlowLayout} standard, il {@code WrapLayout} permette ai componenti
 * di disporsi su più righe (andando a capo) quando la larghezza del contenitore padre
 * diventa insufficiente per contenerli tutti sulla stessa linea.
 * È ideale per griglie di elementi (come immagini o card) in cui si desidera
 * un comportamento responsivo rispetto al ridimensionamento della finestra.
 */
public class WrapLayout extends FlowLayout {

    /**
     * Costruisce un nuovo WrapLayout.
     * @param align L'allineamento dei componenti (es. {@link FlowLayout#LEFT}).
     * @param hgap Lo spazio orizzontale tra i componenti.
     * @param vgap Lo spazio verticale tra i componenti.
     */
    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    /**
     * Calcola la dimensione preferita del layout in base ai componenti contenuti.
     * @param target Il contenitore {@link Container} da gestire.
     * @return La {@link Dimension} ideale calcolata per avvolgere i componenti.
     */
    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    /**
     * Calcola la dimensione minima del layout.
     * @param target Il contenitore {@link Container} da gestire.
     * @return La {@link Dimension} minima necessaria per il layout.
     */
    @Override
    public Dimension minimumLayoutSize(Container target) {
        return layoutSize(target, false);
    }

    /**
     * Metodo privato di supporto per il calcolo delle dimensioni del layout.
     * Iterando sui componenti, determina l'altezza totale necessaria in base alla
     * larghezza disponibile, gestendo i ritorni a capo.
     * @param target Il contenitore target.
     * @param preferred Indica se utilizzare le dimensioni preferite o minime.
     * @return Le dimensioni calcolate.
     */
    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int hgap = getHgap();
            int vgap = getVgap();
            int width = target.getWidth();

            if (width == 0) {
                width = Integer.MAX_VALUE;
            }

            Insets insets = target.getInsets();
            int maxWidth = width - (insets.left + insets.right + hgap * 2);
            int x = 0, y = insets.top + vgap, rowHeight = 0;

            Dimension dim = new Dimension(0, 0);

            for (Component m : target.getComponents()) {
                if (!m.isVisible()) continue;

                Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

                if (x + d.width > maxWidth) {
                    x = 0;
                    y += rowHeight + vgap;
                    rowHeight = 0;
                }

                x += d.width + hgap;
                rowHeight = Math.max(rowHeight, d.height);
                dim.width = Math.max(dim.width, x);
            }

            dim.height = y + rowHeight + vgap;
            return dim;
        }
    }
}
