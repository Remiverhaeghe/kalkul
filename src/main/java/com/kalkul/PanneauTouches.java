package com.kalkul;

import java.awt.Font;
import java.awt.GridLayout;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Grille de boutons : chaque clic envoie le texte du bouton à l'écouteur.
 * Un texte vide laisse une case vide.
 */
class PanneauTouches extends JPanel {
    private static final long serialVersionUID = 1L;

    PanneauTouches(String[] pLabels, int pColumns, Consumer<String> pOnKey) {
        super(new GridLayout(0, pColumns, 5, 5));
        for (String vLabel : pLabels) {
            if (vLabel.isEmpty()) {
                add(new JLabel());
                continue;
            }
            JButton vButton = new JButton(vLabel);
            vButton.setFont(new Font("SansSerif", Font.BOLD, 16));
            vButton.addActionListener(pEvent -> pOnKey.accept(pEvent.getActionCommand()));
            add(vButton);
        }
    }
}
