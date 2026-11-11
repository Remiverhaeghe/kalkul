package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ExpressionTest {
    private static final double DELTA = 1e-9;

    private static double at(String pFormula, double pX) {
        return Expression.parse(pFormula).evaluate(pX);
    }

    @Test
    void basicArithmeticFollowsPriorities() {
        assertEquals(7, at("1 + 2 * 3", 0), DELTA);
        assertEquals(9, at("(1 + 2) * 3", 0), DELTA);
        assertEquals(2.5, at("10 / 4", 0), DELTA);
    }

    @Test
    void variableAndImplicitMultiplication() {
        assertEquals(7, at("2x + 1", 3), DELTA);
        assertEquals(6, at("x(x + 1)", 2), DELTA);
        assertEquals(2 * Math.sin(1), at("2sin(x)", 1), DELTA);
    }

    @Test
    void powerIsRightAssociativeAndBindsTighterThanMinus() {
        assertEquals(512, at("2^3^2", 0), DELTA);
        assertEquals(-9, at("-x^2", 3), DELTA);
        assertEquals(9, at("(-x)^2", 3), DELTA);
    }

    @Test
    void constantsAndFunctions() {
        assertEquals(1, at("sin(pi/2)", 0), DELTA);
        assertEquals(Math.E, at("e", 0), DELTA);
        assertEquals(7, at("sqrt(16) + abs(-3)", 0), DELTA);
        assertEquals(2, at("log(100)", 0), DELTA);
        assertEquals(1, at("ln(e)", 0), DELTA);
    }

    @Test
    void namedParameters() {
        Expression vFormula = Expression.parse("a*x + b");
        Map<String, Double> vValues = new HashMap<>();
        vValues.put("a", 2.0);
        vValues.put("b", 1.0);
        vValues.put("x", 3.0);
        assertEquals(7, vFormula.evaluate(vValues), DELTA);
        assertEquals(Arrays.asList("a", "x", "b"), Arrays.asList(vFormula.variables().toArray()));
    }

    @Test
    void parametersAreReadFromText() {
        Map<String, Double> vParameters = Expression.parseParameters("a=2 b=1,5;c=-3");
        assertEquals(2, vParameters.get("a"), DELTA);
        assertEquals(1.5, vParameters.get("b"), DELTA);
        assertEquals(-3, vParameters.get("c"), DELTA);
        assertTrue(Expression.parseParameters("  ").isEmpty());
    }

    @Test
    void invalidParametersAreRefused() {
        assertThrows(IllegalArgumentException.class, () -> Expression.parseParameters("a"));
        assertThrows(IllegalArgumentException.class, () -> Expression.parseParameters("a=abc"));
    }

    @Test
    void missingValueIsReported() {
        assertThrows(IllegalArgumentException.class, () -> Expression.parse("a*x").evaluate(1));
    }

    @Test
    void invalidFormulasAreRefused() {
        assertThrows(IllegalArgumentException.class, () -> Expression.parse(""));
        assertThrows(IllegalArgumentException.class, () -> Expression.parse("2 +"));
        assertThrows(IllegalArgumentException.class, () -> Expression.parse("(1 + 2"));
        assertThrows(IllegalArgumentException.class, () -> Expression.parse("1 + 2)"));
        assertThrows(IllegalArgumentException.class, () -> Expression.parse("3 $ 4"));
        assertThrows(IllegalArgumentException.class, () -> Expression.parse("1..2"));
    }

    @Test
    void divisionByZeroGivesInfinityWithoutFailing() {
        assertTrue(Double.isInfinite(at("1/x", 0)));
        assertTrue(Double.isNaN(at("ln(x)", -1)));
    }
}
