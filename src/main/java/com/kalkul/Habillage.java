package com.kalkul;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Habillage de Kalkul, inspiré des calculatrices de bureau des années 70 : corps noir mat, touches
 * rondes en relief, opérateurs olive, « = » moutarde et écran à cristaux liquides gris-vert.
 * Version gris clair quand Windows est en mode clair. Tout passe par Swing, sans bibliothèque de thème.
 */
final class Habillage {
    /** Thème clair, décidé une fois au démarrage (voir {@link ThemeSysteme}). */
    static final boolean CLAIR = ThemeSysteme.estClair();

    static final Color FOND = couleur(0x1A1A18, 0xD9D6CE);
    static final Color SURFACE = couleur(0x22221F, 0xE6E3DC);
    static final Color CHAMP = couleur(0x121211, 0xF7F5F0);
    static final Color BORDURE = couleur(0x3A3934, 0xB5B1A7);
    static final Color TEXTE = couleur(0xECE9E1, 0x1E1D1A);
    static final Color TEXTE_DOUX = couleur(0x9C988D, 0x5E5A51);
    // Écran à cristaux liquides : fond gris-vert, segments presque noirs, dans les deux thèmes
    static final Color ECRAN = couleur(0x9DA58C, 0xA9B197);
    static final Color CHIFFRES = couleur(0x1C2118, 0x1B2016);
    static final Color MOUTARDE = couleur(0xE2B33C, 0xD9A52A);
    static final Color ORANGE = couleur(0xC4572C, 0xB8502E);
    static final Color OLIVE = couleur(0x5C6447, 0x6B7352);
    static final Color CADRE_ECRAN = couleur(0x0B0B0A, 0x3A3934);
    static final Color MODE_ECRAN = couleur(0x4D5543, 0x4D5543);
    static final Color ERREUR_ECRAN = couleur(0x7A2414, 0x7A2414);
    static final Color GRILLE = couleur(0x8E967E, 0x98A086);

    /** Rôle d'une touche, qui fixe sa couleur. */
    enum Role {
        CHIFFRE(couleur(0x2F2E2B, 0xF4F2EC), couleur(0xF1EEE6, 0x1E1D1A)),
        OPERATEUR(OLIVE, couleur(0xF1EEE6, 0xFAF8F2)),
        FONCTION(couleur(0x252522, 0xC9C5BB), couleur(0xB9B4A6, 0x2B2A26)),
        EGAL(MOUTARDE, couleur(0x1A1A18, 0x1E1D1A)),
        EFFACER(ORANGE, couleur(0xFAF8F2, 0xFAF8F2));

        final Color aFond;
        final Color aTexte;

        Role(Color pFond, Color pTexte) {
            aFond = pFond;
            aTexte = pTexte;
        }
    }

    private static final Set<String> OPERATEURS = Set.of("+", "-", "*", "/", "^", "%", "mod");
    // Signes typographiques affichés sur les touches ; le moteur reçoit toujours le texte d'origine
    private static final Map<String, String> SIGNES = Map.of("/", "÷", "*", "×", "-", "−");
    private static final Set<String> EFFACEMENT = Set.of("C", "⌫");

    private Habillage() {
    }

    /**
     * Couleur du thème en cours : la première en sombre, la seconde en clair.
     */
    static Color couleur(int pSombre, int pClair) {
        Color rCouleur = new Color(CLAIR ? pClair : pSombre);
        return rCouleur;
    }

    /**
     * Rôle d'une touche d'après son texte : chiffres, opérateurs, égal, effacement, le reste est une fonction.
     */
    static Role role(String pLabel) {
        Role rRole;
        if ("=".equals(pLabel)) {
            rRole = Role.EGAL;
        } else if (EFFACEMENT.contains(pLabel)) {
            rRole = Role.EFFACER;
        } else if (OPERATEURS.contains(pLabel)) {
            rRole = Role.OPERATEUR;
        } else if (pLabel.matches("[0-9.]")) {
            rRole = Role.CHIFFRE;
        } else {
            rRole = Role.FONCTION;
        }
        return rRole;
    }

    /**
     * Texte affiché sur une touche : ÷, × et − à la place de /, * et -.
     */
    static String signe(String pLabel) {
        return SIGNES.getOrDefault(pLabel, pLabel);
    }

