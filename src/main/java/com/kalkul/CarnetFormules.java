package com.kalkul;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Formules enregistrées, partagées entre les onglets : tracées dans Graphique, appliquées au nombre
 * de l'écran dans Scientifique. Gardées d'une session à l'autre dans les préférences de l'utilisateur.
 */
final class CarnetFormules {
    /** Formules gardées au plus, les plus anciennes sortent en premier. */
    static final int MAX = 20;
    private static final String CLE = "formules";

    /** Une formule de x et ses paramètres, écrits comme dans l'onglet Graphique (« a=2; b=1 »). */
    record Formule(String formule, String parametres) {
        @Override
        public String toString() {
            String rTexte = parametres.isBlank() ? formule : formule + "   (" + parametres + ")";
            return rTexte;
        }
    }

    private final List<Formule> aFormules = new ArrayList<>();
    private final List<Runnable> aEcouteurs = new ArrayList<>();

    /**
     * Enregistre une formule en tête de liste ; une formule déjà là remonte au lieu d'être doublée.
     * La formule doit se lire, sinon une IllegalArgumentException explique pourquoi.
     */
    void enregistrer(String pFormule, String pParametres) {
        String vFormule = pFormule.trim();
        String vParametres = pParametres == null ? "" : pParametres.trim();
        Expression.parse(vFormule);
        Expression.parseParameters(vParametres);
        Formule vNouvelle = new Formule(vFormule, vParametres);
        aFormules.remove(vNouvelle);
        aFormules.add(0, vNouvelle);
        while (aFormules.size() > MAX) {
            aFormules.remove(aFormules.size() - 1);
        }
        prevenir();
    }

    void supprimer(Formule pFormule) {
        if (aFormules.remove(pFormule)) {
            prevenir();
        }
    }

    List<Formule> liste() {
        List<Formule> rListe = List.copyOf(aFormules);
        return rListe;
    }

    /** Appelé à chaque changement, pour que tous les onglets montrent la même liste. */
    void ecouter(Runnable pEcouteur) {
        aEcouteurs.add(pEcouteur);
    }

    private void prevenir() {
        aEcouteurs.forEach(Runnable::run);
    }

    /**
     * Valeur de la formule pour pX, avec ses paramètres.
     */
    static double appliquer(Formule pFormule, double pX) {
        double rValeur = Expression.parse(pFormule.formule())
                .bind(Expression.parseParameters(pFormule.parametres()))
                .applyAsDouble(pX);
        if (Double.isNaN(rValeur) || Double.isInfinite(rValeur)) {
            throw new ArithmeticException("La formule n'a pas de valeur pour x = " + Affichage.format(pX));
        }
        return rValeur;
    }

    /**
     * Formule écrite avec ses nombres à la place de x et des paramètres : « 2*3 + 1 », « (-3)^2 ».
     */
    static String ecrire(Formule pFormule, double pX) {
        Map<String, Double> vValeurs = new HashMap<>(Expression.parseParameters(pFormule.parametres()));
        vValeurs.put("x", pX);
        Matcher vMots = Pattern.compile("\\b[A-Za-z_]\\w*\\b").matcher(pFormule.formule());
        StringBuilder rTexte = new StringBuilder();
        while (vMots.find()) {
            Double vValeur = vValeurs.get(vMots.group());
            String vTexte = vMots.group();
            if (vValeur != null) {
                vTexte = vValeur < 0 ? "(" + Affichage.format(vValeur) + ")" : Affichage.format(vValeur);
            }
            vMots.appendReplacement(rTexte, Matcher.quoteReplacement(vTexte));
        }
        vMots.appendTail(rTexte);
        return rTexte.toString();
    }

    /** Une formule par ligne, paramètres après une tabulation. */
    String texte() {
        StringBuilder rTexte = new StringBuilder();
        for (Formule vFormule : aFormules) {
            rTexte.append(vFormule.formule()).append('\t').append(vFormule.parametres()).append('\n');
        }
        return rTexte.toString();
    }

    /**
     * Relit des formules écrites par {@link #texte()} ; les lignes illisibles sont ignorées.
     */
    void lire(String pTexte) {
        aFormules.clear();
        for (String vLigne : pTexte.split("\n")) {
            String[] vParties = vLigne.split("\t", 2);
            if (vParties[0].isBlank() || aFormules.size() >= MAX) {
                continue;
            }
            try {
                Expression.parse(vParties[0]);
                Formule vFormule = new Formule(vParties[0].trim(), vParties.length > 1 ? vParties[1].trim() : "");
                if (!aFormules.contains(vFormule)) {
                    aFormules.add(vFormule);
                }
            } catch (IllegalArgumentException vException) {
                // Une ligne abîmée ne doit pas faire perdre les autres
                continue;
            }
        }
        prevenir();
    }

    /** Carnet relu depuis les préférences, et réécrit à chaque changement. */
    static CarnetFormules depuis(Preferences pPreferences) {
        CarnetFormules rCarnet = new CarnetFormules();
        rCarnet.lire(pPreferences.get(CLE, ""));
        rCarnet.ecouter(() -> pPreferences.put(CLE, rCarnet.texte()));
        return rCarnet;
    }
}
