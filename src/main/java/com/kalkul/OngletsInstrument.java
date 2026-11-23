package com.kalkul;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 * Onglets à plat : pas de cadre ni de relief, le titre de l'onglet ouvert en clair, souligné de
 * moutarde, les autres en gris. Public parce que Swing le crée par son nom ({@code TabbedPaneUI}).
 */
public final class OngletsInstrument extends BasicTabbedPaneUI {
    private static final int SOULIGNE = 3;

    /**
     * Fabrique appelée par Swing pour chaque groupe d'onglets.
     */
    public static ComponentUI createUI(JComponent pComposant) {
        return new OngletsInstrument();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        tabInsets = new Insets(9, 14, 9, 14);
        selectedTabPadInsets = new Insets(0, 0, 0, 0);
        tabAreaInsets = new Insets(2, 8, 0, 8);
        contentBorderInsets = new Insets(1, 0, 0, 0);
        tabPane.setFont(Habillage.texte(Font.BOLD, 13));
    }

    @Override
    protected void paintTabBackground(Graphics pG, int pPlacement, int pIndex, int pX, int pY, int pW, int pH,
            boolean pChoisi) {
        pG.setColor(Habillage.FOND);
        pG.fillRect(pX, pY, pW, pH);
    }

    @Override
    protected void paintTabBorder(Graphics pG, int pPlacement, int pIndex, int pX, int pY, int pW, int pH,
            boolean pChoisi) {
        if (pChoisi) {
            pG.setColor(Habillage.MOUTARDE);
            pG.fillRect(pX + 8, pY + pH - SOULIGNE, pW - 16, SOULIGNE);
        }
    }

    @Override
    protected void paintText(Graphics pG, int pPlacement, Font pFont, FontMetrics pMetrics, int pIndex,
            String pTitre, Rectangle pZone, boolean pChoisi) {
        Graphics2D vG = (Graphics2D) pG;
        vG.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        vG.setFont(pFont);
        vG.setColor(pChoisi ? Habillage.TEXTE : Habillage.TEXTE_DOUX);
        vG.drawString(pTitre, pZone.x, pZone.y + pMetrics.getAscent());
    }

    @Override
    protected void paintFocusIndicator(Graphics pG, int pPlacement, Rectangle[] pZones, int pIndex,
            Rectangle pIcone, Rectangle pTexte, boolean pChoisi) {
        // Le soulignement montre déjà l'onglet ouvert
    }

    @Override
    protected void paintContentBorder(Graphics pG, int pPlacement, int pChoisi) {
        Insets vMarges = tabPane.getInsets();
        int vHaut = vMarges.top + calculateTabAreaHeight(pPlacement, runCount, maxTabHeight);
        pG.setColor(Habillage.BORDURE);
        pG.fillRect(vMarges.left, vHaut, tabPane.getWidth() - vMarges.left - vMarges.right, 1);
    }
}
