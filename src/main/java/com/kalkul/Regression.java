package com.kalkul;

/**
 * Régression linéaire y = a·x + b par moindres carrés, avec corrélation et test de significativité de la pente.
 */
public final class Regression {
    private final int aCount;
    private final double aSlope;
    private final double aIntercept;
    private final double aCorrelation;
    private final double aPValue;

    private Regression(int pCount, double pSlope, double pIntercept, double pCorrelation, double pPValue) {
        aCount = pCount;
        aSlope = pSlope;
        aIntercept = pIntercept;
        aCorrelation = pCorrelation;
        aPValue = pPValue;
    }

    /**
     * Ajuste une droite sur les points (x, y). Il faut au moins deux points et des x qui ne sont pas tous égaux.
     */
    public static Regression fit(double[] pX, double[] pY) {
        if (pX.length != pY.length) {
            throw new IllegalArgumentException("Il faut autant de x que de y");
        }
        if (pX.length < 2) {
            throw new IllegalArgumentException("Il faut au moins deux points");
        }
        int vCount = pX.length;
        double vMeanX = FonctionsScientifiques.mean(pX);
        double vMeanY = FonctionsScientifiques.mean(pY);
        double vSxx = 0;
        double vSyy = 0;
        double vSxy = 0;
        for (int vIndex = 0; vIndex < vCount; vIndex++) {
            vSxx += (pX[vIndex] - vMeanX) * (pX[vIndex] - vMeanX);
            vSyy += (pY[vIndex] - vMeanY) * (pY[vIndex] - vMeanY);
            vSxy += (pX[vIndex] - vMeanX) * (pY[vIndex] - vMeanY);
        }
        if (vSxx == 0) {
            throw new ArithmeticException("Tous les x sont identiques : pas de droite de régression");
        }
        double vSlope = vSxy / vSxx;
        double vIntercept = vMeanY - vSlope * vMeanX;
        // Si tous les y sont égaux, la droite est horizontale et la corrélation n'est pas définie
        double vCorrelation = vSyy == 0 ? Double.NaN : vSxy / Math.sqrt(vSxx * vSyy);
        double vPValue = slopePValue(vCorrelation, vCount);
        Regression rFit = new Regression(vCount, vSlope, vIntercept, vCorrelation, vPValue);
        return rFit;
    }

    // Test bilatéral de l'hypothèse « la pente est nulle » : t = r·√(n-2) / √(1-r²), n-2 degrés de liberté
    private static double slopePValue(double pCorrelation, int pCount) {
        int vDegrees = pCount - 2;
        double rPValue;
        if (vDegrees < 1 || Double.isNaN(pCorrelation)) {
            rPValue = Double.NaN;
        } else if (Math.abs(pCorrelation) >= 1) {
            rPValue = 0;
        } else {
            double vT = pCorrelation * Math.sqrt(vDegrees / (1 - pCorrelation * pCorrelation));
            rPValue = StudentT.twoSidedPValue(vT, vDegrees);
        }
        return rPValue;
    }

    /**
     * Lit des couples « x y », un par ligne (ou séparés par « ; »).
     *
     * @return deux tableaux : les x puis les y
     */
    public static double[][] parsePairs(String pText) {
        String[] vLines = pText.trim().split("[\\n;]+");
        double[] vX = new double[vLines.length];
        double[] vY = new double[vLines.length];
        for (int vIndex = 0; vIndex < vLines.length; vIndex++) {
            double[] vPair = FonctionsScientifiques.parseNumbers(vLines[vIndex]);
            if (vPair.length != 2) {
                throw new IllegalArgumentException("Chaque ligne doit contenir deux nombres : " + vLines[vIndex].trim());
            }
            vX[vIndex] = vPair[0];
            vY[vIndex] = vPair[1];
        }
        double[][] rPairs = {vX, vY};
        return rPairs;
    }

    public int count() {
        return aCount;
    }

    public double slope() {
        return aSlope;
    }

    public double intercept() {
        return aIntercept;
    }

    /**
     * Coefficient de corrélation r, ou NaN si tous les y sont égaux.
     */
    public double correlation() {
        return aCorrelation;
    }

    public double determination() {
        double rDetermination = aCorrelation * aCorrelation;
        return rDetermination;
    }

    /**
     * Probabilité d'observer une telle pente si les deux séries n'avaient aucun lien. Sous 0,05, la pente
     * est dite significative. NaN avec deux points seulement ou des y tous égaux.
     */
    public double pValue() {
        return aPValue;
    }

    public double predict(double pX) {
        double rY = aSlope * pX + aIntercept;
        return rY;
    }
}
