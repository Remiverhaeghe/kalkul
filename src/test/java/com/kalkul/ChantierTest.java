package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ChantierTest {
    private static final double DELTA = 1e-9;

    @Test
    void packsIncludeTheWasteMargin() {
        // 20 m² + 10 % = 22 m², paquets de 2,22 m² : 10 paquets
        assertEquals(10, Chantier.paquets(20, 10, 2.22));
        // Tombe juste : pas de paquet en plus
        assertEquals(10, Chantier.paquets(20, 0, 2));
        assertEquals(0, Chantier.paquets(0, 10, 2));
        assertThrows(IllegalArgumentException.class, () -> Chantier.paquets(10, 5, 0));
        assertThrows(IllegalArgumentException.class, () -> Chantier.paquets(-1, 5, 2));
    }

    @Test
    void slabConcrete() {
        Chantier.Beton vBeton = Chantier.beton(4, 3, 12);
        assertEquals(1.44, vBeton.volumeM3(), DELTA);
        // 1,44 × 350 kg = 504 kg de ciment, soit 15 sacs de 35 kg
        assertEquals(15, vBeton.sacsCiment());
        assertEquals(1008, vBeton.sableKg(), 1e-6);
        assertEquals(1512, vBeton.gravierKg(), 1e-6);
        assertEquals(252, vBeton.eauL(), 1e-6);
        assertThrows(IllegalArgumentException.class, () -> Chantier.beton(4, -3, 12));
    }

    @Test
    void slope() {
        // Évacuation à 2 % sur 5 m : 10 cm
        assertEquals(10, Chantier.deniveleCm(5, 2), DELTA);
        assertEquals(2, Chantier.pentePourcent(10, 5), DELTA);
        assertThrows(IllegalArgumentException.class, () -> Chantier.pentePourcent(10, 0));
    }

    @Test
    void siteUnits() {
        assertEquals(10000, Conversions.convert(1, "hectare", "m²"), DELTA);
        assertEquals(1000, Conversions.convert(1, "m³", "l"), DELTA);
        assertEquals(2, Conversions.convert(2000, "kg", "t"), DELTA);
        assertEquals(1.11, Conversions.convert(100, "l", "brouette"), 0.01);
        assertEquals(20, Conversions.convert(2, "% de pente", "mm/m"), DELTA);
        assertEquals(45, Conversions.convert(100, "% de pente", "° de pente"), 1e-9);
        assertEquals(100, Conversions.convert(45, "° de pente", "% de pente"), 1e-9);
        assertThrows(IllegalArgumentException.class, () -> Conversions.convert(90, "° de pente", "% de pente"));
        assertThrows(IllegalArgumentException.class, () -> Conversions.convert(1, "m²", "m³"));
    }
}
