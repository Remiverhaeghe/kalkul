package com.calculatrice;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Onglet d'outils qui demandent plusieurs saisies : statistiques, équation du second degré
 * et conversion d'unités. Le calcul est fait par {@link FonctionsScientifiques} et {@link Conversions}.
 */
class PanneauOutils extends JPanel {
    private static final long serialVersionUID = 1L;

    PanneauOutils() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(buildStatistics());
        add(buildQuadratic());
        add(buildConversion());
    }

    private JPanel buildStatistics() {
        JPanel vSection = section("Statistiques (nombres séparés par des espaces)");
        JTextField vInput = new JTextField(22);
        JLabel vResult = new JLabel(" ");
        vSection.add(row(vInput, button("Calculer", vResult, () -> {
            double[] vValues = FonctionsScientifiques.parseNumbers(vInput.getText());
            return "Moyenne " + format(FonctionsScientifiques.mean(vValues))
                    + " | variance " + format(FonctionsScientifiques.variance(vValues))
                    + " | écart-type " + format(FonctionsScientifiques.standardDeviation(vValues));
        })));
        vSection.add(vResult);
        return vSection;
    }

    private JPanel buildQuadratic() {
        JPanel vSection = section("Équation ax² + bx + c = 0");
        JTextField vA = new JTextField(5);
        JTextField vB = new JTextField(5);
        JTextField vC = new JTextField(5);
        JLabel vResult = new JLabel(" ");
        vSection.add(row(new JLabel("a"), vA, new JLabel("b"), vB, new JLabel("c"), vC,
                button("Résoudre", vResult, () -> {
                    double[] vSolutions = FonctionsScientifiques.solveQuadratic(
                            FonctionsScientifiques.parseNumbers(vA.getText())[0],
                            FonctionsScientifiques.parseNumbers(vB.getText())[0],
                            FonctionsScientifiques.parseNumbers(vC.getText())[0]);
                    String rText;
                    if (vSolutions.length == 0) {
                        rText = "Aucune solution réelle";
                    } else if (vSolutions.length == 1) {
                        rText = "x = " + format(vSolutions[0]);
                    } else {
                        rText = "x1 = " + format(vSolutions[0]) + " | x2 = " + format(vSolutions[1]);
                    }
                    return rText;
                })));
        vSection.add(vResult);
        return vSection;
    }

    private JPanel buildConversion() {
        JPanel vSection = section("Conversion d'unités");
        JTextField vValue = new JTextField(8);
        JComboBox<String> vFrom = new JComboBox<>(Conversions.units());
        JComboBox<String> vTo = new JComboBox<>(Conversions.units());
        vTo.setSelectedIndex(1);
        JLabel vResult = new JLabel(" ");
        vSection.add(row(vValue, vFrom, new JLabel("vers"), vTo,
                button("Convertir", vResult, () -> {
                    double vNumber = FonctionsScientifiques.parseNumbers(vValue.getText())[0];
                    double vConverted = Conversions.convert(
                            vNumber, (String) vFrom.getSelectedItem(), (String) vTo.getSelectedItem());
                    return format(vNumber) + " " + vFrom.getSelectedItem()
                            + " = " + format(vConverted) + " " + vTo.getSelectedItem();
                })));
        vSection.add(vResult);
        return vSection;
    }

    // Bouton qui affiche le texte renvoyé par le calcul, ou le message de l'erreur
    static JButton button(String pLabel, JLabel pResult, Supplier<String> pCalculation) {
        JButton rButton = new JButton(pLabel);
        rButton.addActionListener(pEvent -> {
            try {
                pResult.setText(pCalculation.get());
            } catch (RuntimeException vException) {
                pResult.setText(MoteurCalcul.ERREUR + " : " + vException.getMessage());
            }
        });
        return rButton;
    }

    static JPanel section(String pTitle) {
        JPanel rSection = new JPanel(new GridLayout(0, 1, 4, 4));
        rSection.setBorder(BorderFactory.createTitledBorder(pTitle));
        return rSection;
    }

    static JPanel row(java.awt.Component... pComponents) {
        JPanel rRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        for (java.awt.Component vComponent : pComponents) {
            rRow.add(vComponent);
        }
        return rRow;
    }

    static String format(double pValue) {
        return MoteurCalcul.trimZeros(pValue);
    }
}
