package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RegressionTest {
    private static final double DELTA = 1e-9;

    @Test
    void perfectLine() {
        Regression vFit = Regression.fit(new double[] {1, 2, 3, 4}, new double[] {3, 5, 7, 9});
        assertEquals(2, vFit.slope(), DELTA);
        assertEquals(1, vFit.intercept(), DELTA);
        assertEquals(1, vFit.correlation(), DELTA);
        assertEquals(1, vFit.determination(), DELTA);
        assertEquals(0, vFit.pValue(), DELTA);
        assertEquals(11, vFit.predict(5), DELTA);
        assertEquals(4, vFit.count());
    }

    @Test
    void noisyLine() {
        // Valeurs vérifiées à la main : x = 1..5, y = 2 4 5 4 5 -> pente 0,6, ordonnée 2,2, r = 0,7746
        Regression vFit = Regression.fit(new double[] {1, 2, 3, 4, 5}, new double[] {2, 4, 5, 4, 5});
        assertEquals(0.6, vFit.slope(), DELTA);
        assertEquals(2.2, vFit.intercept(), DELTA);
        assertEquals(0.7745966692, vFit.correlation(), 1e-9);
        assertEquals(0.6, vFit.determination(), 1e-9);
    }

    @Test
    void negativeCorrelation() {
        Regression vFit = Regression.fit(new double[] {1, 2, 3}, new double[] {6, 4, 2});
        assertEquals(-2, vFit.slope(), DELTA);
        assertEquals(-1, vFit.correlation(), DELTA);
    }

    @Test
    void pValueOfAStrongTrendIsSmallAndOfNoiseIsLarge() {
        Regression vStrong = Regression.fit(new double[] {1, 2, 3, 4, 5, 6, 7, 8},
                new double[] {1.1, 2.0, 2.9, 4.2, 5.0, 6.1, 6.9, 8.1});
        assertTrue(vStrong.pValue() < 0.001);
        Regression vNoise = Regression.fit(new double[] {1, 2, 3, 4, 5}, new double[] {3, 1, 4, 1, 5});
        assertTrue(vNoise.pValue() > 0.05);
    }

    @Test
    void pValueMatchesKnownValues() {
        // Pour 2 degrés de liberté : p = 1 - |t| / sqrt(t² + 2). Pour 1 degré : p = 1 - (2/pi)·atan(|t|).
        assertEquals(1 - 2 / Math.sqrt(6), StudentT.twoSidedPValue(2, 2), 1e-8);
        assertEquals(0.5, StudentT.twoSidedPValue(1, 1), 1e-8);
        assertEquals(1, StudentT.twoSidedPValue(0, 5), 1e-8);
        // Table classique : t = 2,228 avec 10 degrés de liberté donne p = 0,05
        assertEquals(0.05, StudentT.twoSidedPValue(2.228, 10), 1e-3);
        // t = 2,0 avec 30 degrés de liberté donne p = 0,0546
        assertEquals(0.0546, StudentT.twoSidedPValue(2.0, 30), 1e-3);
    }

    @Test
    void pValueIsUndefinedWithTwoPointsOrFlatY() {
        assertTrue(Double.isNaN(Regression.fit(new double[] {1, 2}, new double[] {1, 3}).pValue()));
        Regression vFlat = Regression.fit(new double[] {1, 2, 3}, new double[] {4, 4, 4});
        assertEquals(0, vFlat.slope(), DELTA);
        assertTrue(Double.isNaN(vFlat.correlation()));
    }

    @Test
    void invalidDataIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> Regression.fit(new double[] {1}, new double[] {1}));
        assertThrows(IllegalArgumentException.class, () -> Regression.fit(new double[] {1, 2}, new double[] {1}));
        assertThrows(ArithmeticException.class, () -> Regression.fit(new double[] {2, 2, 2}, new double[] {1, 2, 3}));
    }

    @Test
    void pairsAreReadOnePerLine() {
        double[][] vPairs = Regression.parsePairs("1 2\n3 4;5,5 6");
        assertEquals(3, vPairs[0].length);
        assertEquals(5.5, vPairs[0][2], DELTA);
        assertEquals(6, vPairs[1][2], DELTA);
        assertThrows(IllegalArgumentException.class, () -> Regression.parsePairs("1 2 3"));
    }
}
