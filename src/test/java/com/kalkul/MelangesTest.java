package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MelangesTest {
    private static final double DELTA = 1e-9;

    @Test
    void concreteAt350() {
        // 250 l de béton à 350 kg : 87,5 kg de ciment, 3 sacs de 35 kg
        Melanges.Gachee vGachee = Melanges.gachee(Melanges.Type.BETON, 250, 350, 35, 10);
        assertEquals(87.5, vGachee.cimentKg(), DELTA);
        assertEquals(3, vGachee.sacs());
        assertEquals(10, vGachee.sableSeaux(), DELTA);
        assertEquals(20, vGachee.gravierSeaux(), DELTA);
        assertEquals(43.75, vGachee.eauL(), DELTA);
        // Un sac de 35 kg : 4 seaux de sable, 8 de gravier, 17,5 l d'eau
        assertEquals(4, vGachee.sableSeauxParSac(), DELTA);
        assertEquals(8, vGachee.gravierSeauxParSac(), DELTA);
        assertEquals(17.5, vGachee.eauLParSac(), DELTA);
    }

    @Test
    void mortarHasNoGravel() {
        Melanges.Gachee vGachee = Melanges.gachee(Melanges.Type.MORTIER, 100, 350, 25, 10);
        assertEquals(35, vGachee.cimentKg(), DELTA);
        assertEquals(2, vGachee.sacs());
        assertEquals(10, vGachee.sableSeaux(), DELTA);
        assertEquals(0, vGachee.gravierSeaux(), DELTA);
        assertEquals(400, Melanges.Type.ENDUIT.dosageKg(), DELTA);
        assertEquals("Enduit", Melanges.Type.ENDUIT.toString());
    }

    @Test
    void invalidInputsAreRefused() {
        assertThrows(IllegalArgumentException.class, () -> Melanges.gachee(Melanges.Type.BETON, -1, 350, 35, 10));
        assertThrows(IllegalArgumentException.class, () -> Melanges.gachee(Melanges.Type.BETON, 100, 0, 35, 10));
        assertThrows(IllegalArgumentException.class, () -> Melanges.gachee(Melanges.Type.BETON, 100, 350, 0, 10));
        assertThrows(IllegalArgumentException.class, () -> Melanges.gachee(Melanges.Type.BETON, 100, 350, 35, 0));
    }
}
