package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TerrassementTest {
    private static final double DELTA = 1e-9;

    @Test
    void volumeAndBulking() {
        assertEquals(1.8, Terrassement.volume(4, 3, 15), DELTA);
        // 1,8 m³ en place font 2,25 m³ une fois creusés à 25 %
        assertEquals(2.25, Terrassement.foisonne(1.8, Terrassement.FOISONNEMENT_POURCENT), DELTA);
        assertThrows(IllegalArgumentException.class, () -> Terrassement.volume(4, -3, 15));
        assertThrows(IllegalArgumentException.class, () -> Terrassement.foisonne(Double.NaN, 25));
    }

    @Test
    void bigBags() {
        assertEquals(3, Terrassement.bigBags(2.25, 1));
        assertEquals(2, Terrassement.bigBags(2, 1));
        assertEquals(0, Terrassement.bigBags(0, 1));
        assertThrows(IllegalArgumentException.class, () -> Terrassement.bigBags(2, 0));
    }

    @Test
    void sandDelivery() {
        // 1,8 m³ + 10 % = 1,98 m³ de sable à 1,6 t/m³ = 3,168 t, soit 4 big bags d'une tonne
        Terrassement.Apport vApport = Terrassement.apport(1.8, Terrassement.Materiau.SABLE, 10, 1);
        assertEquals(1.98, vApport.volumeM3(), DELTA);
        assertEquals(3.168, vApport.masseT(), DELTA);
        assertEquals(4, vApport.bigBags());
        assertEquals(1.5, Terrassement.Materiau.GRAVIER.densite(), DELTA);
        assertEquals("Gravier", Terrassement.Materiau.GRAVIER.toString());
        assertThrows(IllegalArgumentException.class,
                () -> Terrassement.apport(1, Terrassement.Materiau.SABLE, -5, 1));
    }
}
