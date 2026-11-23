package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RaccourcisTest {
    private Preferences aPreferences;

    @BeforeEach
    void creer() {
        aPreferences = Preferences.userRoot().node("kalkul-test-raccourcis-" + System.nanoTime());
    }

    @AfterEach
    void supprimer() throws BackingStoreException {
        aPreferences.removeNode();
    }

    @Test
    void eachTabOpensWithCtrlAndItsRankByDefault() {
        Raccourcis vRaccourcis = Raccourcis.depuis(aPreferences);

        assertEquals("ctrl 1", vRaccourcis.texte(Raccourcis.Onglet.BASE));
        assertEquals("ctrl 7", vRaccourcis.texte(Raccourcis.Onglet.OUTILS));
        assertEquals("ctrl 9", vRaccourcis.texte(Raccourcis.Onglet.HISTORIQUE));
    }

    @Test
    void aChangedShortcutIsKeptForTheNextSession() {
        Raccourcis.depuis(aPreferences).definir(Raccourcis.Onglet.OUTILS, "alt shift H");

        assertEquals("shift alt H", Raccourcis.depuis(aPreferences).texte(Raccourcis.Onglet.OUTILS));
    }

    @Test
    void aShortcutWithoutModifierIsRefused() {
        Raccourcis vRaccourcis = Raccourcis.depuis(aPreferences);

        assertThrows(IllegalArgumentException.class, () -> vRaccourcis.definir(Raccourcis.Onglet.BASE, "1"));
        assertEquals("ctrl 1", vRaccourcis.texte(Raccourcis.Onglet.BASE));
    }

    @Test
    void anUnreadableShortcutIsRefused() {
        Raccourcis vRaccourcis = Raccourcis.depuis(aPreferences);

        assertThrows(IllegalArgumentException.class, () -> vRaccourcis.definir(Raccourcis.Onglet.BASE, "blabla"));
    }

    @Test
    void aShortcutAlreadyUsedByAnotherTabIsRefusedWithItsName() {
        Raccourcis vRaccourcis = Raccourcis.depuis(aPreferences);

        IllegalArgumentException vErreur = assertThrows(IllegalArgumentException.class,
                () -> vRaccourcis.definir(Raccourcis.Onglet.BASE, "ctrl 2"));

        assertEquals("Déjà pris par l'onglet Scientifique", vErreur.getMessage());
        assertEquals("ctrl 1", vRaccourcis.texte(Raccourcis.Onglet.BASE));
    }

    @Test
    void aTabCanKeepItsOwnShortcut() {
        Raccourcis vRaccourcis = Raccourcis.depuis(aPreferences);

        vRaccourcis.definir(Raccourcis.Onglet.BASE, "ctrl 1");

        assertEquals("ctrl 1", vRaccourcis.texte(Raccourcis.Onglet.BASE));
    }

    @Test
    void resettingRestoresTheDefaultsAndTellsTheListeners() {
        Raccourcis vRaccourcis = Raccourcis.depuis(aPreferences);
        int[] vAppels = {0};
        vRaccourcis.ecouter(() -> vAppels[0]++);
        vRaccourcis.definir(Raccourcis.Onglet.BASE, "alt B");

        vRaccourcis.reinitialiser();

        assertEquals(2, vAppels[0]);
        assertEquals("ctrl 1", vRaccourcis.texte(Raccourcis.Onglet.BASE));
        assertEquals("ctrl 1", Raccourcis.depuis(aPreferences).texte(Raccourcis.Onglet.BASE));
    }

    @Test
    void aCorruptedStoredValueFallsBackToTheDefault() {
        aPreferences.put("raccourci_BASE", "n'importe quoi");

        assertEquals("ctrl 1", Raccourcis.depuis(aPreferences).texte(Raccourcis.Onglet.BASE));
    }
}
