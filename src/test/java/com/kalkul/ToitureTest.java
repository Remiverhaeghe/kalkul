package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ToitureTest {
    private static final double DELTA = 1e-9;

    @Test
    void threeWaysToWriteASlope() {
        Toiture.Pente vPente = Toiture.depuisPourcent(100);
        assertEquals(45, vPente.degres(), DELTA);
        assertEquals(100, vPente.cmParMetre(), DELTA);
        assertEquals(57.735, Toiture.depuisDegres(30).pourcent(), 0.001);
        // 60 cm de montée sur 2 m : 30 %
        assertEquals(30, Toiture.depuisMontee(60, 2).pourcent(), DELTA);
        assertEquals(30, Conversions.convert(30, "cm/m", "% de pente"), DELTA);
        assertThrows(IllegalArgumentException.class, () -> Toiture.depuisPourcent(-1));
        assertThrows(IllegalArgumentException.class, () -> Toiture.depuisDegres(90));
        assertThrows(IllegalArgumentException.class, () -> Toiture.depuisDegres(-1));
    }

    @Test
    void rafterLength() {
        // 4 m à l'horizontale à 75 % : 5 m de rampant (triangle 3-4-5)
        assertEquals(5, Toiture.rampant(4, Toiture.depuisPourcent(75)), DELTA);
        assertThrows(IllegalArgumentException.class, () -> Toiture.rampant(-1, Toiture.depuisPourcent(30)));
    }

    @Test
    void minimumSlopeOfTheRoofing() {
        assertTrue(Toiture.suffit(Toiture.depuisPourcent(24), Toiture.Couverture.TUILE_CANAL));
        assertFalse(Toiture.suffit(Toiture.depuisPourcent(23.9), Toiture.Couverture.TUILE_CANAL));
        assertFalse(Toiture.suffit(Toiture.depuisDegres(30), Toiture.Couverture.TUILE_PLATE));
        assertEquals("Ardoise naturelle", Toiture.Couverture.ARDOISE.toString());
        assertEquals(40, Toiture.Couverture.ARDOISE.minimumPourcent(), DELTA);
    }
}
