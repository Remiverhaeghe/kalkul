package com.kalkul;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.Preferences;

/**
 * Taux de change gardés sur l'ordinateur, sans connexion : un taux par devise pour un euro, et la date
 * du jour où ils valaient. Ils se tapent à la main ou se lisent dans un fichier : celui de la Banque
 * centrale européenne (eurofxref.csv, à télécharger soi-même) ou une liste « USD;1,08 ».
 */
final class Devises {

    static final String EURO = "EUR";

    /** Au-delà, les taux sont signalés comme anciens. */
    static final int JOURS_AVANT_ALERTE = 7;

    private static final String CLE = "devises";
    private static final DateTimeFormatter DATE_BCE = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);

    private final Map<String, Double> aTaux = new TreeMap<>();
    private LocalDate aDate;
    private Runnable aEcoute = () -> {
    };

    Devises() {
        aTaux.put(EURO, 1.0);
    }

    /** Devises connues, l'euro en premier puis par ordre alphabétique. */
    List<String> codes() {
        List<String> rCodes = new ArrayList<>();
        rCodes.add(EURO);
        aTaux.keySet().stream().filter(pCode -> !pCode.equals(EURO)).forEach(rCodes::add);
        return rCodes;
    }

    /** Jour où les taux valaient ; null tant qu'aucun n'a été saisi. */
    LocalDate date() {
        return aDate;
    }

    Double taux(String pCode) {
        return aTaux.get(pCode);
    }

    /** Montant converti d'une devise à l'autre, en passant par l'euro. */
    double convertir(double pMontant, String pDe, String pVers) {
        Double vDe = aTaux.get(pDe);
        Double vVers = aTaux.get(pVers);
        if (vDe == null || vVers == null) {
            throw new IllegalArgumentException("devise sans taux");
        }
        return pMontant / vDe * vVers;
    }

    /** Vrai quand les taux ont plus de {@link #JOURS_AVANT_ALERTE} jours au jour donné. */
    boolean anciens(LocalDate pAujourdhui) {
        return aDate != null && ChronoUnit.DAYS.between(aDate, pAujourdhui) > JOURS_AVANT_ALERTE;
    }

    /** Taux tapé à la main : combien vaut un euro dans cette devise, ce jour-là. */
    void saisir(String pCode, double pParEuro, LocalDate pDate) {
        String vCode = code(pCode);
        if (vCode.equals(EURO) || !(pParEuro > 0) || Double.isInfinite(pParEuro)) {
            throw new IllegalArgumentException("taux invalide");
        }
        aTaux.put(vCode, pParEuro);
        aDate = pDate;
        aEcoute.run();
    }

    /** Remplace les taux par ceux d'un fichier ; renvoie le nombre de devises lues. */
    int importer(String pTexte, LocalDate pAujourdhui) {
        Devises vLues = lire(pTexte, pAujourdhui);
        if (vLues.aTaux.size() <= 1) {
            throw new IllegalArgumentException("aucun taux reconnu dans le fichier");
        }
        aTaux.clear();
        aTaux.putAll(vLues.aTaux);
        aDate = vLues.aDate;
        aEcoute.run();
        return aTaux.size() - 1;
    }

    /** Texte gardé dans les préférences : la date puis une ligne par devise. */
    String texte() {
        StringBuilder rTexte = new StringBuilder();
        if (aDate != null) {
            rTexte.append("date;").append(aDate).append('\n');
        }
        aTaux.forEach((pCode, pTaux) -> {
            if (!pCode.equals(EURO)) {
                rTexte.append(pCode).append(';').append(pTaux).append('\n');
            }
        });
        return rTexte.toString();
    }

    /**
     * Taux lus dans le format de la BCE (une ligne de titres « Date, USD, JPY… » puis une ligne de valeurs)
     * ou dans une liste de lignes « code;taux », avec une ligne « date;2026-11-20 » au choix. Sans date
     * dans le fichier, les taux sont datés du jour. Les lignes illisibles sont sautées.
     */
    static Devises lire(String pTexte, LocalDate pAujourdhui) {
        Devises rDevises = new Devises();
        rDevises.aDate = pAujourdhui;
        List<String> vLignes = pTexte.lines().map(String::strip).filter(pLigne -> !pLigne.isEmpty()).toList();
        if (!vLignes.isEmpty() && vLignes.get(0).toLowerCase(Locale.ROOT).startsWith("date,") && vLignes.size() > 1) {
            lireBce(rDevises, vLignes.get(0).split(","), vLignes.get(1).split(","));
        } else {
            for (String vLigne : vLignes) {
                lireLigne(rDevises, vLigne);
            }
        }
        return rDevises;
    }

    private static void lireBce(Devises pDevises, String[] pTitres, String[] pValeurs) {
        try {
            pDevises.aDate = LocalDate.parse(pValeurs[0].strip(), DATE_BCE);
        } catch (DateTimeParseException vException) {
            // Date illisible : les taux gardent la date du jour
        }
        for (int vIndex = 1; vIndex < Math.min(pTitres.length, pValeurs.length); vIndex++) {
            ajouter(pDevises, pTitres[vIndex], pValeurs[vIndex].strip());
        }
    }

    private static void lireLigne(Devises pDevises, String pLigne) {
        String[] vParties = pLigne.split("\\s*[;=\\t]\\s*", 2);
        if (vParties.length < 2) {
            return;
        }
        if (vParties[0].equalsIgnoreCase("date")) {
            try {
                pDevises.aDate = LocalDate.parse(vParties[1].strip());
            } catch (DateTimeParseException vException) {
                // Date illisible : les taux gardent la date du jour
            }
            return;
        }
        ajouter(pDevises, vParties[0], vParties[1].strip().replace(',', '.'));
    }

    private static void ajouter(Devises pDevises, String pCode, String pTaux) {
        String vCode = code(pCode);
        try {
            double vTaux = Double.parseDouble(pTaux);
            if (vCode.matches("[A-Z]{3}") && !vCode.equals(EURO) && vTaux > 0 && !Double.isInfinite(vTaux)) {
                pDevises.aTaux.put(vCode, vTaux);
            }
        } catch (NumberFormatException vException) {
            // « N/A » dans le fichier de la BCE pour une devise suspendue
        }
    }

    private static String code(String pCode) {
        return pCode.strip().toUpperCase(Locale.ROOT);
    }

    private void ecouter(Runnable pEcoute) {
        aEcoute = pEcoute;
    }

    /** Taux relus depuis les préférences, et réécrits à chaque changement. */
    static Devises depuis(Preferences pPreferences, LocalDate pAujourdhui) {
        String vTexte = pPreferences.get(CLE, "");
        Devises rDevises = lire(vTexte, pAujourdhui);
        if (!vTexte.contains("date;")) {
            rDevises.aDate = null;
        }
        rDevises.ecouter(() -> pPreferences.put(CLE, rDevises.texte()));
        return rDevises;
    }
}
