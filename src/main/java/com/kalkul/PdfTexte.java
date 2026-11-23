package com.kalkul;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Écrit un PDF de texte seul (Helvetica, A4, plusieurs pages si besoin) sans bibliothèque. Le texte est
 * encodé en Windows-1252, ce qui couvre le français.
 */
final class PdfTexte {
    private static final Charset WIN_ANSI = Charset.forName("windows-1252");
    private static final int LARGEUR = 595;
    private static final int HAUTEUR = 842;
    private static final int MARGE = 50;
    private static final int TAILLE_TITRE = 16;
    private static final int TAILLE = 10;
    private static final int INTERLIGNE = 14;
    /** Lignes de texte d'une page, titre exclu (il n'est écrit que sur la première). */
    static final int LIGNES_PAR_PAGE = (HAUTEUR - 2 * MARGE) / INTERLIGNE;

    private PdfTexte() {
    }

    /** Document d'un titre, puis une ligne de texte par élément de la liste. */
    static byte[] ecrire(String pTitre, List<String> pLignes) {
        List<List<String>> vPages = decouper(pLignes);
        List<byte[]> vObjets = new ArrayList<>();
        int vPremierePage = 5;
        StringBuilder vKids = new StringBuilder();
        for (int vIndex = 0; vIndex < vPages.size(); vIndex++) {
            vKids.append(vIndex == 0 ? "" : " ").append(vPremierePage + 2 * vIndex).append(" 0 R");
        }
        vObjets.add(octets("<< /Type /Catalog /Pages 2 0 R >>"));
        vObjets.add(octets("<< /Type /Pages /Kids [" + vKids + "] /Count " + vPages.size() + " >>"));
        vObjets.add(octets("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>"));
        vObjets.add(octets("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>"));
        for (int vIndex = 0; vIndex < vPages.size(); vIndex++) {
            byte[] vFlux = flux(vIndex == 0 ? pTitre : null, vPages.get(vIndex));
            vObjets.add(octets("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + LARGEUR + " " + HAUTEUR
                    + "] /Resources << /Font << /F1 3 0 R /F2 4 0 R >> >> /Contents " + (vPremierePage + 2 * vIndex + 1)
                    + " 0 R >>"));
            vObjets.add(concat(octets("<< /Length " + vFlux.length + " >>\nstream\n"), vFlux, octets("\nendstream")));
        }
        return assembler(vObjets);
    }

    private static List<List<String>> decouper(List<String> pLignes) {
        List<List<String>> rPages = new ArrayList<>();
        int vPlace = LIGNES_PAR_PAGE - 3; // le titre prend la place de trois lignes sur la première page
        int vDebut = 0;
        do {
            int vFin = Math.min(pLignes.size(), vDebut + vPlace);
            rPages.add(new ArrayList<>(pLignes.subList(vDebut, vFin)));
            vDebut = vFin;
            vPlace = LIGNES_PAR_PAGE;
        } while (vDebut < pLignes.size());
        return rPages;
    }

    private static byte[] flux(String pTitre, List<String> pLignes) {
        StringBuilder rFlux = new StringBuilder();
        int vY = HAUTEUR - MARGE;
        if (pTitre != null) {
            vY -= TAILLE_TITRE;
            rFlux.append("BT /F2 ").append(TAILLE_TITRE).append(" Tf ").append(MARGE).append(' ').append(vY)
                    .append(" Td (").append(echapper(pTitre)).append(") Tj ET\n");
            vY -= 2 * INTERLIGNE;
        }
        for (String vLigne : pLignes) {
            vY -= INTERLIGNE;
            rFlux.append("BT /F1 ").append(TAILLE).append(" Tf ").append(MARGE).append(' ').append(vY).append(" Td (")
                    .append(echapper(vLigne)).append(") Tj ET\n");
        }
        return rFlux.toString().getBytes(WIN_ANSI);
    }

    // Les parenthèses et la barre oblique sont protégées par une barre oblique dans une chaîne PDF
    static String echapper(String pTexte) {
        return pTexte.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)").replace("\r", "").replace("\n", " ");
    }

    private static byte[] assembler(List<byte[]> pObjets) {
        ByteArrayOutputStream rPdf = new ByteArrayOutputStream();
        List<Integer> vPositions = new ArrayList<>();
        ecrireOctets(rPdf, octets("%PDF-1.4\n"));
        for (int vIndex = 0; vIndex < pObjets.size(); vIndex++) {
            vPositions.add(rPdf.size());
            ecrireOctets(rPdf, octets((vIndex + 1) + " 0 obj\n"));
            ecrireOctets(rPdf, pObjets.get(vIndex));
            ecrireOctets(rPdf, octets("\nendobj\n"));
        }
        int vXref = rPdf.size();
        StringBuilder vTable = new StringBuilder("xref\n0 " + (pObjets.size() + 1) + "\n0000000000 65535 f \n");
        for (int vPosition : vPositions) {
            vTable.append(String.format("%010d 00000 n \n", vPosition));
        }
        vTable.append("trailer\n<< /Size ").append(pObjets.size() + 1).append(" /Root 1 0 R >>\nstartxref\n")
                .append(vXref).append("\n%%EOF\n");
        ecrireOctets(rPdf, octets(vTable.toString()));
        return rPdf.toByteArray();
    }

    private static void ecrireOctets(ByteArrayOutputStream pSortie, byte[] pOctets) {
        pSortie.write(pOctets, 0, pOctets.length);
    }

    private static byte[] octets(String pTexte) {
        return pTexte.getBytes(WIN_ANSI);
    }

    private static byte[] concat(byte[]... pMorceaux) {
        ByteArrayOutputStream rSortie = new ByteArrayOutputStream();
        for (byte[] vMorceau : pMorceaux) {
            ecrireOctets(rSortie, vMorceau);
        }
        return rSortie.toByteArray();
    }
}
