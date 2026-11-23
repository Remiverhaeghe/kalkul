package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import org.junit.jupiter.api.Test;

class SousOngletsTest {

    @Test
    void pageAddedInsideAScrollPaneAndKeptAtTheTop() {
        JTabbedPane vOnglets = SousOnglets.creer();
        JLabel vPage = new JLabel("page");
        SousOnglets.ajouter(vOnglets, "Titre", vPage);

        assertEquals(1, vOnglets.getTabCount());
        assertEquals("Titre", vOnglets.getTitleAt(0));
        JScrollPane vDefilement = assertInstanceOf(JScrollPane.class, vOnglets.getComponentAt(0));
        JPanel vHaut = (JPanel) vDefilement.getViewport().getView();
        assertEquals(BorderLayout.NORTH, ((BorderLayout) vHaut.getLayout()).getConstraints(vPage));
        assertEquals(SousOnglets.PAS_DEFILEMENT, vDefilement.getVerticalScrollBar().getUnitIncrement());
    }

    @Test
    void toolsAndSiteTabsSplitIntoScrollingSubTabs() {
        assertSubTabs(new PanneauOutils(new Devises()), "Statistiques", "Second degré", "Unités", "Proportions",
                "Devises", "Mémoires", "Réglages");
        assertSubTabs(new PanneauChantier(), "Surfaces et dalle", "Coupes", "Toiture",
                "Terrassement et mélanges");
    }

    private static void assertSubTabs(JPanel pPanneau, String... pTitres) {
        Component vCentre = ((BorderLayout) pPanneau.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        JTabbedPane vOnglets = assertInstanceOf(JTabbedPane.class, vCentre);
        assertEquals(pTitres.length, vOnglets.getTabCount());
        for (int vIndex = 0; vIndex < pTitres.length; vIndex++) {
            assertEquals(pTitres[vIndex], vOnglets.getTitleAt(vIndex));
            assertInstanceOf(JScrollPane.class, vOnglets.getComponentAt(vIndex));
        }
    }
}
