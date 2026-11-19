package com.kalkul;

/**
 * Angles de coupe à la scie à onglet : onglet simple, coupe composée d'une pièce inclinée et coupe
 * d'un chevron contre un arêtier. Les angles sont en degrés, comptés depuis la coupe d'équerre.
 */
final class Coupes {
    /** Réglage de la scie : rotation de la table (coupe) et inclinaison de la lame. */
    record Reglage(double coupeDeg, double lameDeg) {
    }

    /**
     * Angles d'un chevron : à tracer sur la pièce, et réglage de la scie pour la coupe contre l'arêtier.
     */
    record Chevron(double aplombDeg, double piedDeg, double joueDessusDeg, Reglage reglageArretier) {
    }

    private Coupes() {
    }

    /**
     * Onglet de chacune des deux pièces qui se rejoignent dans un angle de mur de pAngleMurDeg degrés.
     */
    static double onglet(double pAngleMurDeg) {
        if (!(pAngleMurDeg > 0 && pAngleMurDeg <= 180)) {
            throw new IllegalArgumentException("L'angle du mur se mesure entre 0 et 180°");
        }
        double rOnglet = (180 - pAngleMurDeg) / 2;
        return rOnglet;
    }

    /**
     * Coupe composée de deux pièces inclinées de pInclinaisonDeg par rapport à la verticale
     * (0 : debout comme une plinthe, 90 : à plat comme un cadre), qui se rejoignent dans un angle
     * de mur de pAngleMurDeg. La pièce est posée à plat sur la table de la scie.
     */
    static Reglage composee(double pAngleMurDeg, double pInclinaisonDeg) {
        if (!(pInclinaisonDeg >= 0 && pInclinaisonDeg <= 90)) {
            throw new IllegalArgumentException("L'inclinaison se mesure entre 0 et 90°");
        }
        double vOnglet = Math.toRadians(onglet(pAngleMurDeg));
        double vInclinaison = Math.toRadians(pInclinaisonDeg);
        // Formules de la coupe de trémie : elles redonnent l'onglet seul aux deux extrêmes
        double vCoupe = Math.toDegrees(Math.atan(Math.sin(vInclinaison) * Math.tan(vOnglet)));
        double vLame = Math.toDegrees(Math.asin(Math.cos(vInclinaison) * Math.sin(vOnglet)));
        Reglage rReglage = new Reglage(vCoupe, vLame);
        return rReglage;
    }

    /**
     * Angles d'un chevron sur un toit en pente de pPenteDeg, qui vient contre un arêtier formant
     * pAngleEnPlanDeg vu de dessus (45° pour un arêtier de toit carré).
     */
    static Chevron chevron(double pPenteDeg, double pAngleEnPlanDeg) {
        if (!(pPenteDeg >= 0 && pPenteDeg < 90)) {
            throw new IllegalArgumentException("La pente se mesure entre 0 et 90°");
        }
        if (!(pAngleEnPlanDeg >= 0 && pAngleEnPlanDeg < 90)) {
            throw new IllegalArgumentException("L'angle en plan se mesure entre 0 et 90°");
        }
        double vPente = Math.toRadians(pPenteDeg);
        // Vu sur le dessus incliné du chevron, l'angle en plan s'allonge de 1 / cos(pente)
        double vJoue = Math.toDegrees(Math.atan(Math.tan(Math.toRadians(pAngleEnPlanDeg)) / Math.cos(vPente)));
        // Chevron couché sur sa joue : la table tourne de la pente, la lame penche de l'angle en plan
        Chevron rChevron = new Chevron(pPenteDeg, 90 - pPenteDeg, vJoue, new Reglage(pPenteDeg, pAngleEnPlanDeg));
        return rChevron;
    }
}
