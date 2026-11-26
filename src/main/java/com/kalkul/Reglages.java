package com.kalkul;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.EnumMap;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Réglages gardés d'une session à l'autre : pour l'instant, le déclic des touches (coupé au départ).
 */
final class Reglages {
    static final String SON = "son_touches";
    static final String THEME = "theme";
    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(Kalkul.class);

    private Reglages() {
    }

    static boolean sonDesTouches() {
        return PREFERENCES.getBoolean(SON, false);
    }

    static void sonDesTouches(boolean pActif) {
        PREFERENCES.putBoolean(SON, pActif);
    }

    /** Thème gardé : « auto » (comme Windows), « clair » ou « sombre » ; pris au prochain démarrage. */
    static String theme() {
        String rTheme = PREFERENCES.get(THEME, "auto");
        return rTheme;
    }

    static void theme(String pTheme) {
        PREFERENCES.put(THEME, pTheme);
    }

    // Un champ par onglet : Entrée enregistre, un raccourci refusé garde l'ancien et dit pourquoi
    private static JPanel raccourcis() {
        Raccourcis vRaccourcis = Raccourcis.partage();
        JPanel rPanneau = new JPanel(new GridLayout(0, 2, 6, 2));
        rPanneau.setOpaque(false);
        rPanneau.setBorder(BorderFactory.createTitledBorder("Raccourcis des onglets (Entrée pour enregistrer)"));
        JLabel vMessage = new JLabel(" ");
        Map<Raccourcis.Onglet, JTextField> vChamps = new EnumMap<>(Raccourcis.Onglet.class);
        for (Raccourcis.Onglet vOnglet : Raccourcis.Onglet.values()) {
            JTextField vChamp = new JTextField(vRaccourcis.texte(vOnglet), 12);
            vChamps.put(vOnglet, vChamp);
            vChamp.addActionListener(pEvent -> {
                try {
                    vRaccourcis.definir(vOnglet, vChamp.getText());
                    vMessage.setText(" ");
                } catch (IllegalArgumentException vException) {
                    vChamp.setText(vRaccourcis.texte(vOnglet));
                    vMessage.setText(vException.getMessage());
                }
            });
            rPanneau.add(new JLabel(vOnglet.titre()));
            rPanneau.add(vChamp);
        }
        JButton vOrigine = new JButton("Remettre les raccourcis d'origine");
        vOrigine.addActionListener(pEvent -> {
            vRaccourcis.reinitialiser();
            vChamps.forEach((pOnglet, pChamp) -> pChamp.setText(vRaccourcis.texte(pOnglet)));
            vMessage.setText(" ");
        });
        JPanel rBloc = new JPanel(new BorderLayout(0, 4));
        rBloc.setOpaque(false);
        rBloc.add(rPanneau, BorderLayout.CENTER);
        JPanel vBas = new JPanel(new GridLayout(0, 1));
        vBas.setOpaque(false);
        vBas.add(vOrigine);
        vBas.add(vMessage);
        rBloc.add(vBas, BorderLayout.SOUTH);
        return rBloc;
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
        String[] vValeurs = {"auto", "clair", "sombre"};
        JComboBox<String> vTheme = new JComboBox<>(new String[] {"Comme Windows", "Clair", "Sombre"});
        vTheme.setSelectedIndex(Math.max(0, java.util.Arrays.asList(vValeurs).indexOf(theme())));
        vTheme.addActionListener(pEvent -> theme(vValeurs[vTheme.getSelectedIndex()]));
        rSection.add(PanneauOutils.row(new JLabel("Thème (au prochain démarrage)"), vTheme));
        rSection.add(raccourcis());
        return rSection;
    }
}
