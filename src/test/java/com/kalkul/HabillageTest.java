package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HabillageTest {

    @Test
    void eachKeyGetsTheColourOfItsRole() {
        assertEquals(Habillage.Role.CHIFFRE, Habillage.role("7"));
        assertEquals(Habillage.Role.CHIFFRE, Habillage.role("."));
        assertEquals(Habillage.Role.OPERATEUR, Habillage.role("/"));
        assertEquals(Habillage.Role.OPERATEUR, Habillage.role("mod"));
        assertEquals(Habillage.Role.EGAL, Habillage.role("="));
        assertEquals(Habillage.Role.EFFACER, Habillage.role("C"));
        assertEquals(Habillage.Role.EFFACER, Habillage.role("⌫"));
        assertEquals(Habillage.Role.FONCTION, Habillage.role("sin"));
    }

    @Test
    void operatorsShowTheirTypographicSign() {
        assertEquals("÷", Habillage.signe("/"));
        assertEquals("×", Habillage.signe("*"));
        assertEquals("−", Habillage.signe("-"));
        // Le reste garde son texte, et le moteur reçoit toujours le texte d'origine
        assertEquals("sin", Habillage.signe("sin"));
        Touche vKey = new Touche("*");
        assertEquals("×", vKey.getText());
        assertEquals("*", vKey.getActionCommand());
    }
}
