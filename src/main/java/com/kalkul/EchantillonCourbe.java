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

    static boolean isUsable(double pY) {
        boolean rUsable = !Double.isNaN(pY) && !Double.isInfinite(pY) && Math.abs(pY) <= Y_LIMIT;
        return rUsable;
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
