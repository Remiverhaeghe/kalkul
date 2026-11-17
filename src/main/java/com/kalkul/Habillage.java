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
 * Habillage de Kalkul : couleurs graphite, écran à chiffres cyan et touches colorées selon leur rôle.
 * Tout passe par Swing, sans bibliothèque de thème.
 */
final class Habillage {
    static final Color FOND = new Color(0x1E2126);
    static final Color SURFACE = new Color(0x272B31);
    static final Color CHAMP = new Color(0x15171B);
    static final Color BORDURE = new Color(0x3A3F47);
    static final Color TEXTE = new Color(0xE6E8EB);
    static final Color TEXTE_DOUX = new Color(0x9AA1AB);
    static final Color ECRAN = new Color(0x0E1416);
    static final Color CHIFFRES = new Color(0x5CE1E6);
    static final Color AMBRE = new Color(0xF0A23B);

    /** Rôle d'une touche, qui fixe sa couleur. */
    enum Role {
        CHIFFRE(new Color(0x30353C), TEXTE),
        OPERATEUR(new Color(0x3A3226), AMBRE),
        FONCTION(new Color(0x262A30), new Color(0xB8C4D0)),
        EGAL(AMBRE, new Color(0x1E1A12)),
        EFFACER(new Color(0x4A2A27), new Color(0xFF9C8A));

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
        MetalLookAndFeel.setCurrentTheme(new ThemeGraphite());
        try {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (UnsupportedLookAndFeelException vException) {
            // Metal est fourni par tout JDK : on garde l'apparence par défaut sinon
            return;
        }
        UIManager.put("TabbedPane.selected", SURFACE);
        UIManager.put("TabbedPane.contentAreaColor", SURFACE);
        UIManager.put("TabbedPane.borderHightlightColor", BORDURE);
        UIManager.put("TabbedPane.tabAreaBackground", FOND);
        UIManager.put("TabbedPane.unselectedBackground", FOND);
        UIManager.put("TabbedPane.focus", AMBRE);
        UIManager.put("TabbedPane.highlight", BORDURE);
        UIManager.put("TabbedPane.light", BORDURE);
        UIManager.put("TabbedPane.darkShadow", BORDURE);
        UIManager.put("TabbedPane.shadow", BORDURE);
        UIManager.put("TabbedPane.selectHighlight", BORDURE);
        UIManager.put("TabbedPane.tabInsets", new javax.swing.plaf.InsetsUIResource(6, 14, 6, 14));
        UIManager.put("Button.select", BORDURE);
        UIManager.put("ToggleButton.select", new Color(0x3A3226));
        UIManager.put("TitledBorder.titleColor", TEXTE_DOUX);
        UIManager.put("TitledBorder.border", BorderFactory.createLineBorder(BORDURE));
        UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDURE), BorderFactory.createEmptyBorder(3, 6, 3, 6)));
        UIManager.put("TextArea.margin", new javax.swing.plaf.InsetsUIResource(6, 8, 6, 8));
        UIManager.put("ScrollPane.border", BorderFactory.createLineBorder(BORDURE));
        UIManager.put("Table.gridColor", BORDURE);
        UIManager.put("TableHeader.background", SURFACE);
    }

    /** Thème Metal recoloré : fonds graphite, textes clairs, accent ambre. */
    private static final class ThemeGraphite extends DefaultMetalTheme {
        private static final ColorUIResource PRIMAIRE_1 = new ColorUIResource(AMBRE);
        private static final ColorUIResource PRIMAIRE_2 = new ColorUIResource(new Color(0x6B5A3E));
        private static final ColorUIResource PRIMAIRE_3 = new ColorUIResource(new Color(0x3A3226));
        private static final ColorUIResource SECONDAIRE_1 = new ColorUIResource(BORDURE);
        private static final ColorUIResource SECONDAIRE_2 = new ColorUIResource(new Color(0x30353C));
        private static final ColorUIResource SECONDAIRE_3 = new ColorUIResource(SURFACE);
        private static final ColorUIResource NOIR = new ColorUIResource(TEXTE);
        private static final ColorUIResource BLANC = new ColorUIResource(CHAMP);

        @Override
        public String getName() {
            return "Graphite";
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
