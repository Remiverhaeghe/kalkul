package com.calculatrice;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class FormulesFinancieresTest {
    private static final double DELTA = 1e-6;
    private static final double CENT = 0.01;

    @Test
    void simpleInterestGrowsLinearly() {
        assertEquals(150, FormulesFinancieres.simpleInterest(1000, 5, 3), DELTA);
    }

    @Test
    void compoundInterestCapitalisesEveryYear() {
        assertEquals(1102.5, FormulesFinancieres.compoundAmount(1000, 5, 2, 1), DELTA);
    }

    @Test
    void moreCapitalisationsGiveMoreInterest() {
        double vYearly = FormulesFinancieres.compoundAmount(1000, 5, 2, 1);
        double vMonthly = FormulesFinancieres.compoundAmount(1000, 5, 2, 12);
        assertEquals(true, vMonthly > vYearly);
    }

    @Test
    void compoundInterestNeedsAtLeastOnePeriodPerYear() {
        assertThrows(IllegalArgumentException.class, () -> FormulesFinancieres.compoundAmount(1000, 5, 2, 0));
    }

    @Test
    void loanPayment() {
        assertEquals(860.66, FormulesFinancieres.loanPayment(10000, 6, 12), CENT);
    }

    @Test
    void interestFreeLoanIsSplitEvenly() {
        assertEquals(100, FormulesFinancieres.loanPayment(1200, 0, 12), DELTA);
    }

    @Test
    void loanNeedsAtLeastOneMonth() {
        assertThrows(IllegalArgumentException.class, () -> FormulesFinancieres.loanPayment(1000, 5, 0));
    }

    @Test
    void amortizationRepaysTheWholeCapital() {
        double[][] vRows = FormulesFinancieres.amortization(10000, 6, 12);
        double vCapitalPaid = 0;
        for (double[] vRow : vRows) {
            vCapitalPaid += vRow[3];
        }
        assertEquals(12, vRows.length);
        assertEquals(50, vRows[0][2], CENT);
        assertEquals(10000, vCapitalPaid, CENT);
        assertEquals(0, vRows[11][4], CENT);
    }

    @Test
    void netPresentValue() {
        assertEquals(-49.5868, FormulesFinancieres.npv(10, new double[] {-1000, 500, 600}), 1e-3);
    }

    @Test
    void internalRateOfReturn() {
        assertEquals(0.1, FormulesFinancieres.irr(new double[] {-100, 110}), 1e-6);
    }

    @Test
    void internalRateOfReturnCancelsTheNetPresentValue() {
        double[] vFlows = {-1000, 500, 600};
        double vRate = FormulesFinancieres.irr(vFlows);
        assertEquals(0, FormulesFinancieres.npv(vRate * 100, vFlows), 1e-4);
    }

    @Test
    void internalRateOfReturnNeedsASignChange() {
        assertThrows(ArithmeticException.class, () -> FormulesFinancieres.irr(new double[] {100, 50}));
    }

    @Test
    void cumulativeCashFlowAndPayback() {
        double[] vFlows = {-100, 50, 60};
        assertArrayEquals(new double[] {-100, -50, 10}, FormulesFinancieres.cumulative(vFlows), DELTA);
        assertEquals(2, FormulesFinancieres.paybackPeriod(vFlows));
        assertEquals(-1, FormulesFinancieres.paybackPeriod(new double[] {-100, 10}));
    }

    @Test
    void emptyCashFlowsAreRefused() {
        assertThrows(IllegalArgumentException.class, () -> FormulesFinancieres.npv(5, new double[0]));
    }

    @Test
    void currencyConversion() {
        assertEquals(108, FormulesFinancieres.convertCurrency(100, 1.08), DELTA);
        assertThrows(IllegalArgumentException.class, () -> FormulesFinancieres.convertCurrency(100, 0));
    }
}
