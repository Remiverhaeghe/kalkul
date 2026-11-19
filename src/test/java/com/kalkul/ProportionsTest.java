package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ProportionsTest {
    private static final double DELTA = 1e-9;

    @Test
    void ruleOfThree() {
        // 3 m² pour 7,5 €, donc 5 m² pour 12,5 €
        assertEquals(12.5, Proportions.regleDeTrois(3, 7.5, 5), DELTA);
        assertThrows(IllegalArgumentException.class, () -> Proportions.regleDeTrois(0, 1, 2));
    }

    @Test
    void percentages() {
        assertEquals(16, Proportions.pourcentageDe(20, 80), DELTA);
        assertEquals(25, Proportions.part(20, 80), DELTA);
        assertEquals(-20, Proportions.variation(100, 80), DELTA);
        assertEquals(50, Proportions.variation(-10, -5), DELTA);
        assertThrows(IllegalArgumentException.class, () -> Proportions.part(1, 0));
        assertThrows(IllegalArgumentException.class, () -> Proportions.variation(0, 5));
    }

    @Test
    void successiveDiscountsDoNotAdd() {
        // 20 % puis 10 % : 28 % au total, pas 30 %
        assertEquals(72, Proportions.apresRemises(100, 20, 10), DELTA);
        assertEquals(100, Proportions.apresRemises(100), DELTA);
        assertEquals(100, Proportions.avantRemise(80, 20), DELTA);
        assertThrows(IllegalArgumentException.class, () -> Proportions.apresRemises(100, 120));
        assertThrows(IllegalArgumentException.class, () -> Proportions.apresRemises(100, -5));
        assertThrows(IllegalArgumentException.class, () -> Proportions.avantRemise(80, 100));
    }

    @Test
    void pricePerSquareMetre() {
        assertEquals(20, Proportions.prixAuM2(44.4, 2.22), 1e-9);
        assertThrows(IllegalArgumentException.class, () -> Proportions.prixAuM2(10, 0));
    }
}
