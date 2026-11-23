package com.kalkul;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.Preferences;

/**
 * Valeurs gardées sous un nom (« prix », « surface »), d'une session à l'autre, et réutilisables dans une
 * formule : {@code prix * 3} lit la valeur de « prix ». Séparé du Swing pour rester testable.
 */
final class Memoires {

    private static final String CLE = "memoires";

    private final Map<String, Double> aValeurs = new TreeMap<>();
    private Runnable aEcoute = () -> {
    };

    /** Noms gardés, par ordre alphabétique. */
    List<String> noms() {
        return new ArrayList<>(aValeurs.keySet());
    }

    Double valeur(String pNom) {
        return aValeurs.get(nom(pNom));
    }

    /** Calcule la formule avec les mémoires comme variables et garde le résultat sous le nom donné. */
    double enregistrer(String pNom, String pFormule) {
        String vNom = nom(pNom);
        if (vNom.isEmpty() || !vNom.chars().allMatch(Character::isLetter)) {
            throw new IllegalArgumentException("nom invalide : des lettres seulement");
        }
        if (Expression.nomReserve(vNom) || vNom.equals("x")) {
            throw new IllegalArgumentException("« " + vNom + " » est déjà pris par une fonction, une constante ou x");
        }
        double rValeur = evaluer(pFormule);
        if (Double.isNaN(rValeur) || Double.isInfinite(rValeur)) {
            throw new IllegalArgumentException("résultat sans valeur");
        }
        aValeurs.put(vNom, rValeur);
        aEcoute.run();
        return rValeur;
    }

    /** Valeur d'une formule qui peut citer des mémoires ; une mémoire inconnue lève une exception. */
    double evaluer(String pFormule) {
        Expression vExpression = Expression.parse(pFormule.replace(',', '.'));
        for (String vVariable : vExpression.variables()) {
            if (!aValeurs.containsKey(vVariable.toLowerCase(Locale.ROOT))) {
                throw new IllegalArgumentException("mémoire inconnue : " + vVariable);
            }
        }
        return vExpression.evaluate(aValeurs);
    }

    boolean supprimer(String pNom) {
        boolean rSupprimee = aValeurs.remove(nom(pNom)) != null;
        if (rSupprimee) {
            aEcoute.run();
        }
        return rSupprimee;
    }

    /** Une mémoire par ligne, « nom;valeur ». */
    String texte() {
        StringBuilder rTexte = new StringBuilder();
        aValeurs.forEach((pNom, pValeur) -> rTexte.append(pNom).append(';').append(pValeur).append('\n'));
        return rTexte.toString();
    }

    static Memoires lire(String pTexte) {
        Memoires rMemoires = new Memoires();
        for (String vLigne : pTexte.split("\n")) {
            String[] vPartie = vLigne.split(";");
            if (vPartie.length == 2 && !vPartie[0].isBlank()) {
                try {
                    rMemoires.aValeurs.put(nom(vPartie[0]), Double.parseDouble(vPartie[1]));
                } catch (NumberFormatException vException) {
                    // Ligne abîmée : on garde les autres mémoires
                }
            }
        }
        return rMemoires;
    }

    /** Mémoires relues depuis les préférences, et réécrites à chaque changement. */
    static Memoires depuis(Preferences pPreferences) {
        Memoires rMemoires = lire(pPreferences.get(CLE, ""));
        rMemoires.aEcoute = () -> pPreferences.put(CLE, rMemoires.texte());
        return rMemoires;
    }

    private static String nom(String pNom) {
        return pNom.strip().toLowerCase(Locale.ROOT);
    }
}
