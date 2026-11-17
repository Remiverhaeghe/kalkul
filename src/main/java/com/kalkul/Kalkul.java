package com.kalkul;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
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
        vTabs.addTab("Graphique", new PanneauGraphique());
        vTabs.addTab("Programmeur", new PanneauProgrammeur());
        vTabs.addTab("Chantier", new PanneauChantier());
        vTabs.addTab("Outils", new PanneauOutils());
        vTabs.addTab("Historique", aHistory);
        // Les touches du clavier ne doivent pas être volées par la navigation entre onglets
        vTabs.setFocusable(false);
        vTabs.setBorder(BorderFactory.createEmptyBorder(0, 6, 6, 6));
        add(vTabs, BorderLayout.CENTER);

        addKeyBindings();
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
        return rPanel;
    }

    private void send(String pInput) {
        aEngine.handleInput(pInput);
        aDisplay.afficher(aEngine.getDisplay(), aEngine.isDegrees());
        MoteurCalcul.Calcul vLast = aEngine.getDernierCalcul();
        // Le rappel du calcul reste affiché jusqu'à la touche suivante
        aDisplay.rappel(vLast == null ? "" : vLast.expression() + " =");
        if (vLast != null) {
            aHistory.montrer(aEngine.getHistorique());
        }
    }

    private void reuse(String pValue) {
        aEngine.reprendre(pValue);
        aDisplay.afficher(aEngine.getDisplay(), aEngine.isDegrees());
        aDisplay.rappel("");
    }

    private void addKeyBindings() {
        JRootPane vRoot = getRootPane();
        InputMap vInputMap = vRoot.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap vActionMap = vRoot.getActionMap();

        // La touche c efface, comme le bouton C
        for (char vKey : (KEYS + "c").toCharArray()) {
            final String vText = String.valueOf(vKey);
            vInputMap.put(KeyStroke.getKeyStroke(vKey), "key_" + vKey);
            vActionMap.put("key_" + vKey, new AbstractAction() {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent pEvent) {
                    send(vText);
                }
            });
        }
    }

    public static void main(String[] pArgs) {
        SwingUtilities.invokeLater(() -> {
            Habillage.appliquer();
            new Kalkul();
        });
    }
}
