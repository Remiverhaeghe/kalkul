package com.kalkul;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.ButtonModel;
import javax.swing.JButton;

/**
 * Touche de calculatrice dessinée à la main : coins arrondis, couleur selon son rôle,
 * plus claire au survol et plus sombre à l'appui.
 */
class Touche extends JButton {
    private static final long serialVersionUID = 1L;
    private static final int ARRONDI = 14;

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
        int vPas = Habillage.CLAIR ? -18 : 22;
        Color rColor = new Color(borne(pColor.getRed() + vPas), borne(pColor.getGreen() + vPas),
                borne(pColor.getBlue() + vPas));
        return rColor;
    }

    private static int borne(int pValeur) {
        return Math.max(0, Math.min(255, pValeur));
    }

    @Override
    protected void paintComponent(Graphics pGraphics) {
        Graphics2D vG = (Graphics2D) pGraphics.create();
        vG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        vG.setColor(fond());
        vG.fill(new RoundRectangle2D.Double(1, 1, getWidth() - 2.0, getHeight() - 2.0, ARRONDI, ARRONDI));
        // Liseré clair en haut : la touche paraît en relief
        vG.setColor(new Color(255, 255, 255, getModel().isPressed() ? 0 : 18));
        vG.drawLine(ARRONDI / 2, 2, getWidth() - ARRONDI / 2, 2);
        vG.dispose();
        super.paintComponent(pGraphics);
    }
}
