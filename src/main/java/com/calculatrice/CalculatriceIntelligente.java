package com.calculatrice;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Fenêtre Swing de la calculatrice : boutons, écran et clavier.
 * Tout le calcul est dans {@link MoteurCalcul}.
 */
public class CalculatriceIntelligente extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final String[] BUTTONS = {
        "C", "±", "%", "/",
        "7", "8", "9", "*",
        "4", "5", "6", "-",
        "1", "2", "3", "+",
        "0", ".", "=", ""
    };

    // Touches du clavier qui se comportent comme les boutons
    private static final String KEYS = "0123456789.+-*/=\n\r\b";

    private final transient MoteurCalcul aEngine = new MoteurCalcul();
    private final JTextField aDisplay;

    public CalculatriceIntelligente() {
        super("Calculatrice");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(320, 420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        aDisplay = new JTextField(aEngine.getDisplay());
        aDisplay.setHorizontalAlignment(SwingConstants.RIGHT);
        aDisplay.setEditable(false);
        aDisplay.setFont(new Font("SansSerif", Font.BOLD, 28));
        aDisplay.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(aDisplay, BorderLayout.NORTH);

        JPanel vButtonPanel = new JPanel(new GridLayout(5, 4, 5, 5));
        for (String vButton : BUTTONS) {
            if (vButton.isEmpty()) {
                // Case vide pour garder la grille alignée
                vButtonPanel.add(new JLabel());
                continue;
            }
            JButton vSwingButton = new JButton(vButton);
            vSwingButton.setFont(new Font("SansSerif", Font.BOLD, 18));
            vSwingButton.addActionListener(pEvent -> send(pEvent.getActionCommand()));
            vButtonPanel.add(vSwingButton);
        }
        add(vButtonPanel, BorderLayout.CENTER);

        addKeyBindings();
        setVisible(true);
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
