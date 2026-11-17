package com.kalkul;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;

/**
 * Écran de la calculatrice : chiffres cyan sur fond sombre, comme un afficheur fluorescent,
 * avec le mode d'angle en petit. Le texte trop long rétrécit au lieu d'être coupé.
 */
class Ecran extends JComponent {
    private static final long serialVersionUID = 1L;
    private static final int TAILLE_MAX = 44;
    private static final int TAILLE_MIN = 16;
    private static final int MARGE = 18;

    private String aValeur = "0";
    private String aMode = "DEG";

    Ecran() {
        setPreferredSize(new Dimension(400, 104));
    }

    void afficher(String pValeur, boolean pDegres) {
        aValeur = pValeur;
        aMode = pDegres ? "DEG" : "RAD";
        repaint();
    }

    String valeur() {
        return aValeur;
    }

    /**
     * Plus grande taille de police, de 44 à 16 points, pour laquelle le texte tient dans la largeur donnée.
     */
    int tailleQuiTient(Graphics pGraphics, int pLargeur) {
        int rTaille = TAILLE_MAX;
        while (rTaille > TAILLE_MIN
                && pGraphics.getFontMetrics(Habillage.chiffres(Font.PLAIN, rTaille)).stringWidth(aValeur) > pLargeur) {
            rTaille -= 2;
        }
        return rTaille;
    }

    @Override
    protected void paintComponent(Graphics pGraphics) {
        Graphics2D vG = (Graphics2D) pGraphics.create();
        vG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        vG.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        vG.setColor(Habillage.FOND);
        vG.fillRect(0, 0, getWidth(), getHeight());

        RoundRectangle2D vCadre = new RoundRectangle2D.Double(8, 8, getWidth() - 16.0, getHeight() - 16.0, 16, 16);
        vG.setColor(Habillage.ECRAN);
        vG.fill(vCadre);
        vG.setStroke(new BasicStroke(1.5f));
        vG.setColor(Habillage.CADRE_ECRAN);
        vG.draw(vCadre);

        vG.setFont(Habillage.texte(Font.BOLD, 11));
        vG.setColor(Habillage.MODE_ECRAN);
        vG.drawString(aMode, MARGE + 4, 30);

        int vLargeur = getWidth() - 2 * MARGE - 8;
        Font vChiffres = Habillage.chiffres(Font.PLAIN, tailleQuiTient(vG, vLargeur));
        vG.setFont(vChiffres);
        FontMetrics vMetrics = vG.getFontMetrics();
        int vX = getWidth() - MARGE - 4 - vMetrics.stringWidth(aValeur);
        int vY = getHeight() - 22;
        // Halo discret derrière les chiffres, comme la lueur d'un tube fluorescent
        vG.setColor(new Color(Habillage.CHIFFRES.getRed(), Habillage.CHIFFRES.getGreen(),
                Habillage.CHIFFRES.getBlue(), 40));
        vG.drawString(aValeur, vX + 1, vY + 1);
        vG.drawString(aValeur, vX - 1, vY - 1);
        vG.setColor(MoteurCalcul.ERREUR.equals(aValeur) ? Habillage.ERREUR_ECRAN : Habillage.CHIFFRES);
        vG.drawString(aValeur, vX, vY);
        vG.dispose();
    }
}
