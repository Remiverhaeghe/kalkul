package com.kalkul;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;

/**
 * Écran de la calculatrice : afficheur à cristaux liquides gris-vert enfoncé dans un cadre noir,
 * mode d'angle en petit. Le texte trop long rétrécit au lieu d'être coupé.
 */
class Ecran extends JComponent {
    private static final long serialVersionUID = 1L;
    private static final int TAILLE_MAX = 44;
    private static final int TAILLE_MIN = 16;
    private static final int MARGE = 18;

    private String aValeur = "0";
    private String aMode = "DEG";
    // Calcul qui a donné le nombre affiché, en petit au-dessus
    private String aRappel = "";

    Ecran() {
        setPreferredSize(new Dimension(400, 104));
    }

    void afficher(String pValeur, boolean pDegres) {
        aValeur = pValeur;
        aMode = pDegres ? "DEG" : "RAD";
        repaint();
    }

    void rappel(String pRappel) {
        aRappel = pRappel;
        repaint();
    }

    String rappel() {
        return aRappel;
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

        // Cadre noir dans lequel l'écran est enfoncé
        vG.setColor(Habillage.CADRE_ECRAN);
        vG.fill(new RoundRectangle2D.Double(8, 8, getWidth() - 16.0, getHeight() - 16.0, 18, 18));
        RoundRectangle2D vVerre = new RoundRectangle2D.Double(14, 14, getWidth() - 28.0, getHeight() - 28.0, 10, 10);
        vG.setPaint(new GradientPaint(0, 14, Touche.nuance(Habillage.ECRAN, -10), 0, getHeight() - 14f,
                Habillage.ECRAN));
        vG.fill(vVerre);
        // Ombre du cadre sur le haut du verre
        vG.setClip(vVerre);
        vG.setPaint(new GradientPaint(0, 14, new Color(0, 0, 0, 70), 0, 24, new Color(0, 0, 0, 0)));
        vG.fillRect(14, 14, getWidth() - 28, 10);
        vG.setClip(null);

        vG.setFont(Habillage.texte(Font.BOLD, 11));
        vG.setColor(Habillage.MODE_ECRAN);
        vG.drawString(aMode, MARGE + 6, 32);
        if (!aRappel.isEmpty()) {
            vG.setFont(Habillage.chiffres(Font.PLAIN, 14));
            vG.drawString(aRappel, getWidth() - MARGE - 6 - vG.getFontMetrics().stringWidth(aRappel), 33);
        }

        int vLargeur = getWidth() - 2 * MARGE - 12;
        Font vChiffres = Habillage.chiffres(Font.PLAIN, tailleQuiTient(vG, vLargeur));
        vG.setFont(vChiffres);
        FontMetrics vMetrics = vG.getFontMetrics();
        int vX = getWidth() - MARGE - 6 - vMetrics.stringWidth(aValeur);
        int vY = getHeight() - 24;
        vG.setColor(MoteurCalcul.ERREUR.equals(aValeur) ? Habillage.ERREUR_ECRAN : Habillage.CHIFFRES);
        vG.drawString(aValeur, vX, vY);
        vG.dispose();
    }
}
