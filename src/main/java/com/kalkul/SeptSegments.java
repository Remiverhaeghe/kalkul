package com.kalkul;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.Map;

/**
 * Afficheur à sept segments, comme sur une calculatrice de bureau : chaque caractère est une case
 * de sept barres, allumées ou éteintes, légèrement penchées. Le point se loge entre deux cases.
 * Les textes qu'il ne sait pas écrire (lettres rares, signes) restent à la police de l'écran.
 */
final class SeptSegments {
    // Bits des segments : a en haut, puis dans le sens des aiguilles, g au milieu
    private static final int A = 1;
    private static final int B = 2;
    private static final int C = 4;
    private static final int D = 8;
    private static final int E = 16;
    private static final int F = 32;
    private static final int G = 64;

    private static final Map<Character, Integer> FORMES = Map.ofEntries(
            Map.entry('0', A | B | C | D | E | F), Map.entry('1', B | C),
            Map.entry('2', A | B | G | E | D), Map.entry('3', A | B | G | C | D),
            Map.entry('4', F | G | B | C), Map.entry('5', A | F | G | C | D),
            Map.entry('6', A | F | G | E | D | C), Map.entry('7', A | B | C),
            Map.entry('8', A | B | C | D | E | F | G), Map.entry('9', A | B | C | D | F | G),
            Map.entry('-', G), Map.entry(' ', 0),
            Map.entry('E', A | F | G | E | D), Map.entry('e', A | B | D | E | F | G),
            Map.entry('R', E | G), Map.entry('r', E | G),
            Map.entry('U', B | C | D | E | F), Map.entry('u', C | D | E),
            Map.entry('O', A | B | C | D | E | F), Map.entry('o', C | D | E | G),
            Map.entry('n', C | E | G), Map.entry('d', B | C | D | E | G),
            Map.entry('i', C), Map.entry('f', A | F | G | E), Map.entry('é', A | B | D | E | F | G));

    /** Largeur d'une case, en part de la hauteur des chiffres. */
    static final double CASE = 0.5;
    /** Espace entre deux cases, en part de la hauteur. */
    static final double ECART = 0.3;
    /** Pente des chiffres : un peu d'italique, comme les afficheurs à cristaux liquides. */
    private static final double PENTE = 0.08;

    private SeptSegments() {
    }

    /**
     * Segments allumés d'un caractère, un bit par segment ; -1 s'il ne s'écrit pas en sept segments.
     */
    static int forme(char pCaractere) {
        Integer vForme = FORMES.get(pCaractere);
        int rForme = vForme == null ? -1 : vForme;
        return rForme;
    }

    /**
     * Vrai si tout le texte s'écrit en sept segments (le point compris).
     */
    static boolean sait(String pTexte) {
        boolean rSait = !pTexte.isEmpty();
        for (char vCaractere : pTexte.toCharArray()) {
            if (vCaractere != '.' && forme(vCaractere) < 0) {
                rSait = false;
            }
        }
        return rSait;
    }

    /**
     * Nombre de cases occupées : un point ne prend pas de case, sauf en tête.
     */
    static int cases(String pTexte) {
        int rCases = 0;
        for (int vIndex = 0; vIndex < pTexte.length(); vIndex++) {
            if (pTexte.charAt(vIndex) != '.' || vIndex == 0) {
                rCases++;
            }
        }
        return rCases;
    }

    /**
     * Largeur du texte pour une hauteur de chiffres donnée.
     */
    static double largeur(String pTexte, double pHauteur) {
        int vCases = cases(pTexte);
        double rLargeur = vCases * pHauteur * CASE + Math.max(vCases - 1, 0) * pHauteur * ECART;
        return rLargeur;
    }

