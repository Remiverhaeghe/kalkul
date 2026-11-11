package com.kalkul;

/**
 * Opérations sur les vecteurs, donnés sous forme de tableaux de nombres.
 */
public final class Vecteurs {
    private Vecteurs() {
    }

    public static double dot(double[] pFirst, double[] pSecond) {
        if (pFirst.length != pSecond.length) {
            throw new IllegalArgumentException("Les vecteurs doivent avoir la même dimension");
        }
        double rDot = 0;
        for (int vIndex = 0; vIndex < pFirst.length; vIndex++) {
            rDot += pFirst[vIndex] * pSecond[vIndex];
        }
        return rDot;
    }

    public static double norm(double[] pVector) {
        double rNorm = Math.sqrt(dot(pVector, pVector));
        return rNorm;
    }

    /**
     * Produit vectoriel de deux vecteurs de l'espace (dimension 3).
     */
    public static double[] cross(double[] pFirst, double[] pSecond) {
        if (pFirst.length != 3 || pSecond.length != 3) {
            throw new IllegalArgumentException("Le produit vectoriel demande deux vecteurs à 3 composantes");
        }
        double[] rCross = {
            pFirst[1] * pSecond[2] - pFirst[2] * pSecond[1],
            pFirst[2] * pSecond[0] - pFirst[0] * pSecond[2],
            pFirst[0] * pSecond[1] - pFirst[1] * pSecond[0]
        };
        return rCross;
    }
}
