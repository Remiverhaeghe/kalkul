package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EchantillonCourbeTest {
    @Test
    void pointsCoverTheIntervalEvenly() {
        EchantillonCourbe vSample = EchantillonCourbe.sample(x -> 2 * x, 0, 10, 11);
        assertEquals(11, vSample.size());
        assertEquals(0, vSample.x(0), 1e-12);
        assertEquals(10, vSample.xMax(), 1e-12);
        assertEquals(6, vSample.y(3), 1e-12);
    }

    @Test
    void verticalRangeKeepsAMarginAroundTheCurve() {
        EchantillonCourbe vSample = EchantillonCourbe.sample(x -> x, 0, 10, 11);
        assertTrue(vSample.yMin() < 0);
        assertTrue(vSample.yMax() > 10);
    }

    @Test
    void constantCurveGetsANonEmptyRange() {
        EchantillonCourbe vSample = EchantillonCourbe.sample(x -> 5, 0, 1, 5);
        assertTrue(vSample.yMax() - vSample.yMin() > 1);
    }

    @Test
    void asymptotesAndUndefinedValuesDoNotStretchTheRange() {
        EchantillonCourbe vSample = EchantillonCourbe.sample(x -> 1 / x, -1, 1, 21);
        assertTrue(vSample.yMax() < 100);
        assertFalse(EchantillonCourbe.isUsable(Double.NaN));
        assertFalse(EchantillonCourbe.isUsable(Double.POSITIVE_INFINITY));
        assertFalse(EchantillonCourbe.isUsable(1e9));
    }

    @Test
    void invalidIntervalOrNowhereDefinedFunctionIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> EchantillonCourbe.sample(x -> x, 5, 5, 10));
        assertThrows(IllegalArgumentException.class, () -> EchantillonCourbe.sample(x -> x, 0, 1, 1));
        assertThrows(ArithmeticException.class, () -> EchantillonCourbe.sample(x -> Math.sqrt(x), -5, -1, 10));
    }

    @Test
    void niceStepIsOneTwoOrFiveTimesAPowerOfTen() {
        assertEquals(2, EchantillonCourbe.niceStep(20), 1e-12);
        assertEquals(1, EchantillonCourbe.niceStep(10), 1e-12);
        assertEquals(10, EchantillonCourbe.niceStep(100), 1e-12);
        assertEquals(0.5, EchantillonCourbe.niceStep(4), 1e-12);
    }
}
