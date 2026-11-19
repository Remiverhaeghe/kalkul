package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class FeuilleCalculTest {
    private static final double DELTA = 1e-9;

    @Test
    void cellsReuseEachOther() {
        FeuilleCalcul vFeuille = new FeuilleCalcul();
        vFeuille.definir("surface", "4*3");
        vFeuille.definir("Prix", "surface*25");
        assertEquals(300, vFeuille.valeur("prix"), DELTA);
        // Changer une cellule change celles qui la reprennent
        vFeuille.definir("surface", "10");
        assertEquals(250, vFeuille.valeur("prix"), DELTA);
        assertEquals(List.of(new FeuilleCalcul.Cellule("surface", "10"),
                new FeuilleCalcul.Cellule("prix", "surface*25")), vFeuille.liste());
        assertEquals("prix = surface*25", vFeuille.liste().get(1).toString());
    }

    @Test
    void badCellsAreRefusedWithoutChangingTheSheet() {
        FeuilleCalcul vFeuille = new FeuilleCalcul();
        vFeuille.definir("a", "2");
        vFeuille.definir("b", "a+1");
        // Référence circulaire : a reprendrait b qui reprend a
        assertThrows(IllegalArgumentException.class, () -> vFeuille.definir("a", "b*2"));
        assertEquals(2, vFeuille.valeur("a"), DELTA);
        assertThrows(IllegalArgumentException.class, () -> vFeuille.definir("c", "c+1"));
        assertThrows(IllegalArgumentException.class, () -> vFeuille.definir("c", "inconnue*2"));
        assertThrows(IllegalArgumentException.class, () -> vFeuille.definir("c", "2*x"));
        assertThrows(IllegalArgumentException.class, () -> vFeuille.definir("x", "2"));
        assertThrows(IllegalArgumentException.class, () -> vFeuille.definir("pi", "2"));
        assertThrows(IllegalArgumentException.class, () -> vFeuille.definir("sin", "2"));
        assertThrows(IllegalArgumentException.class, () -> vFeuille.definir("a1", "2"));
        assertThrows(IllegalArgumentException.class, () -> vFeuille.definir("été", "2"));
        assertThrows(IllegalArgumentException.class, () -> vFeuille.definir("c", "1/0"));
        assertEquals(2, vFeuille.liste().size());
        assertThrows(IllegalArgumentException.class, () -> vFeuille.valeur("absente"));
    }

    @Test
    void theSheetHasALimit() {
        FeuilleCalcul vFeuille = new FeuilleCalcul();
        // e est la constante, elle ne peut pas nommer une cellule
        String vLettres = "abcdfghijklmnopq";
        for (int vI = 0; vI < FeuilleCalcul.MAX; vI++) {
            vFeuille.definir(vLettres.substring(vI, vI + 1), String.valueOf(vI));
        }
        assertThrows(IllegalArgumentException.class, () -> vFeuille.definir("z", "1"));
        // Remplacer une cellule existante reste possible
        vFeuille.definir("a", "42");
        assertEquals(42, vFeuille.valeur("a"), DELTA);
    }

    @Test
    void aBrokenCellIsLeftOutOfTheValues() {
        FeuilleCalcul vFeuille = new FeuilleCalcul();
        vFeuille.definir("a", "2");
        vFeuille.definir("b", "a*3");
        vFeuille.definir("c", "5");
        vFeuille.supprimer("A");
        assertEquals(Map.of("c", 5.0), vFeuille.valeurs());
    }

    @Test
    void savedFormulasUseTheCells() {
        FeuilleCalcul vFeuille = new FeuilleCalcul();
        vFeuille.definir("prix", "25");
        CarnetFormules.Formule vFormule = new CarnetFormules.Formule("prix*x + b", "b=10");
        assertEquals(85, CarnetFormules.appliquer(vFormule, 3, vFeuille.valeurs()), DELTA);
        assertEquals("25*3 + 10", CarnetFormules.ecrire(vFormule, 3, vFeuille.valeurs()));
        // Un paramètre de la formule passe avant la cellule
        CarnetFormules.Formule vAvecParametre = new CarnetFormules.Formule("prix*x", "prix=2");
        assertEquals(6, CarnetFormules.appliquer(vAvecParametre, 3, vFeuille.valeurs()), DELTA);
        assertThrows(IllegalArgumentException.class, () -> CarnetFormules.appliquer(vFormule, 3));
    }

    @Test
    void savedAndReadBack() {
        FeuilleCalcul vFeuille = new FeuilleCalcul();
        int[] vChangements = {0};
        vFeuille.ecouter(() -> vChangements[0]++);
        vFeuille.definir("a", "2");
        vFeuille.definir("b", "a*3");
        assertEquals(2, vChangements[0]);
        FeuilleCalcul vRelue = new FeuilleCalcul();
        vRelue.lire(vFeuille.texte() + "abîmée\n9\t1\nc\t2*(\n");
        assertEquals(vFeuille.liste(), vRelue.liste());
        vRelue.supprimer("inconnue");
        assertTrue(Expression.nomReserve("COS"));
        assertFalse(Expression.nomReserve("prix"));
    }
}
