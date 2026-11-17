package com.kalkul;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * Onglet Graphique : courbes avec formules, matrices et systèmes, régression.
 */
class PanneauGraphique extends JPanel {
    private static final long serialVersionUID = 1L;

    PanneauGraphique(CarnetFormules pCarnet) {
        super(new BorderLayout());
        JTabbedPane vTabs = new JTabbedPane();
        vTabs.addTab("Courbes", new PanneauCourbe(pCarnet));
        vTabs.addTab("Matrices et systèmes", new PanneauMatrices());
        vTabs.addTab("Régression", new PanneauRegression());
        add(vTabs, BorderLayout.CENTER);
    }
}
