package com.calculatrice;

import java.awt.BorderLayout;
import java.awt.Font;
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
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Fenêtre Swing de la calculatrice : boutons, écran et clavier.
 * Tout le calcul est dans {@link MoteurCalcul}.
 */
public class CalculatriceIntelligente extends JFrame {
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
    private final JTextField aDisplay;

    public CalculatriceIntelligente() {
        super("Calculatrice");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        aDisplay = new JTextField(aEngine.getDisplay());
        aDisplay.setHorizontalAlignment(SwingConstants.RIGHT);
        aDisplay.setEditable(false);
        // Sans cela, l'écran garde le focus et avale les touches du clavier
        aDisplay.setFocusable(false);
        aDisplay.setFont(new Font("SansSerif", Font.BOLD, 28));
        aDisplay.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(aDisplay, BorderLayout.NORTH);

        JTabbedPane vTabs = new JTabbedPane();
        vTabs.addTab("Base", new PanneauTouches(BASE_KEYS, 4, this::send));
        vTabs.addTab("Scientifique", buildScientificTab());
        vTabs.addTab("Outils", new PanneauOutils());
        // Les touches du clavier ne doivent pas être volées par la navigation entre onglets
        vTabs.setFocusable(false);
        add(vTabs, BorderLayout.CENTER);

        addKeyBindings();
        setVisible(true);
    }

    private JPanel buildScientificTab() {
        JToggleButton vAngle = new JToggleButton("Angles en degrés", true);
        vAngle.setFocusable(false);
        vAngle.addActionListener(pEvent -> {
            aEngine.setDegrees(vAngle.isSelected());
            vAngle.setText(vAngle.isSelected() ? "Angles en degrés" : "Angles en radians");
        });

        JPanel rPanel = new JPanel(new BorderLayout(5, 5));
        rPanel.add(vAngle, BorderLayout.NORTH);
        rPanel.add(new PanneauTouches(SCIENTIFIC_KEYS, 6, this::send), BorderLayout.CENTER);
        return rPanel;
    }

    private void send(String pInput) {
        aEngine.handleInput(pInput);
        aDisplay.setText(aEngine.getDisplay());
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
        SwingUtilities.invokeLater(CalculatriceIntelligente::new);
    }
}
