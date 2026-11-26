package com.kalkul;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.util.prefs.Preferences;

/**
 * Fenêtre Swing de la calculatrice : boutons, écran et clavier.
 * Tout le calcul est dans {@link MoteurCalcul}.
 */
public class Kalkul extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final String[] BASE_KEYS = {
        "C", "±", "%", "/",
        "x²", "√", "mod", "*",
        "7", "8", "9", "-",
        "4", "5", "6", "+",
        "1", "2", "3", "⌫",
        "0", ".", "=", ""
    };

    // Deux colonnes de fonctions à gauche du pavé de base
    private static final String[] SCIENTIFIC_KEYS = {
        "sin", "cos", "C", "±", "%", "/",
        "tan", "asin", "x²", "√", "mod", "*",
        "acos", "atan", "7", "8", "9", "-",
        "ln", "log", "4", "5", "6", "+",
        "exp", "^", "1", "2", "3", "⌫",
        "n!", "", "0", ".", "=", ""
    };

    // Touches du clavier qui se comportent comme les boutons
    private static final String KEYS = "0123456789.+-*/=^\n\r\b";

    private final transient MoteurCalcul aEngine = new MoteurCalcul();
    private final Ecran aDisplay = new Ecran();
    private final transient CarnetFormules aFormulas =
            CarnetFormules.depuis(Preferences.userNodeForPackage(Kalkul.class));
    private final transient FeuilleCalcul aSheet =
            FeuilleCalcul.depuis(Preferences.userNodeForPackage(Kalkul.class));
    private final PanneauHistorique aHistory = new PanneauHistorique(this::reuse, aEngine::effacerHistorique);

    public Kalkul() {
        super("Kalkul");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 780);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Habillage.FOND);
        setLayout(new BorderLayout(0, 4));

        aDisplay.afficher(aEngine.getDisplay(), aEngine.isDegrees());
        add(aDisplay, BorderLayout.NORTH);

        // Une seule rangée d'onglets, avec des flèches si la fenêtre est trop étroite
        JTabbedPane vTabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        vTabs.addTab("Base", new PanneauTouches(BASE_KEYS, 4, this::send));
        vTabs.addTab("Scientifique", buildScientificTab());
        vTabs.addTab("Financier", new PanneauFinance());
        vTabs.addTab("Graphique", new PanneauGraphique(aFormulas, aSheet));
        vTabs.addTab("Programmeur", new PanneauProgrammeur());
        vTabs.addTab("Chantier", new PanneauChantier());
        vTabs.addTab("Outils", new PanneauOutils());
        vTabs.addTab("Feuille", new PanneauFeuille(aSheet, aFormulas));
        vTabs.addTab("Historique", aHistory);
        // Les touches du clavier ne doivent pas être volées par la navigation entre onglets
        vTabs.setFocusable(false);
        vTabs.setBorder(BorderFactory.createEmptyBorder(0, 6, 6, 6));
        add(vTabs, BorderLayout.CENTER);

        addKeyBindings();
        installTabShortcuts(vTabs);
        setVisible(true);
    }

    private JPanel buildScientificTab() {
        JToggleButton vAngle = new JToggleButton("Angles en degrés", true);
        vAngle.setFocusable(false);
        vAngle.setContentAreaFilled(false);
        vAngle.setOpaque(true);
        vAngle.setBackground(Habillage.ECRAN);
        vAngle.setForeground(Habillage.CHIFFRES);
        vAngle.setFont(Habillage.texte(java.awt.Font.BOLD, 13));
        vAngle.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Habillage.BORDURE), BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        vAngle.addActionListener(pEvent -> {
            aEngine.setDegrees(vAngle.isSelected());
            vAngle.setText(vAngle.isSelected() ? "Angles en degrés" : "Angles en radians");
            aDisplay.afficher(aEngine.getDisplay(), aEngine.isDegrees());
        });

        JPanel rPanel = new JPanel(new BorderLayout(0, 0));
        rPanel.setBackground(Habillage.FOND);
        rPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        rPanel.add(vAngle, BorderLayout.NORTH);
        rPanel.add(new PanneauTouches(SCIENTIFIC_KEYS, 6, this::send), BorderLayout.CENTER);
        rPanel.add(buildFormulaRow(), BorderLayout.SOUTH);
        return rPanel;
    }

    /**
     * Ligne qui applique une formule enregistrée dans l'onglet Graphique au nombre de l'écran.
     */
    private JPanel buildFormulaRow() {
        ChoixFormule vChoice = new ChoixFormule(aFormulas);
        javax.swing.JButton vApply = new javax.swing.JButton("f(écran)");
        vApply.setFocusable(false);
        javax.swing.JLabel vHint = new javax.swing.JLabel();
        vHint.setForeground(Habillage.TEXTE_DOUX);
        Runnable vRefresh = () -> {
            boolean vAny = !aFormulas.liste().isEmpty();
            vChoice.setEnabled(vAny);
            vApply.setEnabled(vAny);
            vHint.setText(vAny ? "" : "Enregistre une formule dans l'onglet Graphique pour l'appliquer ici.");
        };
        aFormulas.ecouter(vRefresh);
        vRefresh.run();
        vApply.addActionListener(pEvent -> applyFormula(vChoice.choisie()));

        JPanel rRow = PanneauOutils.row(new javax.swing.JLabel("f(x) ="), vChoice, vApply, vHint);
        rRow.setOpaque(false);
        rRow.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        return rRow;
    }

    private void applyFormula(CarnetFormules.Formule pFormula) {
        if (pFormula == null) {
            return;
        }
        String vX = aEngine.getDisplay();
        try {
            java.util.Map<String, Double> vCells = aSheet.valeurs();
            double vResult = CarnetFormules.appliquer(pFormula, Double.parseDouble(vX), vCells);
            aEngine.afficherResultat(CarnetFormules.ecrire(pFormula, Double.parseDouble(vX), vCells), vResult);
            aDisplay.rappel(aEngine.getHistorique().get(0).expression() + " =");
            aHistory.montrer(aEngine.getHistorique());
        } catch (RuntimeException vException) {
            aEngine.afficherErreur();
            aDisplay.rappel("");
        }
        aDisplay.afficher(aEngine.getDisplay(), aEngine.isDegrees());
    }

    private void send(String pInput) {
        if (Reglages.sonDesTouches()) {
            Declic.jouer();
        }
        aEngine.handleInput(pInput);
        aDisplay.afficher(aEngine.getDisplay(), aEngine.isDegrees());
        MoteurCalcul.Calcul vLast = aEngine.getDernierCalcul();
        // Le rappel du calcul reste affiché jusqu'à la touche suivante
        aDisplay.rappel(vLast == null ? "" : vLast.expression() + " =");
        if (vLast != null) {
            aHistory.montrer(aEngine.getHistorique());
        }
    }

    // Ctrl+C copie le nombre affiché, Ctrl+V colle un nombre ; un champ de texte garde ses propres raccourcis
    private void copier() {
        String vTexte = aEngine.texteACopier();
        if (!vTexte.isEmpty()) {
            try {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(vTexte), null);
            } catch (IllegalStateException vException) {
                // Presse-papiers occupé par une autre application : rien à copier cette fois
                return;
            }
        }
    }

    private void coller() {
        try {
            Transferable vContenu = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (vContenu != null && vContenu.isDataFlavorSupported(DataFlavor.stringFlavor)
                    && aEngine.coller((String) vContenu.getTransferData(DataFlavor.stringFlavor))) {
                aDisplay.afficher(aEngine.getDisplay(), aEngine.isDegrees());
                aDisplay.rappel("");
            }
        } catch (IllegalStateException | UnsupportedFlavorException | IOException vException) {
            // Rien d'utilisable dans le presse-papiers : l'écran ne change pas
            return;
        }
    }

    private void reuse(String pValue) {
        aEngine.reprendre(pValue);
        aDisplay.afficher(aEngine.getDisplay(), aEngine.isDegrees());
        aDisplay.rappel("");
    }

    // Un raccourci par onglet, relu à chaque changement dans les réglages
    private void installTabShortcuts(JTabbedPane pTabs) {
        Raccourcis vRaccourcis = Raccourcis.partage();
        Runnable vInstall = () -> {
            InputMap vInputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap vActionMap = getRootPane().getActionMap();
            for (Raccourcis.Onglet vOnglet : Raccourcis.Onglet.values()) {
                String vNom = "onglet_" + vOnglet.name();
                // Les anciennes touches de cette action sont retirées avant d'en poser une autre
                for (KeyStroke vAncienne : vInputMap.allKeys() == null ? new KeyStroke[0] : vInputMap.allKeys()) {
                    if (vNom.equals(vInputMap.get(vAncienne))) {
                        vInputMap.remove(vAncienne);
                    }
                }
                vInputMap.put(vRaccourcis.touche(vOnglet), vNom);
                vActionMap.put(vNom, new AbstractAction() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(ActionEvent pEvent) {
                        int vIndex = pTabs.indexOfTab(vOnglet.titre());
                        if (vIndex >= 0) {
                            pTabs.setSelectedIndex(vIndex);
                        }
                    }
                });
            }
        };
        vInstall.run();
        vRaccourcis.ecouter(vInstall);
    }

    private void addKeyBindings() {
        JRootPane vRoot = getRootPane();
        InputMap vInputMap = vRoot.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap vActionMap = vRoot.getActionMap();

        int vMasque = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        vInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, vMasque), "copier");
        vActionMap.put("copier", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent pEvent) {
                copier();
            }
        });
        vInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, vMasque), "coller");
        vActionMap.put("coller", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent pEvent) {
                coller();
            }
        });

        // La touche c efface, comme le bouton C
        for (char vKey : (KEYS + "c").toCharArray()) {
            final String vText = String.valueOf(vKey);
            vInputMap.put(KeyStroke.getKeyStroke(vKey), "key_" + vKey);
            vActionMap.put("key_" + vKey, new AbstractAction() {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent pEvent) {
                    send(vText);
                    eclairer(getContentPane(), toucheDuClavier(vText));
                }
            });
        }
    }

    /**
     * Texte de la touche à l'écran qui correspond à une touche du clavier : Entrée pour « = »,
     * retour arrière pour « ⌫ », c pour « C ».
     */
    static String toucheDuClavier(String pClavier) {
        String rTouche = switch (pClavier) {
            case "\n", "\r" -> "=";
            case "\b" -> "⌫";
            case "c" -> "C";
            default -> pClavier;
        };
        return rTouche;
    }

    /**
     * Allume la touche visible qui porte ce texte, dans l'onglet ouvert.
     */
    private static void eclairer(java.awt.Container pConteneur, String pTouche) {
        for (java.awt.Component vComposant : pConteneur.getComponents()) {
            if (vComposant instanceof Touche vTouche && vTouche.isShowing()
                    && pTouche.equals(vTouche.getActionCommand())) {
                vTouche.eclairer();
            } else if (vComposant instanceof java.awt.Container vEnfant) {
                eclairer(vEnfant, pTouche);
            }
        }
    }

    public static void main(String[] pArgs) {
        SwingUtilities.invokeLater(() -> {
            Habillage.appliquer();
            new Kalkul();
        });
    }
}
