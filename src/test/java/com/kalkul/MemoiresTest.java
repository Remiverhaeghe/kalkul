package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.prefs.Preferences;
import org.junit.jupiter.api.Test;

class MemoiresTest {

    @Test
    void aValueIsKeptUnderItsName() {
        Memoires vMemoires = new Memoires();

        assertEquals(12.5, vMemoires.enregistrer("Prix", "12,5"), 1e-9);
        assertEquals(12.5, vMemoires.valeur("prix"), 1e-9);
        assertEquals(List.of("prix"), vMemoires.noms());
    }

    @Test
    void aFormulaCanQuoteAnotherMemory() {
        Memoires vMemoires = new Memoires();
        vMemoires.enregistrer("prix", "12.5");

        assertEquals(37.5, vMemoires.enregistrer("total", "prix * 3"), 1e-9);
        assertEquals(37.5, vMemoires.evaluer("prix*3"), 1e-9);
    }

    @Test
    void anUnknownMemoryIsRefusedAndNothingIsKept() {
        Memoires vMemoires = new Memoires();

        assertThrows(IllegalArgumentException.class, () -> vMemoires.enregistrer("total", "prix * 3"));
        assertTrue(vMemoires.noms().isEmpty());
    }

    @Test
    void invalidNamesAreRefused() {
        Memoires vMemoires = new Memoires();

        assertThrows(IllegalArgumentException.class, () -> vMemoires.enregistrer("", "1"));
        assertThrows(IllegalArgumentException.class, () -> vMemoires.enregistrer("a1", "1"));
        assertThrows(IllegalArgumentException.class, () -> vMemoires.enregistrer("sin", "1"));
        assertThrows(IllegalArgumentException.class, () -> vMemoires.enregistrer("pi", "1"));
        assertThrows(IllegalArgumentException.class, () -> vMemoires.enregistrer("x", "1"));
    }

    @Test
    void aResultWithoutValueIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> new Memoires().enregistrer("a", "1/0"));
    }

    @Test
    void aMemoryCanBeForgotten() {
        Memoires vMemoires = new Memoires();
        vMemoires.enregistrer("prix", "2");

        assertTrue(vMemoires.supprimer("Prix"));
        assertFalse(vMemoires.supprimer("prix"));
        assertNull(vMemoires.valeur("prix"));
    }

    @Test
    void textRoundTripKeepsValuesAndSkipsDamagedLines() {
        Memoires vMemoires = new Memoires();
        vMemoires.enregistrer("prix", "12.5");
        vMemoires.enregistrer("surface", "3.25");

        Memoires vRelue = Memoires.lire(vMemoires.texte() + "abime;pas un nombre\nsans separateur\n");

        assertEquals(List.of("prix", "surface"), vRelue.noms());
        assertEquals(3.25, vRelue.valeur("surface"), 1e-9);
    }

    @Test
    void preferencesKeepMemoriesFromOneSessionToTheNext() throws Exception {
        Preferences vNoeud = Preferences.userRoot().node("kalkul-test-memoires");
        try {
            vNoeud.clear();
            Memoires.depuis(vNoeud).enregistrer("prix", "9");

            assertEquals(9.0, Memoires.depuis(vNoeud).valeur("prix"), 1e-9);
        } finally {
            vNoeud.removeNode();
        }
    }

    @Test
    void summaryListsMemoriesOrInvitesToCreateOne() {
        Memoires vMemoires = new Memoires();
        assertTrue(PanneauMemoires.resume(vMemoires).startsWith("Aucune mémoire"));

        vMemoires.enregistrer("prix", "12.5");
        vMemoires.enregistrer("a", "2");

        assertEquals("a = 2 · prix = " + PanneauOutils.format(12.5), PanneauMemoires.resume(vMemoires));
    }
}
