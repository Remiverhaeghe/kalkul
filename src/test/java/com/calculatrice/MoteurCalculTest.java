package com.calculatrice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MoteurCalculTest {

    // Envoie les touches une par une, comme le ferait l'interface
    private static String type(String... pKeys) {
        MoteurCalcul vEngine = new MoteurCalcul();
        for (String vKey : pKeys) {
            vEngine.handleInput(vKey);
        }
        return vEngine.getDisplay();
    }

    @Test
    void basicOperations() {
        assertEquals("5", type("2", "+", "3", "="));
        assertEquals("4", type("9", "-", "5", "="));
        assertEquals("42", type("6", "*", "7", "="));
        assertEquals("3.5", type("7", "/", "2", "="));
    }

    @Test
    void operationsAreChainedFromLeftToRight() {
        assertEquals("20", type("2", "+", "3", "*", "4", "="));
    }

    @Test
    void divisionByZeroShowsAnError() {
        assertEquals(MoteurCalcul.ERREUR, type("5", "/", "0", "="));
    }

    @Test
    void typingAfterAnErrorStartsANewNumber() {
        assertEquals("4", type("5", "/", "0", "=", "4"));
    }

    @Test
    void enterKeyComputesLikeEquals() {
        assertEquals("5", type("2", "+", "3", "\n"));
        assertEquals("5", type("2", "+", "3", "\r"));
    }

    @Test
    void equalsWithoutOperatorKeepsTheValue() {
        assertEquals("7", type("7", "="));
    }

    @Test
    void decimalPointIsAddedOnlyOnce() {
        assertEquals("0.", type("."));
        assertEquals("1.5", type("1", ".", "5", "."));
    }

    @Test
    void leadingZerosAreDropped() {
        assertEquals("7", type("0", "0", "7"));
    }

    @Test
    void percentDividesByOneHundred() {
        assertEquals("0.5", type("5", "0", "%"));
    }

    @Test
    void plusMinusChangesTheSign() {
        assertEquals("-5", type("5", "±"));
        assertEquals("5", type("5", "±", "±"));
    }

    @Test
    void backspaceRemovesTheLastDigit() {
        assertEquals("12", type("1", "2", "3", "\b"));
        assertEquals("0", type("7", "\b"));
    }

    @Test
    void clearResetsEverything() {
        assertEquals("0", type("2", "+", "3", "C"));
        assertEquals("4", type("2", "+", "3", "C", "4", "="));
    }

    @Test
    void trimZerosKeepsIntegersWithoutDecimals() {
        assertEquals("12", MoteurCalcul.trimZeros(12.0));
        assertEquals("0.25", MoteurCalcul.trimZeros(0.25));
    }

    @Test
    void squareAndSquareRoot() {
        assertEquals("49", type("7", "x²"));
        assertEquals("3", type("9", "√"));
        assertEquals(MoteurCalcul.ERREUR, type("9", "±", "√"));
    }

    @Test
    void modGivesTheRemainderOfTheDivision() {
        assertEquals("1", type("7", "mod", "2", "="));
        assertEquals(MoteurCalcul.ERREUR, type("7", "mod", "0", "="));
    }

    @Test
    void powerRaisesToTheSecondNumber() {
        assertEquals("1024", type("2", "^", "1", "0", "="));
        assertEquals(MoteurCalcul.ERREUR, type("1", "0", "^", "9", "9", "9", "="));
    }

    @Test
    void trigonometryUsesDegreesByDefault() {
        assertEquals("0.5", type("3", "0", "sin"));
        assertEquals("1", type("0", "cos"));
        assertEquals("45", type("1", "atan"));
    }

    @Test
    void trigonometryCanUseRadians() {
        MoteurCalcul vEngine = new MoteurCalcul();
        vEngine.setDegrees(false);
        vEngine.handleInput("0");
        vEngine.handleInput("sin");
        assertEquals("0", vEngine.getDisplay());
    }

    @Test
    void exponentialAndLogarithms() {
        assertEquals("1", type("0", "exp"));
        assertEquals("2", type("1", "0", "0", "log"));
        assertEquals("0", type("1", "ln"));
        assertEquals(MoteurCalcul.ERREUR, type("0", "ln"));
    }

    @Test
    void factorial() {
        assertEquals("120", type("5", "n!"));
        assertEquals(MoteurCalcul.ERREUR, type("2", "1", "n!"));
    }

    @Test
    void roundingErrorsAreHiddenOnTheDisplay() {
        assertEquals("0.3", type("0", ".", "1", "+", "0", ".", "2", "="));
    }
}

