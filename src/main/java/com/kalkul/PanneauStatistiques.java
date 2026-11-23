package com.kalkul;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Sous-onglet Statistiques : résumé d'une série de nombres et histogramme ; le calcul est dans {@link Statistiques}. */
final class PanneauStatistiques {

    private PanneauStatistiques() {
    }

    /** Résumé en une ligne de texte, ou le message d'erreur si la série est vide ou illisible. */
    static String resume(String pSaisie) {
        double[] vValeurs = FonctionsScientifiques.parseNumbers(pSaisie);
        Statistiques.Resume vResume = Statistiques.resumer(vValeurs);
        return "n = " + vResume.nombre() + " | moyenne " + PanneauOutils.format(vResume.moyenne()) + " | médiane "
                + PanneauOutils.format(vResume.mediane()) + " | min " + PanneauOutils.format(vResume.minimum())
                + " | max " + PanneauOutils.format(vResume.maximum()) + " | Q1 "
                + PanneauOutils.format(vResume.premierQuartile()) + " | Q3 "
                + PanneauOutils.format(vResume.troisiemeQuartile()) + " | écart-type "
                + ecartType(vResume);
    }

    // Écart-type d'échantillon, et de population entre parenthèses ; « - » quand un seul nombre l'interdit
    private static String ecartType(Statistiques.Resume pResume) {
        String vEchantillon = Double.isNaN(pResume.ecartTypeEchantillon()) ? "-"
                : PanneauOutils.format(pResume.ecartTypeEchantillon());
        return vEchantillon + " (population " + PanneauOutils.format(pResume.ecartTypePopulation()) + ")";
    }

    static JPanel construire() {
        JPanel rPanneau = new JPanel(new BorderLayout(0, 6));
        rPanneau.setBorder(BorderFactory.createTitledBorder("Statistiques (nombres séparés par des espaces)"));
        JTextField vSaisie = new JTextField(30);
        JLabel vResultat = new JLabel(" ");
        Histogramme vHistogramme = new Histogramme();
        JButton vCalculer = new JButton("Calculer");
        vCalculer.addActionListener(pEvent -> {
            try {
                vResultat.setText(resume(vSaisie.getText()));
                double[] vValeurs = FonctionsScientifiques.parseNumbers(vSaisie.getText());
                vHistogramme.definir(Statistiques.histogramme(vValeurs, Statistiques.classesSturges(vValeurs.length)));
            } catch (RuntimeException vException) {
                vResultat.setText(MoteurCalcul.ERREUR + " : " + vException.getMessage());
                vHistogramme.definir(new int[0]);
            }
        });
        JPanel vHaut = new JPanel(new BorderLayout(0, 4));
        vHaut.add(PanneauOutils.row(vSaisie, vCalculer), BorderLayout.NORTH);
        vHaut.add(vResultat, BorderLayout.SOUTH);
        rPanneau.add(vHaut, BorderLayout.NORTH);
        rPanneau.add(vHistogramme, BorderLayout.CENTER);
        return rPanneau;
    }

    /** Barres verticales, une par classe, hauteur proportionnelle à l'effectif. */
    private static final class Histogramme extends JComponent {
        private static final long serialVersionUID = 1L;
        private int[] aEffectifs = new int[0];

        Histogramme() {
            setPreferredSize(new Dimension(360, 120));
        }

        void definir(int[] pEffectifs) {
            aEffectifs = pEffectifs;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics pG) {
            Graphics2D vG = (Graphics2D) pG.create();
            vG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int vMax = 1;
            for (int vEffectif : aEffectifs) {
                vMax = Math.max(vMax, vEffectif);
            }
            int vLargeur = aEffectifs.length == 0 ? 0 : getWidth() / aEffectifs.length;
            vG.setColor(Habillage.MOUTARDE);
            for (int vIndex = 0; vIndex < aEffectifs.length; vIndex++) {
                int vHauteur = (getHeight() - 16) * aEffectifs[vIndex] / vMax;
                vG.fillRect(vIndex * vLargeur + 2, getHeight() - vHauteur, Math.max(1, vLargeur - 4), vHauteur);
                vG.setColor(Habillage.TEXTE_DOUX);
                vG.drawString(String.valueOf(aEffectifs[vIndex]), vIndex * vLargeur + 4, getHeight() - vHauteur - 3);
                vG.setColor(Habillage.MOUTARDE);
            }
            vG.dispose();
        }
    }
}
