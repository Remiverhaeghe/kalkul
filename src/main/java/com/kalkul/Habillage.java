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
 * Habillage de Kalkul : couleurs graphite, écran à chiffres cyan et touches colorées selon leur rôle,
 * ou version claire quand Windows est en mode clair.
 * Tout passe par Swing, sans bibliothèque de thème.
 */
final class Habillage {
    /** Thème clair, décidé une fois au démarrage (voir {@link ThemeSysteme}). */
    static final boolean CLAIR = ThemeSysteme.estClair();

    static final Color FOND = couleur(0x1E2126, 0xE9ECF0);
    static final Color SURFACE = couleur(0x272B31, 0xF5F6F8);
    static final Color CHAMP = couleur(0x15171B, 0xFFFFFF);
    static final Color BORDURE = couleur(0x3A3F47, 0xC5CBD3);
    static final Color TEXTE = couleur(0xE6E8EB, 0x1E2126);
    static final Color TEXTE_DOUX = couleur(0x9AA1AB, 0x5B636E);
    static final Color ECRAN = couleur(0x0E1416, 0xD7E4DF);
    static final Color CHIFFRES = couleur(0x5CE1E6, 0x123A40);
    static final Color AMBRE = couleur(0xF0A23B, 0xB86B0C);
    static final Color CADRE_ECRAN = couleur(0x2A3338, 0xA9BDB6);
    static final Color MODE_ECRAN = couleur(0x3E8E92, 0x4F6E6A);
    static final Color ERREUR_ECRAN = couleur(0xFF8A7A, 0xB3261E);
    static final Color GRILLE = couleur(0x1F282C, 0xC2D3CD);

    /** Rôle d'une touche, qui fixe sa couleur. */
    enum Role {
        CHIFFRE(couleur(0x30353C, 0xFFFFFF), TEXTE),
        OPERATEUR(couleur(0x3A3226, 0xF8E6CC), AMBRE),
        FONCTION(couleur(0x262A30, 0xDDE2E8), couleur(0xB8C4D0, 0x34404D)),
        EGAL(AMBRE, couleur(0x1E1A12, 0xFFFFFF)),
        EFFACER(couleur(0x4A2A27, 0xF6D5CF), couleur(0xFF9C8A, 0xA3301D));

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
        UIManager.put("ToggleButton.select", Role.OPERATEUR.aFond);
        UIManager.put("TitledBorder.titleColor", TEXTE_DOUX);
        UIManager.put("TitledBorder.border", BorderFactory.createLineBorder(BORDURE));
        UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDURE), BorderFactory.createEmptyBorder(3, 6, 3, 6)));
        UIManager.put("TextArea.margin", new javax.swing.plaf.InsetsUIResource(6, 8, 6, 8));
        UIManager.put("ScrollPane.border", BorderFactory.createLineBorder(BORDURE));
        UIManager.put("Table.gridColor", BORDURE);
        UIManager.put("TableHeader.background", SURFACE);
    }

    /** Thème Metal recoloré : fonds graphite et textes clairs, ou l'inverse en thème clair, accent ambre. */
    private static final class ThemeGraphite extends DefaultMetalTheme {
        private static final ColorUIResource PRIMAIRE_1 = new ColorUIResource(AMBRE);
        private static final ColorUIResource PRIMAIRE_2 = new ColorUIResource(couleur(0x6B5A3E, 0xD9B47E));
        private static final ColorUIResource PRIMAIRE_3 = new ColorUIResource(Role.OPERATEUR.aFond);
        private static final ColorUIResource SECONDAIRE_1 = new ColorUIResource(BORDURE);
        private static final ColorUIResource SECONDAIRE_2 = new ColorUIResource(Role.CHIFFRE.aFond);
        private static final ColorUIResource SECONDAIRE_3 = new ColorUIResource(SURFACE);
        private static final ColorUIResource NOIR = new ColorUIResource(TEXTE);
        private static final ColorUIResource BLANC = new ColorUIResource(CHAMP);

        @Override
        public String getName() {
            return CLAIR ? "Clair" : "Graphite";
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
