package com.kalkul;

/**
 * Terrassement : volume à creuser ou à remplir, foisonnement des déblais, masse de sable ou de
 * gravier et nombre de big bags. Les densités sont indicatives : elles varient avec l'humidité et le
 * calibre, la fiche du négoce fait foi.
 */
final class Terrassement {
    /** Foisonnement courant d'une terre ordinaire une fois creusée, en pourcentage (indicatif). */
    static final double FOISONNEMENT_POURCENT = 25;

    /** Matériaux en vrac et densité apparente indicative en tonnes par m³. */
    enum Materiau {
        SABLE("Sable", 1.6),
        GRAVIER("Gravier", 1.5),
        TOUT_VENANT("Tout-venant 0/31,5", 1.8);

        private final String aLibelle;
        private final double aDensite;

        Materiau(String pLibelle, double pDensite) {
            aLibelle = pLibelle;
            aDensite = pDensite;
        }

        double densite() {
            return aDensite;
        }

        @Override
        public String toString() {
            return aLibelle;
        }
    }

    /** Ce qu'il faut faire livrer pour un remblai. */
    record Apport(double volumeM3, double masseT, int bigBags) {
    }

    private Terrassement() {
    }

    /** Volume en m³ d'une fouille ou d'un lit de pLongueur × pLargeur mètres sur pProfondeurCm centimètres. */
    static double volume(double pLongueur, double pLargeur, double pProfondeurCm) {
        positif(pLongueur, pLargeur, pProfondeurCm);
        double rVolume = pLongueur * pLargeur * pProfondeurCm / 100;
        return rVolume;
    }

    /** Volume des déblais une fois sortis du trou, avec pFoisonnementPourcent de foisonnement. */
    static double foisonne(double pVolumeM3, double pFoisonnementPourcent) {
        positif(pVolumeM3, pFoisonnementPourcent);
        double rVolume = pVolumeM3 * (1 + pFoisonnementPourcent / 100);
        return rVolume;
    }

    /** Big bags de pCapacite (m³ ou tonnes, la même unité que pQuantite) pour pQuantite. */
    static int bigBags(double pQuantite, double pCapacite) {
        positif(pQuantite);
        if (!(pCapacite > 0)) {
            throw new IllegalArgumentException("Un big bag doit avoir une capacité");
        }
        // Le petit epsilon évite un big bag de trop pour une erreur d'arrondi
        int rBigBags = (int) Math.ceil(pQuantite / pCapacite - 1e-9);
        return rBigBags;
    }

    /**
     * Apport de pMateriau pour pVolumeM3 tassés, avec pMargePourcent pour le tassement et les pertes,
     * livré en big bags de pCapaciteT tonnes.
     */
    static Apport apport(double pVolumeM3, Materiau pMateriau, double pMargePourcent, double pCapaciteT) {
        positif(pVolumeM3, pMargePourcent);
        double vVolume = pVolumeM3 * (1 + pMargePourcent / 100);
        double vMasse = vVolume * pMateriau.densite();
        Apport rApport = new Apport(vVolume, vMasse, bigBags(vMasse, pCapaciteT));
        return rApport;
    }

    private static void positif(double... pValeurs) {
        for (double vValeur : pValeurs) {
            if (!(vValeur >= 0)) {
                throw new IllegalArgumentException("Les valeurs doivent être positives");
            }
        }
    }
}
