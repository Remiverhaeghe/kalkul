package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AnalyseTest {
    @Test
    void derivativeOfPolynomialAndSine() {
        assertEquals(6, Analyse.derivative(Expression.parse("x^2"), 3), 1e-5);
        assertEquals(1, Analyse.derivative(Expression.parse("sin(x)"), 0), 1e-8);
        assertEquals(Math.exp(2), Analyse.derivative(Expression.parse("exp(x)"), 2), 1e-4);
    }

    @Test
    void derivativeOfAConstantIsZero() {
        assertEquals(0, Analyse.derivative(Expression.parse("5"), 4), 1e-8);
    }

    @Test
    void integralOfPolynomialIsExactWithSimpson() {
        assertEquals(9, Analyse.integral(Expression.parse("x^2"), 0, 3), 1e-9);
    }

    @Test
    void integralOfSine() {
        assertEquals(2, Analyse.integral(Expression.parse("sin(x)"), 0, Math.PI), 1e-8);
    }

    @Test
    void reversedBoundsChangeTheSign() {
        assertEquals(-9, Analyse.integral(Expression.parse("x^2"), 3, 0), 1e-9);
    }

    @Test
    void oddNumberOfIntervalsIsAccepted() {
        assertEquals(9, Analyse.integral(Expression.parse("x^2"), 0, 3, 11), 1e-9);
    }

    @Test
    void integralNeedsEnoughIntervalsAndADefinedFunction() {
        assertThrows(IllegalArgumentException.class, () -> Analyse.integral(Expression.parse("x"), 0, 1, 1));
        assertThrows(ArithmeticException.class, () -> Analyse.integral(Expression.parse("1/x"), -1, 1));
    }
}
