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
 * Écran de la calculatrice : afficheur à cristaux liquides gris-vert enfoncé dans un cadre noir, chiffres
 * à sept segments ({@link SeptSegments}), mode d'angle en petit. Le texte trop long rétrécit au lieu d'être coupé.
 */
class Ecran extends JComponent {
    private static final long serialVersionUID = 1L;
    private static final int TAILLE_MAX = 44;
    private static final int TAILLE_MIN = 16;
    private static final int HAUTEUR_MAX = 48;
    private static final int MARGE = 18;

    private String aValeur = "0";
    private String aMode = "DEG";
    // Calcul qui a donné le nombre affiché, en petit au-dessus
    private String aRappel = "";

    Ecran() {
        setPreferredSize(new Dimension(400, 118));
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
     * Plus grande hauteur de chiffres à sept segments, de 48 à 16 points, qui tient dans la largeur donnée.
     */
    double hauteurQuiTient(int pLargeur) {
        double rHauteur = HAUTEUR_MAX;
        while (rHauteur > TAILLE_MIN && SeptSegments.largeur(aValeur, rHauteur) > pLargeur) {
            rHauteur -= 2;
        }
        return rHauteur;
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
        int vDroite = getWidth() - MARGE - 6;
        int vBas = getHeight() - 26;
        Color vEncre = MoteurCalcul.ERREUR.equals(aValeur) ? Habillage.ERREUR_ECRAN : Habillage.CHIFFRES;
        if (SeptSegments.sait(aValeur)) {
            double vHauteur = hauteurQuiTient(vLargeur);
            Color vEteint = new Color(Habillage.CHIFFRES.getRed(), Habillage.CHIFFRES.getGreen(),
                    Habillage.CHIFFRES.getBlue(), 16);
            SeptSegments.dessiner(vG, aValeur, MARGE + 6, vDroite, vBas, vHauteur, vEncre, vEteint);
        } else {
            vG.setFont(Habillage.chiffres(Font.PLAIN, tailleQuiTient(vG, vLargeur)));
            FontMetrics vMetrics = vG.getFontMetrics();
            vG.setColor(vEncre);
            vG.drawString(aValeur, vDroite - vMetrics.stringWidth(aValeur), vBas);
        }
        vG.dispose();
    }
}
