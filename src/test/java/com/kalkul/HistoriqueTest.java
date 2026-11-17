package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class HistoriqueTest {

    private static MoteurCalcul type(String... pKeys) {
        MoteurCalcul rEngine = new MoteurCalcul();
        for (String vKey : pKeys) {
            rEngine.handleInput(vKey);
        }
        return rEngine;
    }

    @Test
    void eachFinishedCalculationIsKeptMostRecentFirst() {
        MoteurCalcul vEngine = type("1", "2", "*", "3", "=", "9", "√");
        assertEquals(List.of(new MoteurCalcul.Calcul("√9", "3"), new MoteurCalcul.Calcul("12 × 3", "36")),
                vEngine.getHistorique());
    }

    @Test
    void functionsAreWrittenTheUsualWay() {
        assertEquals("3²", MoteurCalcul.unaryExpression("x²", "3"));
        assertEquals("5!", MoteurCalcul.unaryExpression("n!", "5"));
        assertEquals("sin(30)", MoteurCalcul.unaryExpression("sin", "30"));
        assertEquals("sin(30)", type("3", "0", "sin").getHistorique().get(0).expression());
    }

    @Test
    void errorsAndUnfinishedCalculationsAreNotKept() {
        assertTrue(type("5", "/", "0", "=").getHistorique().isEmpty());
        assertTrue(type("5", "+", "2").getHistorique().isEmpty());
        assertTrue(type("=").getHistorique().isEmpty());
    }

    @Test
    void lastCalculationOnlyRightAfterIt() {
        MoteurCalcul vEngine = type("2", "+", "2", "=");
        assertEquals(new MoteurCalcul.Calcul("2 + 2", "4"), vEngine.getDernierCalcul());
        vEngine.handleInput("7");
        assertNull(vEngine.getDernierCalcul());
    }

    @Test
    void historyIsCapped() {
        MoteurCalcul vEngine = new MoteurCalcul();
        for (int vIndex = 0; vIndex < MoteurCalcul.HISTORIQUE_MAX + 5; vIndex++) {
            vEngine.handleInput("1");
            vEngine.handleInput("x²");
        }
        assertEquals(MoteurCalcul.HISTORIQUE_MAX, vEngine.getHistorique().size());
        vEngine.effacerHistorique();
        assertTrue(vEngine.getHistorique().isEmpty());
    }

    @Test
    void aResultCanBeReused() {
        MoteurCalcul vEngine = type("6", "*", "7", "=", "C");
        vEngine.reprendre(vEngine.getHistorique().get(0).resultat());
        vEngine.handleInput("+");
        vEngine.handleInput("1");
        vEngine.handleInput("=");
        assertEquals("43", vEngine.getDisplay());
        // Un texte qui n'est pas un nombre ne change pas l'écran
        vEngine.reprendre("ERREUR");
        assertEquals("43", vEngine.getDisplay());
    }

    @Test
    void subtractionShowsTheMinusSign() {
        assertEquals("9 − 4", type("9", "-", "4", "=").getHistorique().get(0).expression());
    }
}
