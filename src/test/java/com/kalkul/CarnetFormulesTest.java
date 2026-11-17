package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class CarnetFormulesTest {
    private static final double DELTA = 1e-9;

    @Test
    void newestFirstWithoutDuplicates() {
        CarnetFormules vCarnet = new CarnetFormules();
        vCarnet.enregistrer("x^2", "");
        vCarnet.enregistrer("a*x + b", "a=2; b=1");
        vCarnet.enregistrer(" x^2 ", null);
        assertEquals(List.of(new CarnetFormules.Formule("x^2", ""), new CarnetFormules.Formule("a*x + b", "a=2; b=1")),
                vCarnet.liste());
    }

    @Test
    void anUnreadableFormulaIsRefused() {
        CarnetFormules vCarnet = new CarnetFormules();
        assertThrows(IllegalArgumentException.class, () -> vCarnet.enregistrer("2 * (x", ""));
        assertTrue(vCarnet.liste().isEmpty());
    }

    @Test
    void appliedToANumberWithItsParameters() {
        assertEquals(7, CarnetFormules.appliquer(new CarnetFormules.Formule("a*x + b", "a=2; b=1"), 3), DELTA);
        assertEquals(9, CarnetFormules.appliquer(new CarnetFormules.Formule("x^2", ""), -3), DELTA);
        assertThrows(ArithmeticException.class,
                () -> CarnetFormules.appliquer(new CarnetFormules.Formule("1/x", ""), 0));
    }

    @Test
    void writtenWithItsNumbers() {
        assertEquals("2*3 + 1", CarnetFormules.ecrire(new CarnetFormules.Formule("a*x + b", "a=2; b=1"), 3));
        assertEquals("(-3)^2", CarnetFormules.ecrire(new CarnetFormules.Formule("x^2", ""), -3));
        // Les noms de fonction ne sont pas touchés
        assertEquals("exp(0.5)", CarnetFormules.ecrire(new CarnetFormules.Formule("exp(x)", ""), 0.5));
    }

    @Test
    void savedAndReadBackSkippingDamagedLines() {
        CarnetFormules vCarnet = new CarnetFormules();
        vCarnet.enregistrer("sin(x)", "");
        vCarnet.enregistrer("a*x", "a=3");
        CarnetFormules vRelu = new CarnetFormules();
        vRelu.lire(vCarnet.texte() + "2 * (x\t\n\n");
        assertEquals(vCarnet.liste(), vRelu.liste());
    }

    @Test
    void listenersAreToldOfEachChange() {
        CarnetFormules vCarnet = new CarnetFormules();
        int[] vChanges = {0};
        vCarnet.ecouter(() -> vChanges[0]++);
        vCarnet.enregistrer("x", "");
        vCarnet.supprimer(vCarnet.liste().get(0));
        vCarnet.supprimer(new CarnetFormules.Formule("absente", ""));
        assertEquals(2, vChanges[0]);
    }

    @Test
    void cappedAtTheMaximum() {
        CarnetFormules vCarnet = new CarnetFormules();
        for (int vIndex = 0; vIndex < CarnetFormules.MAX + 3; vIndex++) {
            vCarnet.enregistrer("x + " + vIndex, "");
        }
        assertEquals(CarnetFormules.MAX, vCarnet.liste().size());
        assertEquals("x + " + (CarnetFormules.MAX + 2), vCarnet.liste().get(0).formule());
    }

    @Test
    void engineShowsAndRecordsAFormulaResult() {
        MoteurCalcul vEngine = new MoteurCalcul();
        vEngine.afficherResultat("f(3)", 7);
        assertEquals("7", vEngine.getDisplay());
        assertEquals(new MoteurCalcul.Calcul("f(3)", "7"), vEngine.getHistorique().get(0));
        vEngine.afficherErreur();
        assertEquals(MoteurCalcul.ERREUR, vEngine.getDisplay());
    }
}
