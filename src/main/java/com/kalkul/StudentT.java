package com.kalkul;

/**
 * Loi de Student, pour le calcul des probabilités critiques (p-values).
 * Utilise la fonction bêta incomplète régularisée, calculée par fraction continue.
 */
final class StudentT {
    private static final int MAX_ITERATIONS = 300;
    private static final double PRECISION = 3e-16;
    private static final double TINY = 1e-300;
    private static final double[] LANCZOS = {
        76.18009172947146, -86.50532032941677, 24.01409824083091,
        -1.231739572450155, 0.1208650973866179e-2, -0.5395239384953e-5
    };

    private StudentT() {
    }

    /**
     * Probabilité qu'une variable de Student à pDegrees degrés de liberté dépasse |pT| en valeur absolue.
     */
    static double twoSidedPValue(double pT, int pDegrees) {
        if (pDegrees < 1) {
            throw new IllegalArgumentException("Il faut au moins un degré de liberté");
        }
        double vX = pDegrees / (pDegrees + pT * pT);
        double rPValue = regularizedBeta(pDegrees / 2.0, 0.5, vX);
        return rPValue;
    }

    private static double lnGamma(double pX) {
        double vY = pX;
        double vTmp = pX + 5.5;
        vTmp -= (pX + 0.5) * Math.log(vTmp);
        double vSeries = 1.000000000190015;
        for (double vCoefficient : LANCZOS) {
            vY += 1;
            vSeries += vCoefficient / vY;
        }
        double rLn = -vTmp + Math.log(2.5066282746310005 * vSeries / pX);
        return rLn;
    }

    private static double regularizedBeta(double pA, double pB, double pX) {
        if (pX <= 0) {
            return 0;
        }
        if (pX >= 1) {
            return 1;
        }
        double vFront = Math.exp(lnGamma(pA + pB) - lnGamma(pA) - lnGamma(pB)
                + pA * Math.log(pX) + pB * Math.log(1 - pX));
        double rBeta;
        if (pX < (pA + 1) / (pA + pB + 2)) {
            rBeta = vFront * continuedFraction(pA, pB, pX) / pA;
        } else {
            rBeta = 1 - vFront * continuedFraction(pB, pA, 1 - pX) / pB;
        }
        return rBeta;
    }

    private static double continuedFraction(double pA, double pB, double pX) {
        double vQab = pA + pB;
        double vQap = pA + 1;
        double vQam = pA - 1;
        double vC = 1;
        double vD = 1 - vQab * pX / vQap;
        if (Math.abs(vD) < TINY) {
            vD = TINY;
        }
        vD = 1 / vD;
        double rH = vD;
        for (int vStep = 1; vStep <= MAX_ITERATIONS; vStep++) {
            int vTwice = 2 * vStep;
            double vTerm = vStep * (pB - vStep) * pX / ((vQam + vTwice) * (pA + vTwice));
            vD = 1 + vTerm * vD;
            if (Math.abs(vD) < TINY) {
                vD = TINY;
            }
            vC = 1 + vTerm / vC;
            if (Math.abs(vC) < TINY) {
                vC = TINY;
            }
            vD = 1 / vD;
            rH *= vD * vC;
            vTerm = -(pA + vStep) * (vQab + vStep) * pX / ((pA + vTwice) * (vQap + vTwice));
            vD = 1 + vTerm * vD;
            if (Math.abs(vD) < TINY) {
                vD = TINY;
            }
            vC = 1 + vTerm / vC;
            if (Math.abs(vC) < TINY) {
                vC = TINY;
            }
            vD = 1 / vD;
            double vDelta = vD * vC;
            rH *= vDelta;
            if (Math.abs(vDelta - 1) < PRECISION) {
                break;
            }
        }
        return rH;
    }
}