    /**
     * Dessine le texte calé à droite en [pDroite], la base des chiffres en [pBas] ; les cases
     * libres à gauche jusqu'à [pGauche] montrent leurs segments éteints, comme un vrai afficheur.
     */
    static void dessiner(Graphics2D pG, String pTexte, double pGauche, double pDroite, double pBas,
            double pHauteur, Color pAllume, Color pEteint) {
        double vPas = pHauteur * (CASE + ECART);
        double vX = pDroite - pHauteur * CASE;
        for (int vIndex = pTexte.length() - 1; vIndex >= 0; vIndex--) {
            char vCaractere = pTexte.charAt(vIndex);
            boolean vPoint = vIndex + 1 < pTexte.length() && pTexte.charAt(vIndex + 1) == '.';
            if (vCaractere == '.' && vIndex > 0) {
                continue;
            }
            int vForme = vCaractere == '.' ? 0 : forme(vCaractere);
            chiffre(pG, vX, pBas, pHauteur, vForme, vPoint || vCaractere == '.', pAllume, pEteint);
            vX -= vPas;
        }
        while (vX >= pGauche) {
            chiffre(pG, vX, pBas, pHauteur, 0, false, pAllume, pEteint);
            vX -= vPas;
        }
    }

    private static void chiffre(Graphics2D pG, double pX, double pBas, double pHauteur, int pForme,
            boolean pPoint, Color pAllume, Color pEteint) {
        double vLargeur = pHauteur * CASE;
        double vTrait = pHauteur * 0.1;
        double vMoitie = pHauteur / 2;
        double vHaut = pBas - pHauteur;
        AffineTransform vAvant = pG.getTransform();
        // Penche la case autour de sa base
        pG.translate(pX, pBas);
        pG.shear(-PENTE, 0);
        pG.translate(-pX, -pBas);
        segment(pG, pForme, A, barre(pX, vHaut, vLargeur, vTrait, true), pAllume, pEteint);
        segment(pG, pForme, G, barre(pX, vHaut + vMoitie, vLargeur, vTrait, true), pAllume, pEteint);
        segment(pG, pForme, D, barre(pX, pBas, vLargeur, vTrait, true), pAllume, pEteint);
        segment(pG, pForme, F, barre(pX, vHaut, vMoitie, vTrait, false), pAllume, pEteint);
        segment(pG, pForme, B, barre(pX + vLargeur, vHaut, vMoitie, vTrait, false), pAllume, pEteint);
        segment(pG, pForme, E, barre(pX, vHaut + vMoitie, vMoitie, vTrait, false), pAllume, pEteint);
        segment(pG, pForme, C, barre(pX + vLargeur, vHaut + vMoitie, vMoitie, vTrait, false), pAllume,
                pEteint);
        pG.setTransform(vAvant);
        double vRayon = vTrait * 0.62;
        pG.setColor(pPoint ? pAllume : pEteint);
        pG.fill(new Ellipse2D.Double(pX + vLargeur + pHauteur * ECART / 2 - vRayon, pBas - vRayon * 2,
                vRayon * 2, vRayon * 2));
    }

    private static void segment(Graphics2D pG, int pForme, int pBit, Path2D pBarre, Color pAllume,
            Color pEteint) {
        pG.setColor((pForme & pBit) != 0 ? pAllume : pEteint);
        pG.fill(pBarre);
    }

    /**
     * Barre hexagonale de longueur [pLongueur] partant de (pX, pY), horizontale ou verticale :
     * les pointes laissent un fin jour entre deux segments voisins.
     */
    private static Path2D barre(double pX, double pY, double pLongueur, double pTrait, boolean pHorizontale) {
        double vDemi = pTrait / 2;
        double vJour = pTrait * 0.18;
        Path2D.Double rBarre = new Path2D.Double();
        if (pHorizontale) {
            rBarre.moveTo(pX + vJour, pY);
            rBarre.lineTo(pX + vJour + vDemi, pY - vDemi);
            rBarre.lineTo(pX + pLongueur - vJour - vDemi, pY - vDemi);
            rBarre.lineTo(pX + pLongueur - vJour, pY);
            rBarre.lineTo(pX + pLongueur - vJour - vDemi, pY + vDemi);
            rBarre.lineTo(pX + vJour + vDemi, pY + vDemi);
        } else {
            rBarre.moveTo(pX, pY + vJour);
            rBarre.lineTo(pX + vDemi, pY + vJour + vDemi);
            rBarre.lineTo(pX + vDemi, pY + pLongueur - vJour - vDemi);
            rBarre.lineTo(pX, pY + pLongueur - vJour);
            rBarre.lineTo(pX - vDemi, pY + pLongueur - vJour - vDemi);
            rBarre.lineTo(pX - vDemi, pY + vJour + vDemi);
        }
        rBarre.closePath();
        return rBarre;
    }
}
