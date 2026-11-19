package com.kalkul;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

/**
 * Feuille de calcul simple : des cellules nommées (« surface = 4.2*3.5 », « prix = 24.9 »), dont la
 * formule peut reprendre les autres cellules. Leurs valeurs servent de variables aux formules
 * enregistrées. Gardée d'une session à l'autre dans les préférences de l'utilisateur.
 */
final class FeuilleCalcul {
    /** Cellules gardées au plus : la feuille reste un pense-bête, pas un tableur. */
    static final int MAX = 12;
    private static final String CLE = "cellules";

    /** Une cellule : son nom et sa formule, sans x. */
    record Cellule(String nom, String formule) {
        @Override
        public String toString() {
            return nom + " = " + formule;
        }
    }

    private final Map<String, String> aCellules = new LinkedHashMap<>();
    private final List<Runnable> aEcouteurs = new ArrayList<>();

    /**
     * Crée ou remplace la cellule pNom. La feuille entière doit encore se calculer après coup,
     * sinon rien ne change et une IllegalArgumentException explique pourquoi.
     */
    void definir(String pNom, String pFormule) {
        String vNom = verifierNom(pNom);
        String vFormule = pFormule == null ? "" : pFormule.trim();
        if (Expression.parse(vFormule).variables().contains("x")) {
            throw new IllegalArgumentException("x est réservé aux formules enregistrées");
        }
        if (!aCellules.containsKey(vNom) && aCellules.size() >= MAX) {
            throw new IllegalArgumentException("La feuille est pleine (" + MAX + " cellules)");
        }
        String vAncienne = aCellules.put(vNom, vFormule);
        try {
            // Une référence circulaire se voit en calculant la cellule et celles qui la reprennent
            for (String vAutre : aCellules.keySet()) {
                if (vAutre.equals(vNom) || dependDe(vAutre, vNom, new HashSet<>())) {
                    valeur(vAutre);
                }
            }
        } catch (IllegalArgumentException | ArithmeticException vException) {
            if (vAncienne == null) {
                aCellules.remove(vNom);
            } else {
                aCellules.put(vNom, vAncienne);
            }
            throw new IllegalArgumentException(vException.getMessage(), vException);
        }
        prevenir();
    }

    void supprimer(String pNom) {
        if (aCellules.remove(pNom.trim().toLowerCase(Locale.ROOT)) != null) {
            prevenir();
        }
    }

    List<Cellule> liste() {
        List<Cellule> rListe = new ArrayList<>();
        aCellules.forEach((pNom, pFormule) -> rListe.add(new Cellule(pNom, pFormule)));
        return rListe;
    }

    /**
     * Valeur de la cellule pNom ; une cellule absente, circulaire ou sans valeur lève une exception.
     */
    double valeur(String pNom) {
        double rValeur = calculer(pNom.trim().toLowerCase(Locale.ROOT), new HashMap<>(), new HashSet<>());
        return rValeur;
    }

    /**
     * Valeurs de toutes les cellules qui se calculent, prêtes à servir de variables. Une cellule cassée
     * (par exemple parce qu'une cellule qu'elle reprend a été retirée) est laissée de côté.
     */
    Map<String, Double> valeurs() {
        Map<String, Double> rValeurs = new HashMap<>();
        for (String vNom : aCellules.keySet()) {
            try {
                calculer(vNom, rValeurs, new HashSet<>());
            } catch (IllegalArgumentException | ArithmeticException vException) {
                // La cellule s'affiche en erreur dans sa liste, les autres restent utilisables
                continue;
            }
        }
        return rValeurs;
    }

    private double calculer(String pNom, Map<String, Double> pConnues, Set<String> pEnCours) {
        Double vConnue = pConnues.get(pNom);
        if (vConnue != null) {
            return vConnue;
        }
        String vFormule = aCellules.get(pNom);
        if (vFormule == null) {
            throw new IllegalArgumentException("Cellule inconnue : " + pNom);
        }
        if (!pEnCours.add(pNom)) {
            throw new IllegalArgumentException("Référence circulaire sur « " + pNom + " »");
        }
        Expression vExpression = Expression.parse(vFormule);
        Map<String, Double> vVariables = new HashMap<>();
        for (String vVariable : vExpression.variables()) {
            vVariables.put(vVariable, calculer(vVariable, pConnues, pEnCours));
        }
        double rValeur = vExpression.evaluate(vVariables);
        if (Double.isNaN(rValeur) || Double.isInfinite(rValeur)) {
            throw new ArithmeticException("La cellule « " + pNom + " » n'a pas de valeur");
        }
        pEnCours.remove(pNom);
        pConnues.put(pNom, rValeur);
        return rValeur;
    }

    // Vrai si pCellule reprend pCible, directement ou par une autre cellule
    private boolean dependDe(String pCellule, String pCible, Set<String> pVues) {
        String vFormule = aCellules.get(pCellule);
        if (vFormule == null || !pVues.add(pCellule)) {
            return false;
        }
        boolean rDepend = false;
        for (String vVariable : Expression.parse(vFormule).variables()) {
            rDepend = rDepend || vVariable.equals(pCible) || dependDe(vVariable, pCible, pVues);
        }
        return rDepend;
    }

    private static String verifierNom(String pNom) {
        String rNom = pNom == null ? "" : pNom.trim().toLowerCase(Locale.ROOT);
        // Les formules lisent un nom comme une suite de lettres : chiffres et _ couperaient le nom,
        // et les accents ne seraient pas remplacés quand le calcul s'écrit avec ses nombres
        if (!rNom.matches("[a-z]+")) {
            throw new IllegalArgumentException("Un nom de cellule ne contient que des lettres sans accent");
        }
        if ("x".equals(rNom) || Expression.nomReserve(rNom)) {
            throw new IllegalArgumentException("« " + rNom + " » est réservé");
        }
        return rNom;
    }

    void ecouter(Runnable pEcouteur) {
        aEcouteurs.add(pEcouteur);
    }

    private void prevenir() {
        aEcouteurs.forEach(Runnable::run);
    }

    /** Une cellule par ligne, formule après une tabulation. */
    String texte() {
        StringBuilder rTexte = new StringBuilder();
        aCellules.forEach((pNom, pFormule) -> rTexte.append(pNom).append('\t').append(pFormule).append('\n'));
        return rTexte.toString();
    }

    /**
     * Relit des cellules écrites par {@link #texte()} ; les lignes illisibles sont ignorées.
     */
    void lire(String pTexte) {
        aCellules.clear();
        for (String vLigne : pTexte.split("\n")) {
            String[] vParties = vLigne.split("\t", 2);
            if (vParties.length < 2 || aCellules.size() >= MAX) {
                continue;
            }
            try {
                String vNom = verifierNom(vParties[0]);
                Expression.parse(vParties[1]);
                aCellules.put(vNom, vParties[1].trim());
            } catch (IllegalArgumentException vException) {
                // Une ligne abîmée ne doit pas faire perdre les autres
                continue;
            }
        }
        prevenir();
    }

    /** Feuille relue depuis les préférences, et réécrite à chaque changement. */
    static FeuilleCalcul depuis(Preferences pPreferences) {
        FeuilleCalcul rFeuille = new FeuilleCalcul();
        rFeuille.lire(pPreferences.get(CLE, ""));
        rFeuille.ecouter(() -> pPreferences.put(CLE, rFeuille.texte()));
        return rFeuille;
    }
}
