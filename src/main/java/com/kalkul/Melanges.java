package com.kalkul;

/**
 * Gâchée de béton, de mortier ou d'enduit selon le dosage en ciment : sacs de ciment, seaux de sable
 * et de gravier, eau. Les proportions sont des valeurs courantes, indicatives : la fiche du ciment et
 * la consistance voulue font foi, surtout pour l'eau.
 */
final class Melanges {
    /**
     * Mélanges et leurs proportions pour 1 m³ de mélange : dosage par défaut en kg de ciment, sable et
     * gravier en litres, eau en litres par kg de ciment. Pour le mortier et l'enduit, le dosage se donne
     * par m³ de sable, et 1 m³ de sable est compté pour 1 m³ de mélange (approximation).
     */
    enum Type {
        BETON("Béton", 350, 400, 800, 0.5),
        MORTIER("Mortier", 350, 1000, 0, 0.6),
        ENDUIT("Enduit", 400, 1000, 0, 0.6);

        private final String aLibelle;
        private final double aDosageKg;
        private final double aSableL;
        private final double aGravierL;
        private final double aEauParKg;

        Type(String pLibelle, double pDosageKg, double pSableL, double pGravierL, double pEauParKg) {
            aLibelle = pLibelle;
            aDosageKg = pDosageKg;
            aSableL = pSableL;
            aGravierL = pGravierL;
            aEauParKg = pEauParKg;
        }

        double dosageKg() {
            return aDosageKg;
        }

        @Override
        public String toString() {
            return aLibelle;
        }
    }

    /**
     * Ce qu'il faut pour la gâchée entière, et pour un sac de ciment (ce qu'on verse dans la bétonnière).
     */
    record Gachee(double cimentKg, int sacs, double sableSeaux, double gravierSeaux, double eauL,
            double sableSeauxParSac, double gravierSeauxParSac, double eauLParSac) {
    }

    private Melanges() {
    }

    /**
     * Gâchée de pVolumeL litres de pType dosé à pDosageKg, en sacs de pSacKg et en seaux de pSeauL litres.
     */
    static Gachee gachee(Type pType, double pVolumeL, double pDosageKg, double pSacKg, double pSeauL) {
        if (!(pVolumeL >= 0 && pDosageKg > 0)) {
            throw new IllegalArgumentException("Le volume et le dosage doivent être positifs");
        }
        if (!(pSacKg > 0 && pSeauL > 0)) {
            throw new IllegalArgumentException("Le sac et le seau doivent avoir une contenance");
        }
        double vMetresCubes = pVolumeL / 1000;
        double vCiment = vMetresCubes * pDosageKg;
        double vSable = vMetresCubes * pType.aSableL / pSeauL;
        double vGravier = vMetresCubes * pType.aGravierL / pSeauL;
        // Pour un sac, les proportions ne dépendent que du dosage, pas du volume voulu
        double vParSac = pSacKg / pDosageKg;
        Gachee rGachee = new Gachee(vCiment, (int) Math.ceil(vCiment / pSacKg - 1e-9), vSable, vGravier,
                vCiment * pType.aEauParKg, vParSac * pType.aSableL / pSeauL, vParSac * pType.aGravierL / pSeauL,
                pSacKg * pType.aEauParKg);
        return rGachee;
    }
}
