package com.calculatrice;

import java.util.Arrays;

/**
 * Fonctions mathématiques de la calculatrice scientifique, sans dépendance à Swing.
 * Une saisie impossible (racine d'un négatif, logarithme de 0...) lève une ArithmeticException.
 */
public final class FonctionsScientifiques {
    // Au-delà de 20, la factorielle dépasse la capacité d'un long
    private static final int FACTORIAL_MAX = 20;
    // Sous ce seuil, le cosinus est considéré nul : la tangente n'existe pas
    private static final double COSINE_EPSILON = 1e-12;

    private FonctionsScientifiques() {
    }

    /**
     * Applique une fonction à une valeur.
     *
     * @param pName    nom de la fonction : x², √, sin, cos, tan, asin, acos, atan, exp, ln, log ou n!
     * @param pValue   valeur de départ
     * @param pDegrees vrai si les angles sont en degrés, faux s'ils sont en radians
     * @return résultat de la fonction
     */
    public static double apply(String pName, double pValue, boolean pDegrees) {
        double rResult;
        switch (pName) {
            case "x²":
                rResult = pValue * pValue;
                break;
            case "√":
                if (pValue < 0) {
                    throw new ArithmeticException("Racine d'un nombre négatif");
                }
                rResult = Math.sqrt(pValue);
                break;
            case "sin":
                rResult = Math.sin(toRadians(pValue, pDegrees));
                break;
            case "cos":
                rResult = Math.cos(toRadians(pValue, pDegrees));
                break;
            case "tan":
                rResult = tangent(pValue, pDegrees);
                break;
            case "asin":
                checkUnitRange(pValue);
                rResult = fromRadians(Math.asin(pValue), pDegrees);
                break;
            case "acos":
                checkUnitRange(pValue);
                rResult = fromRadians(Math.acos(pValue), pDegrees);
                break;
            case "atan":
                rResult = fromRadians(Math.atan(pValue), pDegrees);
                break;
            case "exp":
                rResult = Math.exp(pValue);
                break;
            case "ln":
                checkPositive(pValue);
                rResult = Math.log(pValue);
                break;
            case "log":
                checkPositive(pValue);
                rResult = Math.log10(pValue);
                break;
            case "n!":
                rResult = factorial(pValue);
                break;
            default:
                throw new IllegalArgumentException("Fonction inconnue : " + pName);
        }
        return rResult;
    }

    /**
     * Factorielle d'un entier compris entre 0 et 20.
     *
     * @param pValue entier positif ou nul
     * @return n!
     */
    public static double factorial(double pValue) {
        if (pValue < 0 || pValue != Math.floor(pValue) || pValue > FACTORIAL_MAX) {
            throw new ArithmeticException("Factorielle définie pour un entier de 0 à " + FACTORIAL_MAX);
        }
        long vProduct = 1;
        for (long vFactor = 2; vFactor <= (long) pValue; vFactor++) {
            vProduct *= vFactor;
        }
        double rResult = vProduct;
        return rResult;
    }

    /**
     * Solutions réelles de ax² + bx + c = 0, classées par ordre croissant.
     *
     * @return aucune, une ou deux solutions
     */
    public static double[] solveQuadratic(double pA, double pB, double pC) {
        if (pA == 0) {
            throw new IllegalArgumentException("Le coefficient a ne doit pas être nul");
        }
        double vDelta = pB * pB - 4 * pA * pC;
        double[] rSolutions;
        if (vDelta < 0) {
            rSolutions = new double[0];
        } else if (vDelta == 0) {
            rSolutions = new double[] {-pB / (2 * pA)};
        } else {
            double vRoot = Math.sqrt(vDelta);
            rSolutions = new double[] {(-pB - vRoot) / (2 * pA), (-pB + vRoot) / (2 * pA)};
            Arrays.sort(rSolutions);
        }
        return rSolutions;
    }

    public static double mean(double[] pValues) {
        checkNotEmpty(pValues);
        double vSum = 0;
        for (double vValue : pValues) {
            vSum += vValue;
        }
        double rMean = vSum / pValues.length;
        return rMean;
    }

    /**
     * Variance de population : moyenne des carrés des écarts à la moyenne.
     */
    public static double variance(double[] pValues) {
        double vMean = mean(pValues);
        double vSum = 0;
        for (double vValue : pValues) {
            vSum += (vValue - vMean) * (vValue - vMean);
        }
        double rVariance = vSum / pValues.length;
        return rVariance;
    }

    public static double standardDeviation(double[] pValues) {
        double rDeviation = Math.sqrt(variance(pValues));
        return rDeviation;
    }

    /**
     * Lit des nombres séparés par des espaces ou des points-virgules.
     * La virgule est acceptée comme séparateur décimal.
     */
    public static double[] parseNumbers(String pText) {
        String vClean = pText.trim().replace(',', '.');
        if (vClean.isEmpty()) {
            throw new IllegalArgumentException("Aucun nombre saisi");
        }
        String[] vParts = vClean.split("[\\s;]+");
        double[] rValues = new double[vParts.length];
        for (int vIndex = 0; vIndex < vParts.length; vIndex++) {
            try {
                rValues[vIndex] = Double.parseDouble(vParts[vIndex]);
            } catch (NumberFormatException vException) {
                throw new IllegalArgumentException("Saisie invalide : " + vParts[vIndex]);
            }
        }
        return rValues;
    }

    private static double tangent(double pValue, boolean pDegrees) {
        double vRadians = toRadians(pValue, pDegrees);
        if (Math.abs(Math.cos(vRadians)) < COSINE_EPSILON) {
            throw new ArithmeticException("Tangente non définie pour cet angle");
        }
        double rResult = Math.tan(vRadians);
        return rResult;
    }

    private static double toRadians(double pValue, boolean pDegrees) {
        double rRadians = pDegrees ? Math.toRadians(pValue) : pValue;
        return rRadians;
    }

    private static double fromRadians(double pRadians, boolean pDegrees) {
        double rValue = pDegrees ? Math.toDegrees(pRadians) : pRadians;
        return rValue;
    }

    private static void checkUnitRange(double pValue) {
        if (pValue < -1 || pValue > 1) {
            throw new ArithmeticException("Valeur hors de l'intervalle [-1 ; 1]");
        }
    }

    private static void checkPositive(double pValue) {
        if (pValue <= 0) {
            throw new ArithmeticException("Le logarithme demande un nombre strictement positif");
        }
    }

    private static void checkNotEmpty(double[] pValues) {
        if (pValues.length == 0) {
            throw new IllegalArgumentException("Aucun nombre saisi");
        }
    }
}
