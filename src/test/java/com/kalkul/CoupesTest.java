package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CoupesTest {
    private static final double DELTA = 1e-9;

    @Test
    void mitreOfACorner() {
        assertEquals(45, Coupes.onglet(90), DELTA);
        // Pan coupé à 135° : 22,5° sur chaque plinthe
        assertEquals(22.5, Coupes.onglet(135), DELTA);
        assertEquals(0, Coupes.onglet(180), DELTA);
        assertThrows(IllegalArgumentException.class, () -> Coupes.onglet(0));
        assertThrows(IllegalArgumentException.class, () -> Coupes.onglet(200));
        assertThrows(IllegalArgumentException.class, () -> Coupes.onglet(Double.NaN));
    }

    @Test
    void compoundCutFallsBackOnTheMitre() {
        // Pièce debout couchée sur la table : tout passe dans la lame
        Coupes.Reglage vDebout = Coupes.composee(90, 0);
        assertEquals(0, vDebout.coupeDeg(), DELTA);
        assertEquals(45, vDebout.lameDeg(), DELTA);
        // Pièce à plat, comme un cadre : tout passe dans la table
        Coupes.Reglage vAPlat = Coupes.composee(90, 90);
        assertEquals(45, vAPlat.coupeDeg(), DELTA);
        assertEquals(0, vAPlat.lameDeg(), 1e-6);
    }

    @Test
    void crownMouldingAt38Degrees() {
        // Valeurs connues d'une corniche 52/38 dans un angle droit : 31,6° et 33,9°
        Coupes.Reglage vCorniche = Coupes.composee(90, 38);
        assertEquals(31.62, vCorniche.coupeDeg(), 0.01);
        assertEquals(33.86, vCorniche.lameDeg(), 0.01);
        assertThrows(IllegalArgumentException.class, () -> Coupes.composee(90, -1));
        assertThrows(IllegalArgumentException.class, () -> Coupes.composee(90, 91));
    }

    @Test
    void jackRafterAgainstAHip() {
        Coupes.Chevron vChevron = Coupes.chevron(30, 45);
        assertEquals(30, vChevron.aplombDeg(), DELTA);
        assertEquals(60, vChevron.piedDeg(), DELTA);
        // tan(joue) = tan 45° / cos 30° = 1,1547
        assertEquals(Math.toDegrees(Math.atan(1 / Math.cos(Math.toRadians(30)))), vChevron.joueDessusDeg(), DELTA);
        assertEquals(30, vChevron.reglageArretier().coupeDeg(), DELTA);
        assertEquals(45, vChevron.reglageArretier().lameDeg(), DELTA);
        // Toit plat : la joue vaut l'angle en plan
        assertEquals(45, Coupes.chevron(0, 45).joueDessusDeg(), DELTA);
        assertThrows(IllegalArgumentException.class, () -> Coupes.chevron(90, 45));
        assertThrows(IllegalArgumentException.class, () -> Coupes.chevron(30, 90));
        assertThrows(IllegalArgumentException.class, () -> Coupes.chevron(-5, 45));
    }
}
