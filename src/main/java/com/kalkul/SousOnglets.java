package com.kalkul;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 * Sous-onglets d'un onglet : une page par famille de calculs, qui défile quand elle dépasse la
 * fenêtre au lieu d'être coupée.
 */
final class SousOnglets {

    /** Pas de défilement à la molette, en pixels : celui de Swing par défaut est trop lent. */
    static final int PAS_DEFILEMENT = 16;

    private SousOnglets() {
    }

    static JTabbedPane creer() {
        return new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    static void ajouter(JTabbedPane pOnglets, String pTitre, JComponent pPage) {
        // Page calée en haut : sans cela, BoxLayout étire les sections sur toute la hauteur
        JPanel vHaut = new JPanel(new BorderLayout());
        vHaut.add(pPage, BorderLayout.NORTH);
        JScrollPane vDefilement = new JScrollPane(vHaut);
        vDefilement.setBorder(BorderFactory.createEmptyBorder());
        vDefilement.getVerticalScrollBar().setUnitIncrement(PAS_DEFILEMENT);
        vDefilement.getHorizontalScrollBar().setUnitIncrement(PAS_DEFILEMENT);
        pOnglets.addTab(pTitre, vDefilement);
    }
}
