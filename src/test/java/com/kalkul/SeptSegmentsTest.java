package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SeptSegmentsTest {

    @Test
    void digitsLightTheirUsualSegments() {
        assertEquals(127, SeptSegments.forme('8'));
        assertEquals(2 | 4, SeptSegments.forme('1'));
        assertEquals(64, SeptSegments.forme('-'));
        assertEquals(-1, SeptSegments.forme('+'));
    }

    @Test
    void numbersAndErrorAreWrittenInSegmentsOtherTextsKeepTheFont() {
        assertTrue(SeptSegments.sait("-12.5"));
        assertTrue(SeptSegments.sait("1.5E10"));
        assertTrue(SeptSegments.sait(MoteurCalcul.ERREUR));
        assertTrue(SeptSegments.sait("indéfini"));
        assertFalse(SeptSegments.sait("1.5E+10"));
        assertFalse(SeptSegments.sait(""));
    }

    @Test
    void decimalPointTakesNoCellExceptInFront() {
        assertEquals(3, SeptSegments.cases("58.5"));
        assertEquals(2, SeptSegments.cases(".5"));
        assertEquals(30 * (3 * SeptSegments.CASE + 2 * SeptSegments.ECART), SeptSegments.largeur("58.5", 30), 1e-9);
        assertEquals(0, SeptSegments.largeur("", 30), 1e-9);
    }

    @Test
    void keypadCellsStaySquareAndFitTheSmallestSide() {
        assertEquals(100, PanneauTouches.GrilleCarree.cote(640, 600, 4, 24));
        assertEquals(80, PanneauTouches.GrilleCarree.cote(480, 800, 6, 36));
        assertEquals(0, PanneauTouches.GrilleCarree.cote(0, 0, 4, 24));
    }
}
