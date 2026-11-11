package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ConversionsTest {
    private static final double DELTA = 1e-9;

    @Test
    void lengthMassAndVolume() {
        assertEquals(1000, Conversions.convert(1, "km", "m"), DELTA);
        assertEquals(2.54, Conversions.convert(1, "pouce", "cm"), DELTA);
        assertEquals(1000, Conversions.convert(1, "kg", "g"), DELTA);
        assertEquals(0.5, Conversions.convert(500, "ml", "l"), DELTA);
    }

    @Test
    void temperature() {
        assertEquals(212, Conversions.convert(100, "°C", "°F"), DELTA);
        assertEquals(273.15, Conversions.convert(0, "°C", "K"), DELTA);
        assertEquals(-40, Conversions.convert(-40, "°F", "°C"), DELTA);
    }

    @Test
    void convertingToTheSameUnitChangesNothing() {
        assertEquals(12, Conversions.convert(12, "m", "m"), DELTA);
    }

    @Test
    void differentCategoriesAreRefused() {
        assertThrows(IllegalArgumentException.class, () -> Conversions.convert(1, "kg", "m"));
    }

    @Test
    void unknownUnitIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> Conversions.convert(1, "parsec", "m"));
    }

    @Test
    void everyListedUnitCanBeConvertedToItself() {
        for (String vUnit : Conversions.units()) {
            assertEquals(3, Conversions.convert(3, vUnit, vUnit), DELTA);
        }
    }
}
