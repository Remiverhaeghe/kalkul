package com.kalkul;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Grille de touches : chaque clic envoie le texte de la touche à l'écouteur.
 * Un texte vide laisse une case vide. Les cases restent carrées et la grille se centre, pour que les
 * touches rondes restent serrées comme sur un vrai clavier, quelle que soit la taille de la fenêtre.
 */
class PanneauTouches extends JPanel {
    private static final long serialVersionUID = 1L;

    PanneauTouches(String[] pLabels, int pColumns, Consumer<String> pOnKey) {
        super();
        setLayout(new GrilleCarree(pColumns));
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

    /** Disposition en cases carrées, de la plus grande taille qui tient, grille centrée. */
    static final class GrilleCarree implements LayoutManager {
        private final int aColonnes;

        GrilleCarree(int pColonnes) {
            aColonnes = pColonnes;
        }

        /**
         * Côté d'une case pour une zone et un nombre de touches donnés.
         */
        static int cote(int pLargeur, int pHauteur, int pColonnes, int pTouches) {
            int vLignes = Math.max((pTouches + pColonnes - 1) / pColonnes, 1);
            int rCote = Math.max(Math.min(pLargeur / pColonnes, pHauteur / vLignes), 0);
            return rCote;
        }

        @Override
        public void layoutContainer(Container pParent) {
            Insets vMarges = pParent.getInsets();
            int vLargeur = pParent.getWidth() - vMarges.left - vMarges.right;
            int vHauteur = pParent.getHeight() - vMarges.top - vMarges.bottom;
            int vTouches = pParent.getComponentCount();
            int vCote = cote(vLargeur, vHauteur, aColonnes, vTouches);
            int vLignes = Math.max((vTouches + aColonnes - 1) / aColonnes, 1);
            int vGauche = vMarges.left + (vLargeur - vCote * aColonnes) / 2;
            int vHaut = vMarges.top + (vHauteur - vCote * vLignes) / 2;
            for (int vIndex = 0; vIndex < vTouches; vIndex++) {
                Component vTouche = pParent.getComponent(vIndex);
                vTouche.setBounds(vGauche + (vIndex % aColonnes) * vCote, vHaut + (vIndex / aColonnes) * vCote,
                        vCote, vCote);
            }
        }

        @Override
        public Dimension preferredLayoutSize(Container pParent) {
            int vLignes = Math.max((pParent.getComponentCount() + aColonnes - 1) / aColonnes, 1);
            return new Dimension(aColonnes * 84, vLignes * 84);
        }

        @Override
        public Dimension minimumLayoutSize(Container pParent) {
            int vLignes = Math.max((pParent.getComponentCount() + aColonnes - 1) / aColonnes, 1);
            return new Dimension(aColonnes * 48, vLignes * 48);
        }

        @Override
        public void addLayoutComponent(String pNom, Component pComposant) {
            // Les touches se placent dans l'ordre d'ajout
        }

        @Override
        public void removeLayoutComponent(Component pComposant) {
            // Rien à oublier : la disposition ne garde aucune touche
        }
    }
}
