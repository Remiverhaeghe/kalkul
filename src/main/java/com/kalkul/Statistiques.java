package com.kalkul;

import java.util.Arrays;

/** Résumé statistique d'une série de nombres et son histogramme, à part de l'onglet Swing pour rester testable. */
final class Statistiques {

    /**
     * Résumé d'une série ; les écarts-types valent NaN quand ils n'existent pas (un seul nombre pour
     * l'écart-type d'échantillon).
     */
    record Resume(int nombre, double moyenne, double mediane, double minimum, double maximum, double premierQuartile,
            double troisiemeQuartile, double ecartTypeEchantillon, double ecartTypePopulation) {
    }

    private Statistiques() {
    }

    /** Résumé complet ; les quartiles sont interpolés comme QUARTILE.INC d'un tableur. */
    static Resume resumer(double[] pValeurs) {
        if (pValeurs.length == 0) {
            throw new IllegalArgumentException("Aucune valeur");
        }
        double[] vTriees = pValeurs.clone();
        Arrays.sort(vTriees);
        double vSomme = 0;
        for (double vValeur : vTriees) {
            vSomme += vValeur;
        }
        double vMoyenne = vSomme / vTriees.length;
        double vEcarts = 0;
        for (double vValeur : vTriees) {
            vEcarts += (vValeur - vMoyenne) * (vValeur - vMoyenne);
        }
        double vEchantillon = vTriees.length < 2 ? Double.NaN : Math.sqrt(vEcarts / (vTriees.length - 1));
        return new Resume(vTriees.length, vMoyenne, quantile(vTriees, 0.5), vTriees[0], vTriees[vTriees.length - 1],
                quantile(vTriees, 0.25), quantile(vTriees, 0.75), vEchantillon, Math.sqrt(vEcarts / vTriees.length));
    }

    // Quantile d'une série triée, interpolé entre les deux valeurs voisines
    private static double quantile(double[] pTriees, double pPart) {
        double vPosition = pPart * (pTriees.length - 1);
        int vBas = (int) Math.floor(vPosition);
        int vHaut = (int) Math.ceil(vPosition);
        return pTriees[vBas] + (pTriees[vHaut] - pTriees[vBas]) * (vPosition - vBas);
    }

    /** Nombre de classes de la règle de Sturges, 1 + log2(n), au moins 1. */
    static int classesSturges(int pNombre) {
        return Math.max(1, (int) Math.ceil(1 + Math.log(Math.max(1, pNombre)) / Math.log(2)));
    }

    /**
     * Effectif de chaque classe de même largeur entre le minimum et le maximum ; le maximum entre dans la
     * dernière classe. Quand toutes les valeurs sont égales, tout tombe dans la première classe.
     */
    static int[] histogramme(double[] pValeurs, int pClasses) {
        if (pValeurs.length == 0 || pClasses < 1) {
            throw new IllegalArgumentException("Série vide ou nombre de classes invalide");
        }
        double vMin = Arrays.stream(pValeurs).min().getAsDouble();
        double vMax = Arrays.stream(pValeurs).max().getAsDouble();
        double vLargeur = (vMax - vMin) / pClasses;
        int[] rEffectifs = new int[pClasses];
        for (double vValeur : pValeurs) {
            int vClasse = vLargeur == 0 ? 0 : (int) ((vValeur - vMin) / vLargeur);
            rEffectifs[Math.min(vClasse, pClasses - 1)]++;
        }
        return rEffectifs;
    }
}
