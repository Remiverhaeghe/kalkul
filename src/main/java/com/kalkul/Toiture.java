package com.kalkul;

/**
 * Pente de toiture en pourcentage, en degrés et en centimètres par mètre, longueur de rampant
 * et pente minimale indicative selon la couverture.
 */
final class Toiture {
    /**
     * Couvertures courantes et pente minimale indicative en pourcentage. Le minimum réel dépend du
     * modèle, de la zone climatique, de l'exposition et de la longueur du rampant : notice du fabricant.
     */
    enum Couverture {
        TUILE_CANAL("Tuile canal", 24),
        TUILE_EMBOITEMENT_RELIEF("Tuile à emboîtement à relief (romane)", 30),
        TUILE_EMBOITEMENT_PLATE("Tuile plate à emboîtement", 35),
        ARDOISE("Ardoise naturelle", 40),
        TUILE_PLATE("Tuile plate petit moule", 70);

        private final String aLibelle;
        private final double aMinimumPourcent;

        Couverture(String pLibelle, double pMinimumPourcent) {
            aLibelle = pLibelle;
            aMinimumPourcent = pMinimumPourcent;
        }

        double minimumPourcent() {
            return aMinimumPourcent;
        }

        @Override
        public String toString() {
            return aLibelle;
        }
    }

    /** Une pente sous ses trois écritures. */
    record Pente(double pourcent, double degres, double cmParMetre) {
    }

    private Toiture() {
    }

    /** Pente donnée en pourcentage. */
    static Pente depuisPourcent(double pPourcent) {
        if (!(pPourcent >= 0)) {
            throw new IllegalArgumentException("La pente doit être positive");
        }
        // Le cm par mètre de projection horizontale vaut le pourcentage
        Pente rPente = new Pente(pPourcent, Conversions.convert(pPourcent, "% de pente", "° de pente"), pPourcent);
        return rPente;
    }

    /** Pente donnée en degrés. */
    static Pente depuisDegres(double pDegres) {
        if (!(pDegres >= 0 && pDegres < 90)) {
            throw new IllegalArgumentException("Une pente de toit se mesure entre 0 et 90°");
        }
        Pente rPente = depuisPourcent(Conversions.convert(pDegres, "° de pente", "% de pente"));
        return rPente;
    }

    /** Pente d'une montée de pMonteeCm centimètres sur pProjectionM mètres à l'horizontale. */
    static Pente depuisMontee(double pMonteeCm, double pProjectionM) {
        Pente rPente = depuisPourcent(Chantier.pentePourcent(pMonteeCm, pProjectionM));
        return rPente;
    }

    /** Longueur du rampant, en mètres, pour pProjectionM mètres à l'horizontale. */
    static double rampant(double pProjectionM, Pente pPente) {
        if (!(pProjectionM >= 0)) {
            throw new IllegalArgumentException("La longueur doit être positive");
        }
        double rRampant = pProjectionM * Math.hypot(1, pPente.pourcent() / 100);
        return rRampant;
    }

    /** Vrai si la pente atteint le minimum indicatif de la couverture. */
    static boolean suffit(Pente pPente, Couverture pCouverture) {
        // Tolérance pour une pente saisie en degrés qui tombe pile sur le minimum
        boolean rSuffit = pPente.pourcent() >= pCouverture.minimumPourcent() - 1e-9;
        return rSuffit;
    }
}
