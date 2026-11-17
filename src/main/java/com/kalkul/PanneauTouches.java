package com.kalkul;

import java.awt.GridLayout;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Grille de touches : chaque clic envoie le texte de la touche à l'écouteur.
 * Un texte vide laisse une case vide.
 */
class PanneauTouches extends JPanel {
    private static final long serialVersionUID = 1L;

    PanneauTouches(String[] pLabels, int pColumns, Consumer<String> pOnKey) {
        super(new GridLayout(0, pColumns, 8, 8));
        setBackground(Habillage.FOND);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        for (String vLabel : pLabels) {
            if (vLabel.isEmpty()) {
                add(new JLabel());
                continue;
            }
            Touche vButton = new Touche(vLabel);
            vButton.addActionListener(pEvent -> pOnKey.accept(pEvent.getActionCommand()));
            add(vButton);
        }
    }
}
