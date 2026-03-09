package GUI;

import java.awt.*;

/**
 * Serve per gestire il posizionamento dei label nella GUI ed evitare che gli oggetti si dispongono al di fuori della viewport
 */

public class WrapLayout extends FlowLayout {

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override //dimensione ideale del layout
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override //dimensione minima del layout
    public Dimension minimumLayoutSize(Container target) {
        return layoutSize(target, false);
    }

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

