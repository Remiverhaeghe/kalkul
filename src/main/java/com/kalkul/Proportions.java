package com.kalkul;

/**
 * Règle de trois et pourcentages du quotidien : quatrième proportionnelle, part, remises, variation
 * et prix au mètre carré.
 */
final class Proportions {
    private Proportions() {
    }

    /** Si pA donne pB, ce que donne pC : pB × pC / pA. */
    static double regleDeTrois(double pA, double pB, double pC) {
        nonNul(pA, "La première valeur ne peut pas être nulle");
        double rValeur = pB * pC / pA;
        return rValeur;
    }

    /** pPourcent % de pValeur. */
    static double pourcentageDe(double pPourcent, double pValeur) {
        double rValeur = pValeur * pPourcent / 100;
        return rValeur;
    }

    /** Ce que pPart représente de pTotal, en pourcentage. */
    static double part(double pPart, double pTotal) {
        nonNul(pTotal, "Le total ne peut pas être nul");
        double rPourcent = pPart / pTotal * 100;
        return rPourcent;
    }

    /** Évolution de pAvant à pApres, en pourcentage. */
    static double variation(double pAvant, double pApres) {
        nonNul(pAvant, "La valeur de départ ne peut pas être nulle");
        double rPourcent = (pApres - pAvant) / Math.abs(pAvant) * 100;
        return rPourcent;
    }

    /**
     * Prix après des remises successives : 20 % puis 10 % ne font pas 30 % mais 28 %.
     */
    static double apresRemises(double pPrix, double... pRemisesPourcent) {
        double rPrix = pPrix;
        for (double vRemise : pRemisesPourcent) {
            if (!(vRemise >= 0 && vRemise <= 100)) {
                throw new IllegalArgumentException("Une remise se donne entre 0 et 100 %");
            }
            rPrix = rPrix * (1 - vRemise / 100);
        }
        return rPrix;
    }

    /** Prix avant une remise de pRemisePourcent qui a donné pPrixRemise. */
    static double avantRemise(double pPrixRemise, double pRemisePourcent) {
        if (!(pRemisePourcent >= 0 && pRemisePourcent < 100)) {
            throw new IllegalArgumentException("Une remise se donne entre 0 et 100 %, 100 exclu");
        }
        double rPrix = pPrixRemise / (1 - pRemisePourcent / 100);
        return rPrix;
    }

    /** Prix au mètre carré de pPrix pour pSurfaceM2. */
    static double prixAuM2(double pPrix, double pSurfaceM2) {
        if (!(pSurfaceM2 > 0)) {
            throw new IllegalArgumentException("La surface doit être positive");
        }
        double rPrix = pPrix / pSurfaceM2;
        return rPrix;
    }

    private static void nonNul(double pValeur, String pMessage) {
        if (pValeur == 0 || Double.isNaN(pValeur)) {
            throw new IllegalArgumentException(pMessage);
        }
    }
}
