package com.kalkul;

/**
 * Calculs de chantier : matériau en paquets avec la marge de chute, béton gâché à la main et pente.
 * Toutes les longueurs sont en mètres sauf mention contraire.
 */
final class Chantier {
    /** Dosage courant d'un béton de dalle, pour 1 m³. */
    static final double CIMENT_KG_M3 = 350;
    static final double SABLE_KG_M3 = 700;
    static final double GRAVIER_KG_M3 = 1050;
    static final double EAU_L_M3 = 175;
    /** Sac de ciment courant. */
    static final double SAC_CIMENT_KG = 35;

    /** Béton pour une dalle : volume et ce qu'il faut gâcher. */
    record Beton(double volumeM3, int sacsCiment, double sableKg, double gravierKg, double eauL) {
    }

    private Chantier() {
    }

    private static void positif(double... pValeurs) {
        for (double vValeur : pValeurs) {
            if (vValeur < 0 || Double.isNaN(vValeur)) {
                throw new IllegalArgumentException("Les dimensions doivent être positives");
            }
        }
    }

    /**
     * Paquets à acheter pour couvrir pSurfaceM2, avec pMargePourcent de chutes et pM2ParPaquet par paquet.
     */
    static int paquets(double pSurfaceM2, double pMargePourcent, double pM2ParPaquet) {
        positif(pSurfaceM2, pMargePourcent);
        if (pM2ParPaquet <= 0) {
            throw new IllegalArgumentException("Un paquet doit couvrir une surface");
        }
        // Le petit epsilon évite d'acheter un paquet de trop pour une erreur d'arrondi
        int rPaquets = (int) Math.ceil(pSurfaceM2 * (1 + pMargePourcent / 100) / pM2ParPaquet - 1e-9);
        return rPaquets;
    }

    /**
     * Béton d'une dalle de pLongueur × pLargeur mètres sur pEpaisseurCm centimètres.
     */
    static Beton beton(double pLongueur, double pLargeur, double pEpaisseurCm) {
        positif(pLongueur, pLargeur, pEpaisseurCm);
        double vVolume = pLongueur * pLargeur * pEpaisseurCm / 100;
        Beton rBeton = new Beton(vVolume, (int) Math.ceil(vVolume * CIMENT_KG_M3 / SAC_CIMENT_KG - 1e-9),
                vVolume * SABLE_KG_M3, vVolume * GRAVIER_KG_M3, vVolume * EAU_L_M3);
        return rBeton;
    }

    /**
     * Dénivelé sur pLongueur mètres à pPentePourcent de pente, en centimètres.
     */
    static double deniveleCm(double pLongueur, double pPentePourcent) {
        positif(pLongueur);
        double rDenivele = pLongueur * pPentePourcent;
        return rDenivele;
    }

    /**
     * Pente en pourcentage pour pDeniveleCm centimètres sur pLongueur mètres.
     */
    static double pentePourcent(double pDeniveleCm, double pLongueur) {
        if (pLongueur <= 0) {
            throw new IllegalArgumentException("La longueur doit être positive");
        }
        double rPente = pDeniveleCm / pLongueur;
        return rPente;
    }
}
