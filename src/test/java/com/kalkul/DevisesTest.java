package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.prefs.Preferences;
import org.junit.jupiter.api.Test;

class DevisesTest {

    private static final LocalDate JOUR = LocalDate.of(2026, 11, 20);

    @Test
    void fileFromTheCentralBankIsRead() {
        Devises vDevises = Devises.lire("""
                Date, USD, JPY, CHF, XYZ,
                18 November 2026, 1.0800, 160.00, 0.9500, N/A,
                """, JOUR);

        assertEquals(LocalDate.of(2026, 11, 18), vDevises.date());
        assertEquals(List.of("EUR", "CHF", "JPY", "USD"), vDevises.codes());
        assertEquals(108.0, vDevises.convertir(100, "EUR", "USD"), 1e-9);
        assertEquals(100.0, vDevises.convertir(108, "USD", "EUR"), 1e-9);
        assertEquals(16000.0 / 1.08, vDevises.convertir(100, "USD", "JPY"), 1e-6);
    }

    @Test
    void simpleListWithFrenchDecimalsAndOptionalDate() {
        Devises vDevises = Devises.lire("date;2026-11-19\nusd;1,08\nGBP = 0.85\nligne abîmée\nAB;2\nCHF;-1\n", JOUR);
        assertEquals(LocalDate.of(2026, 11, 19), vDevises.date());
        assertEquals(List.of("EUR", "GBP", "USD"), vDevises.codes());

        Devises vSansDate = Devises.lire("USD;1.1\ndate;hier", JOUR);
        assertEquals(JOUR, vSansDate.date());
        Devises vBceDateAbimee = Devises.lire("Date, USD\nhier, 1.1", JOUR);
        assertEquals(JOUR, vBceDateAbimee.date());
        assertEquals(1.1, vBceDateAbimee.taux("USD"), 1e-9);
    }

    @Test
    void typedRateImportAndAge() {
        Devises vDevises = new Devises();
        assertNull(vDevises.date());
        assertFalse(vDevises.anciens(JOUR));
        assertThrows(IllegalArgumentException.class, () -> vDevises.convertir(1, "EUR", "USD"));

        vDevises.saisir(" usd ", 1.08, JOUR.minusDays(8));
        assertEquals(1.08, vDevises.taux("USD"), 1e-9);
        assertTrue(vDevises.anciens(JOUR));
        assertFalse(vDevises.anciens(JOUR.minusDays(1)));
        assertThrows(IllegalArgumentException.class, () -> vDevises.saisir("EUR", 2, JOUR));
        assertThrows(IllegalArgumentException.class, () -> vDevises.saisir("USD", 0, JOUR));
        assertThrows(IllegalArgumentException.class, () -> vDevises.saisir("USD", Double.POSITIVE_INFINITY, JOUR));

        assertEquals(1, vDevises.importer("CHF;0,95", JOUR));
        assertNull(vDevises.taux("USD"));
        assertThrows(IllegalArgumentException.class, () -> vDevises.importer("rien d'utile", JOUR));
        assertEquals(0.95, vDevises.taux("CHF"), 1e-9);
    }

    @Test
    void ratesSurviveARestart() throws Exception {
        Preferences vPreferences = Preferences.userRoot().node("kalkul-test-devises");
        vPreferences.clear();
        try {
            Devises vDevises = Devises.depuis(vPreferences, JOUR);
            assertNull(vDevises.date());
            vDevises.saisir("USD", 1.08, LocalDate.of(2026, 11, 17));

            Devises vRelues = Devises.depuis(vPreferences, JOUR);
            assertEquals(LocalDate.of(2026, 11, 17), vRelues.date());
            assertEquals(1.08, vRelues.taux("USD"), 1e-9);
        } finally {
            vPreferences.removeNode();
        }
    }

    @Test
    void stateLineShowsTheDateAndWarnsWhenOld() {
        Devises vDevises = new Devises();
        assertEquals("Aucun taux : tapez-en un ou importez un fichier.", PanneauDevises.etat(vDevises, JOUR));
        vDevises.saisir("USD", 1.08, LocalDate.of(2026, 11, 18));
        assertEquals("Taux du 18/11/2026.", PanneauDevises.etat(vDevises, JOUR));
        assertEquals("Taux du 18/11/2026, plus d'une semaine : à mettre à jour.",
                PanneauDevises.etat(vDevises, LocalDate.of(2026, 12, 1)));
    }
}
