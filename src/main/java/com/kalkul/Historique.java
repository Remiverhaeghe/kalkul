package com.kalkul;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Recherche dans l'historique et export en CSV, à part de l'onglet Swing pour rester testable. */
final class Historique {

    /** Séparateur lu par un tableur réglé en français, où la virgule sert aux décimales. */
    static final char SEPARATEUR = ';';

    private Historique() {
    }

    /**
     * Calculs dont le calcul ou le résultat contient le texte cherché, sans tenir compte des majuscules ni
     * des espaces ; « * », « / » et « - » trouvent aussi « × », « ÷ » et « − ». Un texte vide garde tout.
     */
    static List<MoteurCalcul.Calcul> filtrer(List<MoteurCalcul.Calcul> pCalculs, String pRecherche) {
        String vCherche = normaliser(pRecherche);
        return pCalculs.stream()
                .filter(pCalcul -> vCherche.isEmpty() || normaliser(pCalcul.expression()).contains(vCherche)
                        || normaliser(pCalcul.resultat()).contains(vCherche))
                .toList();
    }

    /** Historique en CSV, une ligne de titres puis un calcul par ligne, dans l'ordre donné. */
    static String enCsv(List<MoteurCalcul.Calcul> pCalculs) {
        StringBuilder rCsv = new StringBuilder("Calcul").append(SEPARATEUR).append("Résultat\r\n");
        for (MoteurCalcul.Calcul vCalcul : pCalculs) {
            rCsv.append(champ(vCalcul.expression())).append(SEPARATEUR).append(champ(vCalcul.resultat()))
                    .append("\r\n");
        }
        return rCsv.toString();
    }

    /**
     * Historique en PDF : un titre, la date, puis une ligne « calcul = résultat » par calcul. Plusieurs pages
     * au besoin ; un historique vide donne une page avec la mention « Aucun calcul ».
     */
    static byte[] enPdf(List<MoteurCalcul.Calcul> pCalculs, LocalDate pDate) {
        List<String> vLignes = new ArrayList<>();
        vLignes.add("Édité le " + pDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        vLignes.add("");
        if (pCalculs.isEmpty()) {
            vLignes.add("Aucun calcul");
        }
        for (MoteurCalcul.Calcul vCalcul : pCalculs) {
            // Le vrai signe moins (−) n'existe pas en Windows-1252 : il est écrit « - » dans le PDF
            vLignes.add((vCalcul.expression() + " = " + vCalcul.resultat()).replace('−', '-'));
        }
        return PdfTexte.ecrire("Historique des calculs", vLignes);
    }

    // Guillemets autour d'un champ qui contient le séparateur, un guillemet ou un saut de ligne
    private static String champ(String pTexte) {
        String rChamp = pTexte;
        if (pTexte.indexOf(SEPARATEUR) >= 0 || pTexte.contains("\"") || pTexte.contains("\n")) {
            rChamp = "\"" + pTexte.replace("\"", "\"\"") + "\"";
        }
        return rChamp;
    }

    private static String normaliser(String pTexte) {
        return pTexte.toLowerCase(Locale.ROOT).replaceAll("\\s+", "").replace('*', '×').replace('/', '÷')
                .replace('-', '−');
    }
}
