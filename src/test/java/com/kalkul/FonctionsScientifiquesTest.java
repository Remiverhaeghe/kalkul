package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class FonctionsScientifiquesTest {
    private static final double DELTA = 1e-9;

    @Test
    void trigonometricFunctionsInDegrees() {
        assertEquals(0.5, FonctionsScientifiques.apply("sin", 30, true), DELTA);
        assertEquals(0.5, FonctionsScientifiques.apply("cos", 60, true), DELTA);
        assertEquals(1, FonctionsScientifiques.apply("tan", 45, true), DELTA);
    }

    @Test
    void inverseFunctionsReturnDegrees() {
        assertEquals(30, FonctionsScientifiques.apply("asin", 0.5, true), DELTA);
        assertEquals(60, FonctionsScientifiques.apply("acos", 0.5, true), DELTA);
        assertEquals(Math.PI / 2, FonctionsScientifiques.apply("asin", 1, false), DELTA);
    }

    @Test
    void tangentIsUndefinedAtNinetyDegrees() {
        assertThrows(ArithmeticException.class, () -> FonctionsScientifiques.apply("tan", 90, true));
    }

    @Test
    void inverseSineAndCosineNeedAValueBetweenMinusOneAndOne() {
        assertThrows(ArithmeticException.class, () -> FonctionsScientifiques.apply("asin", 2, true));
        assertThrows(ArithmeticException.class, () -> FonctionsScientifiques.apply("acos", -2, true));
    }

    @Test
    void logarithmsNeedAPositiveNumber() {
        assertThrows(ArithmeticException.class, () -> FonctionsScientifiques.apply("ln", 0, true));
        assertThrows(ArithmeticException.class, () -> FonctionsScientifiques.apply("log", -1, true));
    }

    @Test
    void unknownFunctionIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> FonctionsScientifiques.apply("foo", 1, true));
    }

    @Test
    void factorialOfSmallIntegers() {
        assertEquals(1, FonctionsScientifiques.factorial(0), DELTA);
        assertEquals(120, FonctionsScientifiques.factorial(5), DELTA);
        assertEquals(2432902008176640000.0, FonctionsScientifiques.factorial(20), 1);
    }

    @Test
    void factorialRefusesNegativeFractionalAndTooLargeValues() {
        assertThrows(ArithmeticException.class, () -> FonctionsScientifiques.factorial(-1));
        assertThrows(ArithmeticException.class, () -> FonctionsScientifiques.factorial(2.5));
        assertThrows(ArithmeticException.class, () -> FonctionsScientifiques.factorial(21));
    }

    @Test
    void quadraticWithTwoOneOrNoSolution() {
        assertArrayEquals(new double[] {1, 2}, FonctionsScientifiques.solveQuadratic(1, -3, 2), DELTA);
        assertArrayEquals(new double[] {-1}, FonctionsScientifiques.solveQuadratic(1, 2, 1), DELTA);
        assertEquals(0, FonctionsScientifiques.solveQuadratic(1, 0, 1).length);
    }

    @Test
    void quadraticSolutionsAreSortedWhenAIsNegative() {
        assertArrayEquals(new double[] {1, 2}, FonctionsScientifiques.solveQuadratic(-1, 3, -2), DELTA);
    }

    @Test
    void quadraticNeedsANonZeroA() {
        assertThrows(IllegalArgumentException.class, () -> FonctionsScientifiques.solveQuadratic(0, 1, 1));
    }

    @Test
    void basicStatistics() {
        double[] vValues = {2, 4, 4, 4, 5, 5, 7, 9};
        assertEquals(5, FonctionsScientifiques.mean(vValues), DELTA);
        assertEquals(4, FonctionsScientifiques.variance(vValues), DELTA);
        assertEquals(2, FonctionsScientifiques.standardDeviation(vValues), DELTA);
    }

    @Test
    void statisticsNeedAtLeastOneValue() {
        assertThrows(IllegalArgumentException.class, () -> FonctionsScientifiques.mean(new double[0]));
    }

    @Test
    void numbersAreReadWithSpacesSemicolonsAndDecimalCommas() {
        assertArrayEquals(new double[] {1.5, 2, 3},
                FonctionsScientifiques.parseNumbers(" 1,5 2;3 "), DELTA);
    }

    @Test
    void invalidOrEmptyInputIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> FonctionsScientifiques.parseNumbers("1 abc"));
        assertThrows(IllegalArgumentException.class, () -> FonctionsScientifiques.parseNumbers("   "));
    }
}
