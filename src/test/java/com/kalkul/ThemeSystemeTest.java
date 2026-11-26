package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import org.junit.jupiter.api.Test;

class ThemeSystemeTest {
    private static final String CLAIR = "\r\nHKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion"
            + "\\Themes\\Personalize\r\n    AppsUseLightTheme    REG_DWORD    0x1\r\n\r\n";
    private static final String SOMBRE = CLAIR.replace("0x1", "0x0");

    @Test
    void readsTheWindowsSetting() {
        assertTrue(ThemeSysteme.clairSelonRegistre(CLAIR));
        assertFalse(ThemeSysteme.clairSelonRegistre(SOMBRE));
        // Clé absente (Windows ancien) ou commande en échec : thème sombre
        assertFalse(ThemeSysteme.clairSelonRegistre(""));
        assertFalse(ThemeSysteme.clairSelonRegistre("ERREUR : clé introuvable"));
    }

    @Test
    void propertyWinsOverWindows() {
        assertTrue(ThemeSysteme.choisirClair("clair", "Windows 11", () -> SOMBRE));
        assertFalse(ThemeSysteme.choisirClair(" SOMBRE ", "Windows 11", () -> CLAIR));
    }

    @Test
    void savedSettingWinsOverWindowsButNotOverTheProperty() {
        assertTrue(ThemeSysteme.choisirClair("", "clair", "Windows 11", () -> SOMBRE));
        assertFalse(ThemeSysteme.choisirClair(null, "sombre", "Windows 11", () -> CLAIR));
        assertFalse(ThemeSysteme.choisirClair("sombre", "clair", "Windows 11", () -> CLAIR));
        // « auto » ou réglage inconnu : on suit Windows
        assertTrue(ThemeSysteme.choisirClair(null, "auto", "Windows 11", () -> CLAIR));
        assertFalse(ThemeSysteme.choisirClair(null, "bleu", "Windows 11", () -> SOMBRE));
        assertFalse(ThemeSysteme.choisirClair(null, "auto", "Linux", () -> CLAIR));
    }

    @Test
    void followsWindowsOnlyOnWindows() {
        assertTrue(ThemeSysteme.choisirClair(null, "Windows 11", () -> CLAIR));
        assertFalse(ThemeSysteme.choisirClair("", "Windows 10", () -> SOMBRE));
        assertFalse(ThemeSysteme.choisirClair(null, "Linux", () -> {
            throw new AssertionError("le registre n'existe que sous Windows");
        }));
    }

    @Test
    void hoverDarkensInLightThemeAndLightensInDarkTheme() {
        Color vSurvol = Touche.eclaircir(new Color(100, 100, 100));
        int vAttendu = Habillage.CLAIR ? 82 : 122;
        assertEquals(new Color(vAttendu, vAttendu, vAttendu), vSurvol);
        // Les bornes tiennent aux extrémités
        Color vBlanc = Touche.eclaircir(Color.WHITE);
        assertTrue(vBlanc.getRed() <= 255);
    }
}
