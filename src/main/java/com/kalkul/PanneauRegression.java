package com.kalkul;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Sous-onglet Régression : une droite ajustée sur des couples x y, avec corrélation et significativité.
 * Le calcul est fait par {@link Regression}.
 */
class PanneauRegression extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final double SIGNIFICANCE_LEVEL = 0.05;

    private final JTextArea aPairs = new JTextArea("1 2\n2 4\n3 5\n4 4\n5 5", 8, 14);
    private final JTextField aPrediction = new JTextField(6);
    private final JTextArea aResult = new JTextArea(7, 30);

    PanneauRegression() {
        setLayout(new BorderLayout(4, 4));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        aPairs.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        JPanel vInput = new JPanel(new BorderLayout());
        vInput.setBorder(BorderFactory.createTitledBorder("Couples x y (un par ligne)"));
        vInput.add(new JScrollPane(aPairs), BorderLayout.CENTER);

        JButton vCompute = new JButton("Ajuster la droite");
        vCompute.addActionListener(pEvent -> compute());
        JPanel vActions = PanneauOutils.row(new JLabel("prévoir y pour x ="), aPrediction, vCompute);
        vInput.add(vActions, BorderLayout.SOUTH);
        add(vInput, BorderLayout.CENTER);

        aResult.setEditable(false);
        aResult.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        aResult.setBorder(BorderFactory.createTitledBorder("Résultat"));
        add(aResult, BorderLayout.SOUTH);
    }

    private void compute() {
        try {
            double[][] vPairs = Regression.parsePairs(aPairs.getText());
            Regression vFit = Regression.fit(vPairs[0], vPairs[1]);
            StringBuilder vText = new StringBuilder();
            vText.append("y = ").append(Affichage.format(vFit.slope())).append(" x + ")
                    .append(Affichage.format(vFit.intercept())).append('\n');
            vText.append("Corrélation r = ").append(Affichage.format(vFit.correlation()))
                    .append("   r² = ").append(Affichage.format(vFit.determination())).append('\n');
            vText.append("Points : ").append(vFit.count()).append('\n');
            vText.append(describePValue(vFit.pValue()));
            if (!aPrediction.getText().trim().isEmpty()) {
                double vX = FonctionsScientifiques.parseNumbers(aPrediction.getText())[0];
                vText.append("\nPour x = ").append(Affichage.format(vX)).append(" : y = ")
                        .append(Affichage.format(vFit.predict(vX)));
            }
            aResult.setText(vText.toString());
        } catch (RuntimeException vException) {
            aResult.setText(MoteurCalcul.ERREUR + " : " + vException.getMessage());
        }
    }

    private static String describePValue(double pPValue) {
        String rText;
        if (Double.isNaN(pPValue)) {
            rText = "Significativité : non calculable (il faut au moins 3 points et des y qui varient)";
        } else if (pPValue < SIGNIFICANCE_LEVEL) {
            rText = "Significativité : p = " + Affichage.format(pPValue) + " (< 0,05) : la pente est significative";
        } else {
            rText = "Significativité : p = " + Affichage.format(pPValue) + " (≥ 0,05) : lien non démontré";
        }
        return rText;
    }
}