    /**
     * Première police installée parmi celles proposées, sinon la police logique de repli.
     */
    static String police(String pRepli, String... pFamilles) {
        Set<String> vInstallees = new HashSet<>(
                Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        String rFamille = pRepli;
        for (String vFamille : pFamilles) {
            if (vInstallees.contains(vFamille)) {
                rFamille = vFamille;
                break;
            }
        }
        return rFamille;
    }

    static Font texte(int pStyle, int pTaille) {
        return new Font(police(Font.SANS_SERIF, "Segoe UI", "Inter", "Noto Sans", "DejaVu Sans"), pStyle, pTaille);
    }

    static Font chiffres(int pStyle, int pTaille) {
        return new Font(police(Font.MONOSPACED, "Cascadia Mono", "Consolas", "JetBrains Mono", "DejaVu Sans Mono"),
                pStyle, pTaille);
    }

    /**
     * Applique le thème à toute l'application ; à appeler avant de créer la première fenêtre.
     */
    static void appliquer() {
        MetalLookAndFeel.setCurrentTheme(new ThemeInstrument());
        try {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (UnsupportedLookAndFeelException vException) {
            // Metal est fourni par tout JDK : on garde l'apparence par défaut sinon
            return;
        }
        UIManager.put("TabbedPaneUI", OngletsInstrument.class.getName());
        UIManager.put("ButtonUI", BoutonPlat.class.getName());
        UIManager.put("TabbedPane.selected", SURFACE);
        UIManager.put("TabbedPane.contentAreaColor", FOND);
        UIManager.put("TabbedPane.borderHightlightColor", BORDURE);
        UIManager.put("TabbedPane.tabAreaBackground", FOND);
        UIManager.put("TabbedPane.unselectedBackground", FOND);
        UIManager.put("TabbedPane.focus", MOUTARDE);
        UIManager.put("TabbedPane.highlight", BORDURE);
        UIManager.put("TabbedPane.light", BORDURE);
        UIManager.put("TabbedPane.darkShadow", BORDURE);
        UIManager.put("TabbedPane.shadow", BORDURE);
        UIManager.put("TabbedPane.selectHighlight", BORDURE);
        UIManager.put("TabbedPane.tabInsets", new javax.swing.plaf.InsetsUIResource(6, 14, 6, 14));
        UIManager.put("Button.select", BORDURE);
        UIManager.put("ToggleButton.select", Role.OPERATEUR.aFond);
        UIManager.put("TitledBorder.titleColor", TEXTE_DOUX);
        // Sections des formulaires : un simple trait sous le titre, sans cadre fermé
        UIManager.put("TitledBorder.border", BorderFactory.createMatteBorder(1, 0, 0, 0, BORDURE));
        UIManager.put("TextField.border", BoutonPlat.champ());
        UIManager.put("FormattedTextField.border", BoutonPlat.champ());
        UIManager.put("Button.foreground", TEXTE);
        UIManager.put("Button.disabledText", TEXTE_DOUX);
        UIManager.put("ComboBox.background", CHAMP);
        UIManager.put("ComboBox.foreground", TEXTE);
        UIManager.put("ComboBox.selectionBackground", OLIVE);
        UIManager.put("ComboBox.selectionForeground", TEXTE);
        UIManager.put("TextArea.margin", new javax.swing.plaf.InsetsUIResource(6, 8, 6, 8));
        UIManager.put("ScrollPane.border", BorderFactory.createLineBorder(BORDURE));
        UIManager.put("Table.gridColor", BORDURE);
        UIManager.put("TableHeader.background", SURFACE);
    }

    /** Thème Metal recoloré : corps noir mat et textes clairs, ou l'inverse en thème clair, accent moutarde. */
    private static final class ThemeInstrument extends DefaultMetalTheme {
        private static final ColorUIResource PRIMAIRE_1 = new ColorUIResource(MOUTARDE);
        private static final ColorUIResource PRIMAIRE_2 = new ColorUIResource(couleur(0x6E5B2A, 0xC9A458));
        private static final ColorUIResource PRIMAIRE_3 = new ColorUIResource(Role.OPERATEUR.aFond);
        private static final ColorUIResource SECONDAIRE_1 = new ColorUIResource(BORDURE);
        private static final ColorUIResource SECONDAIRE_2 = new ColorUIResource(Role.CHIFFRE.aFond);
        private static final ColorUIResource SECONDAIRE_3 = new ColorUIResource(SURFACE);
        private static final ColorUIResource NOIR = new ColorUIResource(TEXTE);
        private static final ColorUIResource BLANC = new ColorUIResource(CHAMP);

        @Override
        public String getName() {
            return CLAIR ? "Gris clair" : "Noir mat";
        }

        @Override
        protected ColorUIResource getPrimary1() {
            return PRIMAIRE_1;
        }

        @Override
        protected ColorUIResource getPrimary2() {
            return PRIMAIRE_2;
        }

        @Override
        protected ColorUIResource getPrimary3() {
            return PRIMAIRE_3;
        }

        @Override
        protected ColorUIResource getSecondary1() {
            return SECONDAIRE_1;
        }

        @Override
        protected ColorUIResource getSecondary2() {
            return SECONDAIRE_2;
        }

        @Override
        protected ColorUIResource getSecondary3() {
            return SECONDAIRE_3;
        }

        @Override
        protected ColorUIResource getBlack() {
            return NOIR;
        }

        @Override
        protected ColorUIResource getWhite() {
            return BLANC;
        }

        @Override
        public ColorUIResource getMenuBackground() {
            return SECONDAIRE_3;
        }

        @Override
        public FontUIResource getControlTextFont() {
            return new FontUIResource(texte(Font.PLAIN, 13));
        }

        @Override
        public FontUIResource getUserTextFont() {
            return new FontUIResource(texte(Font.PLAIN, 13));
        }

        @Override
        public FontUIResource getSystemTextFont() {
            return new FontUIResource(texte(Font.PLAIN, 13));
        }

        @Override
        public FontUIResource getSubTextFont() {
            return new FontUIResource(texte(Font.PLAIN, 11));
        }

        @Override
        public FontUIResource getWindowTitleFont() {
            return new FontUIResource(texte(Font.BOLD, 13));
        }

        @Override
        public FontUIResource getMenuTextFont() {
            return new FontUIResource(texte(Font.PLAIN, 13));
        }
    }
}
