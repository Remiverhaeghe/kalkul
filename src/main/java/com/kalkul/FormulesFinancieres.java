package com.kalkul;

/**
 * Formules financières de la calculatrice, sans dépendance à Swing.
 * Les taux sont donnés en pourcentage (5 pour 5 %), sauf le résultat de {@link #irr(double[])} qui est une fraction.
 */
public final class FormulesFinancieres {
    private static final int IRR_MAX_ITERATIONS = 200;
    private static final double IRR_PRECISION = 1e-10;
    // Un taux ne peut pas descendre sous -100 % ; la recherche part de -99 %
    private static final double IRR_LOWER_BOUND = -0.99;
    private static final double IRR_UPPER_BOUND = 100;

    private FormulesFinancieres() {
    }

    /**
     * Intérêts simples : capital x taux x durée.
     */
    public static double simpleInterest(double pPrincipal, double pRatePercent, double pYears) {
        double rInterest = pPrincipal * pRatePercent / 100 * pYears;
        return rInterest;
    }

    /**
     * Capital final avec intérêts composés.
     *
     * @param pPeriodsPerYear nombre de capitalisations par an (1 annuel, 12 mensuel)
     */
    public static double compoundAmount(double pPrincipal, double pRatePercent, double pYears, int pPeriodsPerYear) {
        if (pPeriodsPerYear < 1) {
            throw new IllegalArgumentException("Il faut au moins une capitalisation par an");
        }
        double vPeriodRate = pRatePercent / 100 / pPeriodsPerYear;
        double rAmount = pPrincipal * Math.pow(1 + vPeriodRate, pPeriodsPerYear * pYears);
        return rAmount;
    }

    /**
     * Mensualité constante d'un prêt.
     *
     * @param pAnnualRatePercent taux annuel en pourcentage
     * @param pMonths            durée en mois
     */
    public static double loanPayment(double pPrincipal, double pAnnualRatePercent, int pMonths) {
        if (pMonths < 1) {
            throw new IllegalArgumentException("La durée doit être d'au moins un mois");
        }
        double vMonthlyRate = pAnnualRatePercent / 100 / 12;
        double rPayment;
        if (vMonthlyRate == 0) {
            rPayment = pPrincipal / pMonths;
        } else {
            rPayment = pPrincipal * vMonthlyRate / (1 - Math.pow(1 + vMonthlyRate, -pMonths));
        }
        return rPayment;
    }

    /**
     * Tableau d'amortissement d'un prêt.
     *
     * @return une ligne par mois : mois, mensualité, intérêts, capital remboursé, capital restant dû
     */
    public static double[][] amortization(double pPrincipal, double pAnnualRatePercent, int pMonths) {
        double vPayment = loanPayment(pPrincipal, pAnnualRatePercent, pMonths);
        double vMonthlyRate = pAnnualRatePercent / 100 / 12;
        double[][] rRows = new double[pMonths][5];
        double vRemaining = pPrincipal;
        for (int vMonth = 1; vMonth <= pMonths; vMonth++) {
            double vInterest = vRemaining * vMonthlyRate;
            double vCapital = vPayment - vInterest;
            vRemaining -= vCapital;
            rRows[vMonth - 1] = new double[] {vMonth, vPayment, vInterest, vCapital, vRemaining};
        }
        return rRows;
    }

    /**
     * Valeur actuelle nette. Le premier flux est à la date 0 (investissement initial, en général négatif).
     */
    public static double npv(double pRatePercent, double[] pCashFlows) {
        checkNotEmpty(pCashFlows);
        double vRate = pRatePercent / 100;
        double rNpv = 0;
        for (int vPeriod = 0; vPeriod < pCashFlows.length; vPeriod++) {
            rNpv += pCashFlows[vPeriod] / Math.pow(1 + vRate, vPeriod);
        }
        return rNpv;
    }

    /**
     * Taux de rentabilité interne : taux qui annule la valeur actuelle nette, en fraction (0,1 pour 10 %).
     * Cherche par dichotomie ; échoue si les flux ne changent pas de signe.
     */
    public static double irr(double[] pCashFlows) {
        checkNotEmpty(pCashFlows);
        double vLow = IRR_LOWER_BOUND;
        double vHigh = IRR_UPPER_BOUND;
        double vNpvLow = npv(vLow * 100, pCashFlows);
        double vNpvHigh = npv(vHigh * 100, pCashFlows);
        if (vNpvLow * vNpvHigh > 0) {
            throw new ArithmeticException("TRI introuvable : les flux doivent changer de signe");
        }
        for (int vStep = 0; vStep < IRR_MAX_ITERATIONS; vStep++) {
            double vMiddle = (vLow + vHigh) / 2;
            double vNpvMiddle = npv(vMiddle * 100, pCashFlows);
            if (Math.abs(vNpvMiddle) < IRR_PRECISION || (vHigh - vLow) < IRR_PRECISION) {
                return vMiddle;
            }
            if (vNpvLow * vNpvMiddle < 0) {
                vHigh = vMiddle;
            } else {
                vLow = vMiddle;
                vNpvLow = vNpvMiddle;
            }
        }
        double rIrr = (vLow + vHigh) / 2;
        return rIrr;
    }

    /**
     * Trésorerie cumulée à la fin de chaque période.
     */
    public static double[] cumulative(double[] pCashFlows) {
        checkNotEmpty(pCashFlows);
        double[] rTotals = new double[pCashFlows.length];
        double vSum = 0;
        for (int vPeriod = 0; vPeriod < pCashFlows.length; vPeriod++) {
            vSum += pCashFlows[vPeriod];
            rTotals[vPeriod] = vSum;
        }
        return rTotals;
    }

    /**
     * Première période où la trésorerie cumulée devient positive ou nulle, ou -1 si elle ne l'est jamais.
     */
    public static int paybackPeriod(double[] pCashFlows) {
        double[] vTotals = cumulative(pCashFlows);
        int rPeriod = -1;
        for (int vPeriod = 0; vPeriod < vTotals.length && rPeriod < 0; vPeriod++) {
            if (vTotals[vPeriod] >= 0) {
                rPeriod = vPeriod;
            }
        }
        return rPeriod;
    }

    /**
     * Conversion monétaire avec un taux saisi : 1 unité de départ vaut pRate unités d'arrivée.
     */
    public static double convertCurrency(double pAmount, double pRate) {
        if (pRate <= 0) {
            throw new IllegalArgumentException("Le taux de change doit être strictement positif");
        }
        double rConverted = pAmount * pRate;
        return rConverted;
    }

    private static void checkNotEmpty(double[] pCashFlows) {
        if (pCashFlows.length == 0) {
            throw new IllegalArgumentException("Aucun flux saisi");
        }
    }
}
