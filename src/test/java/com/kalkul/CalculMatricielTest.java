package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CalculMatricielTest {
    @Test
    void everyListedOperationWorksOnSimpleData() {
        String vA = "1 1\n2 -1";
        String vB = "3\n0";
        assertEquals("x1 =  1\nx2 =  2", CalculMatriciel.apply("Résoudre A·x = B", vA, vB));
        assertEquals("det(A) = -3", CalculMatriciel.apply("Déterminant de A", vA, ""));
        assertEquals("1  2\n1  -1", CalculMatriciel.apply("Transposée de A", vA, ""));
        assertEquals("3  5\n5  -1", CalculMatriciel.apply("A + B", vA, "2 4\n3 0"));
        assertEquals("-1  -3\n1  -1", CalculMatriciel.apply("A − B", vA, "2 4\n1 0"));
        // A x B avec B = (1 -1 ; 3 0) : (1*1+1*3, 1*-1+1*0) puis (2*1-1*3, 2*-1-1*0)
        assertEquals("4  -1\n-1  -2", CalculMatriciel.apply("A × B", vA, "1 -1\n3 0"));
    }

    @Test
    void vectorOperationsUseTheFirstRows() {
        assertEquals("A · B = 32", CalculMatriciel.apply(
                "Produit scalaire (1re ligne de A et de B)", "1 2 3", "4 5 6"));
        assertEquals("0  0  1", CalculMatriciel.apply(
                "Produit vectoriel (1re ligne de A et de B)", "1 0 0", "0 1 0"));
        assertEquals("‖A‖ = 5", CalculMatriciel.apply("Norme (1re ligne de A)", "3 4", ""));
    }

    @Test
    void inverseIsDisplayed() {
        assertEquals("0.6  -0.7\n-0.2  0.4", CalculMatriciel.apply("Inverse de A", "4 7\n2 6", ""));
    }

    @Test
    void unknownOperationAndBadInputAreRefused() {
        assertThrows(IllegalArgumentException.class, () -> CalculMatriciel.apply("Magie", "1", "1"));
        assertThrows(IllegalArgumentException.class, () -> CalculMatriciel.apply("A + B", "1 2", "1"));
        assertThrows(ArithmeticException.class, () -> CalculMatriciel.apply("Inverse de A", "1 2\n2 4", ""));
    }
}
