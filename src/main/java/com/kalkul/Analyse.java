package com.kalkul;

import java.util.function.DoubleUnaryOperator;

/**
 * Dérivée et intégrale approchées d'une formule, par calcul numérique.
 */
public final class Analyse {
    private static final double DERIVATIVE_STEP = 1e-5;
    private static final int DEFAULT_INTERVALS = 1000;

    private Analyse() {
    }

    /**
     * Dérivée en un point, par différence centrée.
     */
    public static double derivative(Expression pFunction, double pX) {
        return derivative((DoubleUnaryOperator) pFunction::evaluate, pX);
    }

    public static double derivative(DoubleUnaryOperator pFunction, double pX) {
        double vStep = DERIVATIVE_STEP * Math.max(1, Math.abs(pX));
        double rDerivative = (pFunction.applyAsDouble(pX + vStep) - pFunction.applyAsDouble(pX - vStep)) / (2 * vStep);
        return rDerivative;
    }

    /**
     * Intégrale entre a et b par la méthode de Simpson (précise pour les fonctions régulières).
     */
    public static double integral(Expression pFunction, double pFrom, double pTo) {
        return integral((DoubleUnaryOperator) pFunction::evaluate, pFrom, pTo, DEFAULT_INTERVALS);
    }

    public static double integral(Expression pFunction, double pFrom, double pTo, int pIntervals) {
        return integral((DoubleUnaryOperator) pFunction::evaluate, pFrom, pTo, pIntervals);
    }

    public static double integral(DoubleUnaryOperator pFunction, double pFrom, double pTo) {
        return integral(pFunction, pFrom, pTo, DEFAULT_INTERVALS);
    }

    public static double integral(DoubleUnaryOperator pFunction, double pFrom, double pTo, int pIntervals) {
        if (pIntervals < 2) {
            throw new IllegalArgumentException("Il faut au moins deux intervalles");
        }
        // Simpson demande un nombre pair d'intervalles
        int vIntervals = pIntervals % 2 == 0 ? pIntervals : pIntervals + 1;
        double vStep = (pTo - pFrom) / vIntervals;
        double vSum = pFunction.applyAsDouble(pFrom) + pFunction.applyAsDouble(pTo);
        for (int vIndex = 1; vIndex < vIntervals; vIndex++) {
            double vWeight = vIndex % 2 == 0 ? 2 : 4;
            vSum += vWeight * pFunction.applyAsDouble(pFrom + vIndex * vStep);
        }
        double rIntegral = vSum * vStep / 3;
        if (Double.isNaN(rIntegral) || Double.isInfinite(rIntegral)) {
            throw new ArithmeticException("Intégrale impossible : la formule n'est pas définie sur tout l'intervalle");
        }
        return rIntegral;
    }
}
