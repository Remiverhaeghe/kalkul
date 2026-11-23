package com.kalkul;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.KeyStroke;

/**
 * Raccourcis clavier qui ouvrent chaque onglet, réglables et gardés d'une session à l'autre. Un raccourci
 * s'écrit comme dans {@link KeyStroke#getKeyStroke(String)} (« ctrl 1 », « alt shift H »). Il doit comporter
 * un modificateur, sinon il volerait les touches de la calculatrice, et ne servir qu'à un seul onglet.
 */
final class Raccourcis {

    /** Onglets de la fenêtre, dans l'ordre d'affichage ; le raccourci d'origine est Ctrl + son rang. */
    enum Onglet {
        BASE("Base"), SCIENTIFIQUE("Scientifique"), FINANCIER("Financier"), GRAPHIQUE("Graphique"),
        PROGRAMMEUR("Programmeur"), CHANTIER("Chantier"), OUTILS("Outils"), FEUILLE("Feuille"),
        HISTORIQUE("Historique");

        private final String aTitre;

        Onglet(String pTitre) {
            aTitre = pTitre;
        }

        String titre() {
            return aTitre;
        }

        String parDefaut() {
            return "ctrl " + (ordinal() + 1);
        }
    }

    private static final String CLE = "raccourci_";
    private final Preferences aPreferences;
    private static Raccourcis aPartage;
    private final Map<Onglet, KeyStroke> aTouches = new EnumMap<>(Onglet.class);
    private final List<Runnable> aEcouteurs = new ArrayList<>();

    private Raccourcis(Preferences pPreferences) {
        aPreferences = pPreferences;
        for (Onglet vOnglet : Onglet.values()) {
            KeyStroke vGardee = lire(aPreferences.get(CLE + vOnglet.name(), vOnglet.parDefaut()));
            aTouches.put(vOnglet, vGardee != null ? vGardee : lire(vOnglet.parDefaut()));
        }
    }

    /** Raccourcis gardés dans les préférences données. */
    static Raccourcis depuis(Preferences pPreferences) {
        return new Raccourcis(pPreferences);
    }

    /** Raccourcis de l'application, gardés dans les préférences de Kalkul et partagés par la fenêtre et les réglages. */
    static synchronized Raccourcis partage() {
        if (aPartage == null) {
            aPartage = depuis(Preferences.userNodeForPackage(Kalkul.class));
        }
        return aPartage;
    }

    /** Appelle l'action à chaque changement de raccourci. */
    void ecouter(Runnable pAction) {
        aEcouteurs.add(pAction);
    }

    KeyStroke touche(Onglet pOnglet) {
        return aTouches.get(pOnglet);
    }

    /** Texte du raccourci tel qu'il se saisit et s'enregistre, par exemple « ctrl pressed 1 » devient « ctrl 1 ». */
    String texte(Onglet pOnglet) {
        return ecrire(aTouches.get(pOnglet));
    }

    /**
     * Change le raccourci d'un onglet et le garde.
     *
     * @throws IllegalArgumentException si le texte n'est pas un raccourci, n'a pas de modificateur ou sert
     *                                  déjà à un autre onglet
     */
    void definir(Onglet pOnglet, String pTexte) {
        KeyStroke vTouche = lire(pTexte);
        if (vTouche == null) {
            throw new IllegalArgumentException("Raccourci illisible : « " + pTexte + " » (exemple : ctrl 1)");
        }
        if (vTouche.getModifiers() == 0) {
            throw new IllegalArgumentException("Ajoute ctrl, alt ou shift : une touche seule sert à la calculatrice");
        }
        for (Map.Entry<Onglet, KeyStroke> vAutre : aTouches.entrySet()) {
            if (vAutre.getKey() != pOnglet && vAutre.getValue().equals(vTouche)) {
                throw new IllegalArgumentException("Déjà pris par l'onglet " + vAutre.getKey().titre());
            }
        }
        aTouches.put(pOnglet, vTouche);
        aPreferences.put(CLE + pOnglet.name(), ecrire(vTouche));
        aEcouteurs.forEach(Runnable::run);
    }

    /** Remet les raccourcis d'origine. */
    void reinitialiser() {
        for (Onglet vOnglet : Onglet.values()) {
            aTouches.put(vOnglet, lire(vOnglet.parDefaut()));
            aPreferences.remove(CLE + vOnglet.name());
        }
        aEcouteurs.forEach(Runnable::run);
    }

    private static KeyStroke lire(String pTexte) {
        return pTexte == null ? null : KeyStroke.getKeyStroke(pTexte.trim());
    }

    // « ctrl pressed 1 » devient « ctrl 1 » : la forme de toString() ne se relit pas telle quelle
    private static String ecrire(KeyStroke pTouche) {
        return pTouche.toString().replace("pressed ", "");
    }
}
