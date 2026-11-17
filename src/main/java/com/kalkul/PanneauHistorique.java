package com.kalkul;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

/**
 * Onglet Historique : les calculs terminés, du plus récent au plus ancien. Un double-clic ou Entrée remet
 * le résultat à l'écran.
 */
class PanneauHistorique extends JPanel {
    private static final long serialVersionUID = 1L;

    private final DefaultListModel<MoteurCalcul.Calcul> aModel = new DefaultListModel<>();
    private final JList<MoteurCalcul.Calcul> aList = new JList<>(aModel);

    PanneauHistorique(Consumer<String> pOnReuse, Runnable pOnClear) {
        super(new BorderLayout(0, 8));
        setBackground(Habillage.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        aList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        aList.setBackground(Habillage.CHAMP);
        aList.setCellRenderer(new Rendu());
        aList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent pEvent) {
                if (pEvent.getClickCount() == 2) {
                    reuse(pOnReuse);
                }
            }
        });
        aList.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "reprendre");
        aList.getActionMap().put("reprendre", new javax.swing.AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent pEvent) {
                reuse(pOnReuse);
            }
        });

        JLabel vHint = new JLabel("Double-clic sur un calcul pour reprendre son résultat.");
        vHint.setForeground(Habillage.TEXTE_DOUX);
        JButton vClear = new JButton("Vider l'historique");
        vClear.setFocusable(false);
        vClear.addActionListener(pEvent -> {
            pOnClear.run();
            aModel.clear();
        });
        JPanel vBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        vBottom.setOpaque(false);
        vBottom.add(vClear);

        add(vHint, BorderLayout.NORTH);
        add(new JScrollPane(aList), BorderLayout.CENTER);
        add(vBottom, BorderLayout.SOUTH);
    }

    /** Remplace la liste par l'historique du moteur. */
    void montrer(List<MoteurCalcul.Calcul> pHistory) {
        aModel.clear();
        pHistory.forEach(aModel::addElement);
    }

    int taille() {
        return aModel.getSize();
    }

    private void reuse(Consumer<String> pOnReuse) {
        MoteurCalcul.Calcul vSelected = aList.getSelectedValue();
        if (vSelected != null) {
            pOnReuse.accept(vSelected.resultat());
        }
    }

    /** Une ligne : le calcul en gris, le résultat en chiffres de l'écran. */
    private static final class Rendu extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList<?> pList, Object pValue, int pIndex, boolean pSelected,
                boolean pFocus) {
            MoteurCalcul.Calcul vCalcul = (MoteurCalcul.Calcul) pValue;
            String vTexte = "<html><span style='color:" + hex(Habillage.TEXTE_DOUX) + "'>" + vCalcul.expression()
                    + " =</span>&nbsp;&nbsp;<b>" + vCalcul.resultat() + "</b></html>";
            JLabel rLabel = (JLabel) super.getListCellRendererComponent(pList, vTexte, pIndex, pSelected, pFocus);
            rLabel.setFont(Habillage.chiffres(Font.PLAIN, 16));
            rLabel.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            return rLabel;
        }

        private static String hex(java.awt.Color pColor) {
            return String.format("#%06X", pColor.getRGB() & 0xFFFFFF);
        }
    }
}
