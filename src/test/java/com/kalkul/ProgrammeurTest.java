package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ProgrammeurTest {

    @Test
    void writesInEachBase() {
        assertEquals("FF", Programmeur.ecrire(255, 16, 32));
        assertEquals("255", Programmeur.ecrire(255, 10, 32));
        assertEquals("377", Programmeur.ecrire(255, 8, 32));
        assertEquals("0000 0000 1111 1111", Programmeur.ecrire(255, 2, 16));
        assertEquals("0000 0101", Programmeur.ecrire(5, 2, 8));
    }

    @Test
    void negativeNumbersUseTwosComplement() {
        assertEquals("FF", Programmeur.ecrire(-1, 16, 8));
        assertEquals("FFFF", Programmeur.ecrire(-1, 16, 16));
        assertEquals("FFFFFFFFFFFFFFFF", Programmeur.ecrire(-1, 16, 64));
        assertEquals("-128", Programmeur.ecrire(0x80, 10, 8));
        assertEquals(-1, Programmeur.lire("FF", 16, 8));
        assertEquals(255, Programmeur.lire("FF", 16, 16));
    }

    @Test
    void readsPrefixesSpacesAndUnderscores() {
        assertEquals(10, Programmeur.lire("0b1010", 2, 32));
        assertEquals(10, Programmeur.lire("10 10", 2, 32));
        assertEquals(255, Programmeur.lire("0xff", 16, 32));
        assertEquals(1000000, Programmeur.lire("1_000_000", 10, 32));
        assertEquals(8, Programmeur.lire("0o10", 8, 32));
        assertThrows(NumberFormatException.class, () -> Programmeur.lire("", 16, 32));
        assertThrows(NumberFormatException.class, () -> Programmeur.lire("12", 2, 32));
        assertThrows(NumberFormatException.class, () -> Programmeur.lire("1FF", 16, 8));
    }

    @Test
    void bitwiseOperations() {
        assertEquals(0b1000, Programmeur.calculer(0b1100, 0b1010, "AND", 8));
        assertEquals(0b1110, Programmeur.calculer(0b1100, 0b1010, "OR", 8));
        assertEquals(0b0110, Programmeur.calculer(0b1100, 0b1010, "XOR", 8));
        assertEquals(-16, Programmeur.non(15, 8));
        assertEquals(4, Programmeur.bitsAUn(0b1011_0100, 8));
    }

    @Test
    void shiftsOverflowAndDivision() {
        assertEquals(40, Programmeur.calculer(5, 3, "<<", 32));
        // 128 ne tient pas sur 8 bits signés : il revient à -128
        assertEquals(-128, Programmeur.calculer(1, 7, "<<", 8));
        assertEquals(-4, Programmeur.calculer(-16, 2, ">>", 8));
        assertEquals(44, Programmeur.calculer(200, 100, "+", 8));
        assertEquals(3, Programmeur.calculer(17, 5, "/", 32));
        assertEquals(2, Programmeur.calculer(17, 5, "mod", 32));
        assertEquals(-7, Programmeur.calculer(3, 10, "-", 32));
        assertEquals(42, Programmeur.calculer(6, 7, "*", 32));
        assertThrows(ArithmeticException.class, () -> Programmeur.calculer(1, 0, "/", 32));
        assertThrows(ArithmeticException.class, () -> Programmeur.calculer(1, 8, "<<", 8));
        assertThrows(IllegalArgumentException.class, () -> Programmeur.calculer(1, 1, "NAND", 8));
    }
}
