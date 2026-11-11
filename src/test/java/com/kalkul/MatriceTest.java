package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MatriceTest {
    private static final double DELTA = 1e-9;

    @Test
    void parsingReadsRowsAndColumns() {
        Matrice vMatrix = Matrice.parse("1 2 3\n4 5 6");
        assertEquals(2, vMatrix.rows());
        assertEquals(3, vMatrix.columns());
        assertEquals(6, vMatrix.get(1, 2), DELTA);
        assertEquals(2, Matrice.parse("1 2; 3 4").rows());
    }

    @Test
    void raggedOrEmptyMatricesAreRefused() {
        assertThrows(IllegalArgumentException.class, () -> Matrice.parse("1 2\n3"));
        assertThrows(IllegalArgumentException.class, () -> Matrice.parse(""));
    }

    @Test
    void additionAndSubtraction() {
        Matrice vA = Matrice.parse("1 2\n3 4");
        Matrice vB = Matrice.parse("5 6\n7 8");
        assertEquals("6  8\n10  12", vA.add(vB).toString());
        assertEquals("4  4\n4  4", vB.subtract(vA).toString());
    }

    @Test
    void additionNeedsSameDimensions() {
        assertThrows(IllegalArgumentException.class, () -> Matrice.parse("1 2").add(Matrice.parse("1\n2")));
    }

    @Test
    void multiplication() {
        Matrice vA = Matrice.parse("1 2\n3 4");
        Matrice vB = Matrice.parse("5 6\n7 8");
        assertEquals("19  22\n43  50", vA.multiply(vB).toString());
        assertEquals("14", Matrice.parse("1 2 3").multiply(Matrice.parse("1\n2\n3")).toString());
    }

    @Test
    void multiplicationNeedsMatchingDimensions() {
        assertThrows(IllegalArgumentException.class, () -> Matrice.parse("1 2").multiply(Matrice.parse("1 2")));
    }

    @Test
    void transpose() {
        assertEquals("1  4\n2  5\n3  6", Matrice.parse("1 2 3\n4 5 6").transpose().toString());
    }

    @Test
    void determinant() {
        assertEquals(-2, Matrice.parse("1 2\n3 4").determinant(), DELTA);
        assertEquals(6, Matrice.parse("2 0 1\n1 3 2\n1 1 2").determinant(), DELTA);
        assertEquals(0, Matrice.parse("1 2\n2 4").determinant(), DELTA);
    }

    @Test
    void determinantNeedsASquareMatrix() {
        assertThrows(IllegalArgumentException.class, () -> Matrice.parse("1 2 3").determinant());
    }

    @Test
    void inverseTimesOriginalIsIdentity() {
        Matrice vA = Matrice.parse("4 7\n2 6");
        Matrice vProduct = vA.multiply(vA.inverse());
        assertEquals(1, vProduct.get(0, 0), DELTA);
        assertEquals(0, vProduct.get(0, 1), DELTA);
        assertEquals(0, vProduct.get(1, 0), DELTA);
        assertEquals(1, vProduct.get(1, 1), DELTA);
    }

    @Test
    void inverseOfAKnownMatrix() {
        Matrice vInverse = Matrice.parse("4 7\n2 6").inverse();
        assertEquals(0.6, vInverse.get(0, 0), DELTA);
        assertEquals(-0.7, vInverse.get(0, 1), DELTA);
        assertEquals(-0.2, vInverse.get(1, 0), DELTA);
        assertEquals(0.4, vInverse.get(1, 1), DELTA);
    }

    @Test
    void singularMatrixHasNoInverse() {
        assertThrows(ArithmeticException.class, () -> Matrice.parse("1 2\n2 4").inverse());
    }

    @Test
    void linearSystemIsSolved() {
        // x + y = 3 ; 2x - y = 0  ->  x = 1, y = 2
        Matrice vSolution = Matrice.parse("1 1\n2 -1").solve(Matrice.parse("3\n0"));
        assertEquals(1, vSolution.get(0, 0), DELTA);
        assertEquals(2, vSolution.get(1, 0), DELTA);
    }

    @Test
    void systemNeedingARowSwapIsSolved() {
        // 0x + y = 2 ; x + y = 5  ->  x = 3, y = 2
        Matrice vSolution = Matrice.parse("0 1\n1 1").solve(Matrice.parse("2\n5"));
        assertEquals(3, vSolution.get(0, 0), DELTA);
        assertEquals(2, vSolution.get(1, 0), DELTA);
    }

    @Test
    void singularSystemAndWrongSizeAreRefused() {
        assertThrows(ArithmeticException.class, () -> Matrice.parse("1 2\n2 4").solve(Matrice.parse("1\n2")));
        assertThrows(IllegalArgumentException.class, () -> Matrice.parse("1 2\n3 4").solve(Matrice.parse("1")));
    }

    @Test
    void vectorOperations() {
        assertEquals(32, Vecteurs.dot(new double[] {1, 2, 3}, new double[] {4, 5, 6}), DELTA);
        assertEquals(5, Vecteurs.norm(new double[] {3, 4}), DELTA);
        assertArrayEquals(new double[] {0, 0, 1}, Vecteurs.cross(new double[] {1, 0, 0}, new double[] {0, 1, 0}), DELTA);
        assertThrows(IllegalArgumentException.class, () -> Vecteurs.dot(new double[] {1}, new double[] {1, 2}));
        assertThrows(IllegalArgumentException.class, () -> Vecteurs.cross(new double[] {1, 2}, new double[] {1, 2}));
    }
}
