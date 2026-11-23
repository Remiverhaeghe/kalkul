package com.kalkul;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import javax.swing.ButtonModel;
import javax.swing.JButton;

/**
 * Touche ronde de calculatrice dessinée à la main : dôme éclairé par le haut, cuvette au centre,
 * ombre portée. Couleur selon son rôle, plus claire au survol, enfoncée et plus sombre à l'appui.
 */
class Touche extends JButton {
    private static final long serialVersionUID = 1L;
    // Espace laissé autour de la touche ronde, dans sa case
    private static final int MARGE = 5;

    private final Habillage.Role aRole;

    Touche(String pLabel) {
        super(Habillage.signe(pLabel));
        setActionCommand(pLabel);
        aRole = Habillage.role(pLabel);
        boolean vSigne = pLabel.length() == 1 && aRole != Habillage.Role.EFFACER;
        setFont(vSigne ? Habillage.chiffres(Font.BOLD, 24) : Habillage.texte(Font.BOLD, 16));
        setForeground(aRole.aTexte);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    Habillage.Role role() {
        return aRole;
    }

    /**
     * Couleur de fond selon l'état : appuyée, survolée ou au repos.
     */
    Color fond() {
        ButtonModel vModel = getModel();
        Color rFond = aRole.aFond;
        if (vModel.isPressed()) {
            rFond = rFond.darker();
        } else if (vModel.isRollover()) {
            rFond = eclaircir(rFond);
        }
        return rFond;
    }

    /**
     * Teinte de survol : plus claire en thème sombre, plus foncée en thème clair où le blanc ne s'éclaircit plus.
     */
    static Color eclaircir(Color pColor) {
        Color rColor = nuance(pColor, Habillage.CLAIR ? -18 : 22);
        return rColor;
    }

    private static int borne(int pValeur) {
        return Math.max(0, Math.min(255, pValeur));
    }

    @Override
    protected void paintComponent(Graphics pGraphics) {
        Graphics2D vG = (Graphics2D) pGraphics.create();
        vG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        boolean vAppuyee = getModel().isPressed();
        // Touche ronde au centre de sa case, enfoncée de deux points à l'appui
        double vDiametre = Math.max(Math.min(getWidth(), getHeight()) - 2.0 * MARGE, 10);
        double vX = (getWidth() - vDiametre) / 2;
        double vY = (getHeight() - vDiametre) / 2 - 1 + (vAppuyee ? 2 : 0);
        Color vFond = fond();

        if (!vAppuyee) {
            vG.setColor(new Color(0, 0, 0, Habillage.CLAIR ? 55 : 110));
            vG.fill(new Ellipse2D.Double(vX, vY + 3, vDiametre, vDiametre));
        }
        // Dôme : plus clair en haut, comme éclairé par une lampe de bureau
        vG.setPaint(new GradientPaint(0, (float) vY, nuance(vFond, 16), 0, (float) (vY + vDiametre),
                nuance(vFond, -14)));
        vG.fill(new Ellipse2D.Double(vX, vY, vDiametre, vDiametre));
        // Cuvette au centre, en creux : le dégradé s'inverse
        double vCreux = vDiametre * 0.72;
        double vDecalage = (vDiametre - vCreux) / 2;
        vG.setPaint(new GradientPaint(0, (float) (vY + vDecalage), nuance(vFond, -8), 0,
                (float) (vY + vDecalage + vCreux), nuance(vFond, 8)));
        vG.fill(new Ellipse2D.Double(vX + vDecalage, vY + vDecalage, vCreux, vCreux));
        vG.dispose();

        Graphics vTexte = pGraphics.create();
        vTexte.translate(0, vAppuyee ? 1 : -1);
        super.paintComponent(vTexte);
        vTexte.dispose();
    }

    /**
     * Couleur éclaircie (pas positif) ou assombrie (pas négatif), bornée à 0..255.
     */
    static Color nuance(Color pColor, int pPas) {
        Color rColor = new Color(borne(pColor.getRed() + pPas), borne(pColor.getGreen() + pPas),
                borne(pColor.getBlue() + pPas));
        return rColor;
    }
}
