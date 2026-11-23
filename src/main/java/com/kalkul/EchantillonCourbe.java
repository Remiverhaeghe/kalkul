package com.kalkul;

import java.util.function.DoubleUnaryOperator;

/**
 * Points calculés d'une courbe sur un intervalle, avec la plage verticale utile pour l'afficher.
 */
final class EchantillonCourbe {
    // Au-delà, une valeur écraserait tout le dessin (asymptote) : elle est ignorée pour la plage
    private static final double Y_LIMIT = 1e6;
    private static final double MARGIN = 0.05;

    private final double[] aX;
    private final double[] aY;
    private final double aYMin;
    private final double aYMax;

    private EchantillonCourbe(double[] pX, double[] pY, double pYMin, double pYMax) {
        aX = pX;
        aY = pY;
        aYMin = pYMin;
        aYMax = pYMax;
    }

    static EchantillonCourbe sample(DoubleUnaryOperator pFunction, double pXMin, double pXMax, int pPoints) {
        if (!(pXMin < pXMax)) {
            throw new IllegalArgumentException("x min doit être inférieur à x max");
        }
        if (pPoints < 2) {
            throw new IllegalArgumentException("Il faut au moins deux points");
        }
        double[] vX = new double[pPoints];
        double[] vY = new double[pPoints];
        double vMin = Double.POSITIVE_INFINITY;
        double vMax = Double.NEGATIVE_INFINITY;
        for (int vIndex = 0; vIndex < pPoints; vIndex++) {
            vX[vIndex] = pXMin + (pXMax - pXMin) * vIndex / (pPoints - 1);
            vY[vIndex] = pFunction.applyAsDouble(vX[vIndex]);
            if (isUsable(vY[vIndex])) {
                vMin = Math.min(vMin, vY[vIndex]);
                vMax = Math.max(vMax, vY[vIndex]);
            }
        }
        if (vMin > vMax) {
            throw new ArithmeticException("La formule n'est définie nulle part sur cet intervalle");
        }
        if (vMin == vMax) {
            vMin -= 1;
            vMax += 1;
        }
        double vSpace = (vMax - vMin) * MARGIN;
        EchantillonCourbe rSample = new EchantillonCourbe(vX, vY, vMin - vSpace, vMax + vSpace);
        return rSample;
    }

    /**
     * Droite d'ajustement tracée un peu au-delà des points, avec une plage verticale qui montre tous les points.
     */
    static EchantillonCourbe scatter(double[] pX, double[] pY, DoubleUnaryOperator pLine, int pPoints) {
        double vXMin = Double.POSITIVE_INFINITY;
        double vXMax = Double.NEGATIVE_INFINITY;
        for (double vX : pX) {
            vXMin = Math.min(vXMin, vX);
            vXMax = Math.max(vXMax, vX);
        }
        if (!(vXMin < vXMax)) {
            throw new IllegalArgumentException("Il faut au moins deux x différents");
        }
        double vXSpace = (vXMax - vXMin) * MARGIN;
        EchantillonCourbe vLine = sample(pLine, vXMin - vXSpace, vXMax + vXSpace, pPoints);
        // La plage de la droite seule laisserait hors du cadre les points loin de la droite
        double vMin = vLine.aYMin;
        double vMax = vLine.aYMax;
        for (double vY : pY) {
            vMin = Math.min(vMin, vY);
            vMax = Math.max(vMax, vY);
        }
        double vYSpace = (vMax - vMin) * MARGIN;
        EchantillonCourbe rSample = new EchantillonCourbe(vLine.aX, vLine.aY, vMin - vYSpace, vMax + vYSpace);
        return rSample;
    }

    static boolean isUsable(double pY) {
        boolean rUsable = !Double.isNaN(pY) && !Double.isInfinite(pY) && Math.abs(pY) <= Y_LIMIT;
        return rUsable;
    }

    /**
     * Repères d'un axe, multiples du pas compris entre les bornes. Chaque repère est recalculé
     * depuis son rang : ajouter le pas en boucle laissait 1.2E-16 à la place de 0.
     */
    static double[] graduations(double pMin, double pMax, double pStep) {
        long vFirst = (long) Math.ceil(pMin / pStep);
        long vLast = (long) Math.floor(pMax / pStep);
        double[] rTicks = new double[(int) Math.max(vLast - vFirst + 1, 0)];
        for (int vIndex = 0; vIndex < rTicks.length; vIndex++) {
            double vTick = (vFirst + vIndex) * pStep;
            // Arrondi au chiffre significatif du pas, pour 0.30000000000000004 comme pour 0
            rTicks[vIndex] = Math.abs(vTick) < pStep * 1e-9 ? 0 : vTick;
        }
        return rTicks;
    }

    /**
     * Pas « rond » (1, 2, 5 fois une puissance de 10) qui donne environ huit repères sur l'étendue.
     */
    static double niceStep(double pRange) {
        double vRaw = pRange / 8;
        double vMagnitude = Math.pow(10, Math.floor(Math.log10(vRaw)));
        double vNormal = vRaw / vMagnitude;
        double vFactor;
        if (vNormal < 1.5) {
            vFactor = 1;
        } else if (vNormal < 3) {
            vFactor = 2;
        } else if (vNormal < 7) {
            vFactor = 5;
        } else {
            vFactor = 10;
        }
        double rStep = vFactor * vMagnitude;
        return rStep;
    }

    int size() {
        return aX.length;
    }

    double x(int pIndex) {
        return aX[pIndex];
    }

    double y(int pIndex) {
        return aY[pIndex];
    }

    double xMin() {
        return aX[0];
    }

    double xMax() {
        return aX[aX.length - 1];
    }

    double yMin() {
        return aYMin;
    }

    double yMax() {
        return aYMax;
    }
}
