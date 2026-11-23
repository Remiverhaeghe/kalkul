package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PdfTexteTest {
    private static final Charset WIN = Charset.forName("windows-1252");

    private static String lire(byte[] pPdf) {
        return new String(pPdf, WIN);
    }

    @Test
    void theDocumentIsAPdfWithATitleAndItsLines() {
        String vPdf = lire(PdfTexte.ecrire("Titre", List.of("Première", "Seconde")));

        assertTrue(vPdf.startsWith("%PDF-1.4\n"));
        assertTrue(vPdf.endsWith("%%EOF\n"));
        assertTrue(vPdf.contains("BT /F2 16 Tf 50 776 Td (Titre) Tj ET\n"));
        assertTrue(vPdf.contains("BT /F1 10 Tf 50 734 Td (Première) Tj ET\n"));
        assertTrue(vPdf.contains("BT /F1 10 Tf 50 720 Td (Seconde) Tj ET\n"));
        assertTrue(vPdf.contains("/Kids [5 0 R] /Count 1"));
    }

    @Test
    void everyCrossReferenceEntryPointsAtItsObject() {
        String vPdf = lire(PdfTexte.ecrire("Titre", List.of("a", "b")));
        String[] vTable = vPdf.substring(vPdf.indexOf("xref\n")).split("\n");
        int vObjets = Integer.parseInt(vTable[1].split(" ")[1]);

        for (int vNumero = 1; vNumero < vObjets; vNumero++) {
            int vPosition = Integer.parseInt(vTable[2 + vNumero].substring(0, 10));
            assertTrue(vPdf.startsWith(vNumero + " 0 obj\n", vPosition), "objet " + vNumero);
        }
        int vXref = Integer.parseInt(vPdf.substring(vPdf.indexOf("startxref\n") + 10).split("\n")[0]);
        assertTrue(vPdf.startsWith("xref\n", vXref));
    }

    @Test
    void parenthesesAndBackslashesAreEscapedAndAccentsKept() {
        String vPdf = lire(PdfTexte.ecrire("T", List.of("2+(3×4) \\ é")));

        assertTrue(vPdf.contains("(2+\\(3×4\\) \\\\ é) Tj"));
        assertEquals("a\\(b\\)", PdfTexte.echapper("a(b)"));
        assertEquals("a b", PdfTexte.echapper("a\r\nb"));
    }

    @Test
    void aLongListOverflowsOntoNewPages() {
        List<String> vLignes = new ArrayList<>();
        for (int vIndex = 0; vIndex < 150; vIndex++) {
            vLignes.add("ligne " + vIndex);
        }

        String vPdf = lire(PdfTexte.ecrire("T", vLignes));

        assertTrue(vPdf.contains("/Kids [5 0 R 7 0 R 9 0 R] /Count 3"));
        assertTrue(vPdf.contains("(ligne 149) Tj"));
    }

    @Test
    void theHistoryPdfListsEachCalculationAndMentionsAnEmptyOne() {
        String vPdf = lire(Historique.enPdf(
                List.of(new MoteurCalcul.Calcul("2+3", "5"), new MoteurCalcul.Calcul("4−1", "3")),
                LocalDate.of(2026, 11, 23)));
        String vVide = lire(Historique.enPdf(List.of(), LocalDate.of(2026, 11, 23)));

        assertTrue(vPdf.contains("(Historique des calculs) Tj"));
        assertTrue(vPdf.contains("(Édité le 23/11/2026) Tj"));
        assertTrue(vPdf.contains("(2+3 = 5) Tj"));
        assertTrue(vPdf.contains("(4-1 = 3) Tj"));
        assertTrue(vVide.contains("(Aucun calcul) Tj"));
    }
}
