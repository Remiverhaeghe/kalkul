package com.kalkul;

import java.util.prefs.Preferences;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 * Réglages gardés d'une session à l'autre : pour l'instant, le déclic des touches (coupé au départ).
 */
final class Reglages {
    static final String SON = "son_touches";
    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(Kalkul.class);

    private Reglages() {
    }

    static boolean sonDesTouches() {
        return PREFERENCES.getBoolean(SON, false);
    }

    static void sonDesTouches(boolean pActif) {
        PREFERENCES.putBoolean(SON, pActif);
    }

    /**
     * Sous-onglet Réglages de l'onglet Outils.
     */
    static JPanel construire() {
        JPanel rSection = PanneauOutils.section("Réglages");
        JCheckBox vSon = new JCheckBox("Déclic à chaque touche, clavier compris", sonDesTouches());
        vSon.setOpaque(false);
        vSon.addActionListener(pEvent -> sonDesTouches(vSon.isSelected()));
        rSection.add(PanneauOutils.row(vSon));
        return rSection;
    }
}
