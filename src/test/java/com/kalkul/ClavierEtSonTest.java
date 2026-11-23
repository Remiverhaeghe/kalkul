package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.DefaultButtonModel;
import org.junit.jupiter.api.Test;

class ClavierEtSonTest {

    @Test
    void keyboardKeysLightTheMatchingScreenKey() {
        assertEquals("=", Kalkul.toucheDuClavier("\n"));
        assertEquals("=", Kalkul.toucheDuClavier("\r"));
        assertEquals("⌫", Kalkul.toucheDuClavier("\b"));
        assertEquals("C", Kalkul.toucheDuClavier("c"));
        assertEquals("7", Kalkul.toucheDuClavier("7"));
    }

    @Test
    void clickIsShortAndFadesOut() {
        byte[] vOctets = Declic.echantillons();
        int vNombre = (int) (Declic.FREQUENCE_ECHANTILLONNAGE * Declic.DUREE_MS / 1000);
        assertEquals(vNombre * 2, vOctets.length);
        assertTrue(Math.abs(amplitude(vOctets, 0, 40)) > Math.abs(amplitude(vOctets, vNombre - 40, vNombre)));
    }

    @Test
    void keyLitFromKeyboardLooksPressedWithoutFiring() {
        Touche vTouche = new Touche("7");
        int[] vClics = {0};
        vTouche.addActionListener(pEvent -> vClics[0]++);
        vTouche.eclairer();
        assertTrue(vTouche.enfoncee());
        assertEquals(0, vClics[0]);
    }

    @Test
    void flatButtonDarkensWhenPressed() {
        DefaultButtonModel vModele = new DefaultButtonModel();
        assertEquals(Habillage.Role.CHIFFRE.aFond, BoutonPlat.fond(vModele));
        vModele.setArmed(true);
        vModele.setPressed(true);
        assertEquals(Touche.nuance(Habillage.Role.CHIFFRE.aFond, -12), BoutonPlat.fond(vModele));
    }

    /** Plus grand écart à zéro sur une plage d'échantillons. */
    private static int amplitude(byte[] pOctets, int pDebut, int pFin) {
        int rMax = 0;
        for (int vIndex = pDebut; vIndex < pFin; vIndex++) {
            int vValeur = (short) ((pOctets[2 * vIndex] & 0xFF) | (pOctets[2 * vIndex + 1] << 8));
            rMax = Math.max(rMax, Math.abs(vValeur));
        }
        return rMax;
    }
}
