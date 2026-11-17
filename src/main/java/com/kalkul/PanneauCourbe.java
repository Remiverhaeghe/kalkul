package com.kalkul;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Sous-onglet Courbes : formule avec variables, tracé, tangente en un point et aire sous la courbe,
 * formules enregistrées dans le {@link CarnetFormules}.
 * Le calcul est fait par {@link Expression}, {@link Analyse} et {@link EchantillonCourbe}.
 */
class PanneauCourbe extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int POINTS = 600;

    private final JTextField aFormula = new JTextField("sin(x)", 16);
    private final JTextField aParameters = new JTextField(10);
    private final JTextField aXMin = new JTextField("-10", 4);
    private final JTextField aXMax = new JTextField("10", 4);
    private final JTextField aPoint = new JTextField(4);
    private final JTextField aFrom = new JTextField(4);
    private final JTextField aTo = new JTextField(4);
    private final JLabel aResult = new JLabel(" ");
    private final TraceCourbe aPlot = new TraceCourbe();

    PanneauCourbe(CarnetFormules pCarnet) {
        setLayout(new BorderLayout(4, 4));

        JPanel vForm = new JPanel(new GridLayout(0, 1, 2, 2));
        vForm.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 4));
        ChoixFormule vSaved = new ChoixFormule(pCarnet);
        vSaved.addActionListener(pEvent -> {
            if (!vSaved.enRemplissage() && vSaved.choisie() != null) {
                aFormula.setText(vSaved.choisie().formule());
                aParameters.setText(vSaved.choisie().parametres());
            }
        });
        JButton vSave = new JButton("Enregistrer");
        vSave.addActionListener(pEvent -> {
            try {
                pCarnet.enregistrer(aFormula.getText(), aParameters.getText());
                aResult.setText("Formule enregistrée : elle s'applique aussi au nombre de l'écran, onglet Scientifique.");
            } catch (RuntimeException vException) {
                aResult.setText(MoteurCalcul.ERREUR + " : " + vException.getMessage());
            }
        });
        JButton vRemove = new JButton("Retirer");
        vRemove.addActionListener(pEvent -> {
            if (vSaved.choisie() != null) {
                pCarnet.supprimer(vSaved.choisie());
            }
        });
        vForm.add(PanneauOutils.row(new JLabel("Formules enregistrées"), vSaved, vSave, vRemove));
        vForm.add(PanneauOutils.row(new JLabel("f(x) ="), aFormula, new JLabel("paramètres"), aParameters));
        vForm.add(PanneauOutils.row(new JLabel("x de"), aXMin, new JLabel("à"), aXMax,
                new JLabel("tangente en x ="), aPoint));
        JButton vDraw = new JButton("Tracer");
        vDraw.addActionListener(pEvent -> draw());
        vForm.add(PanneauOutils.row(new JLabel("aire de"), aFrom, new JLabel("à"), aTo, vDraw));
        add(vForm, BorderLayout.NORTH);
        add(aPlot, BorderLayout.CENTER);
        add(aResult, BorderLayout.SOUTH);
    }

    private void draw() {
        try {
            Expression vFormula = Expression.parse(aFormula.getText());
            Map<String, Double> vParameters = Expression.parseParameters(aParameters.getText());
            DoubleUnaryOperator vFunction = vFormula.bind(vParameters);
            aPlot.setCurve(EchantillonCourbe.sample(vFunction, number(aXMin), number(aXMax), POINTS));

            StringBuilder vText = new StringBuilder("<html>");
            if (!aPoint.getText().trim().isEmpty()) {
                double vX = number(aPoint);
                double vY = vFunction.applyAsDouble(vX);
                double vSlope = Analyse.derivative(vFunction, vX);
                aPlot.setTangent(vX, vY, vSlope);
                vText.append("f(").append(Affichage.format(vX)).append(") = ").append(Affichage.format(vY))
                        .append(" &nbsp; f'(").append(Affichage.format(vX)).append(") = ")
                        .append(Affichage.format(vSlope)).append("<br>");
            }
            if (!aFrom.getText().trim().isEmpty() && !aTo.getText().trim().isEmpty()) {
                double vFrom = number(aFrom);
                double vTo = number(aTo);
                aPlot.setArea(vFrom, vTo);
                vText.append("Intégrale de ").append(Affichage.format(vFrom)).append(" à ")
                        .append(Affichage.format(vTo)).append(" = ")
                        .append(Affichage.format(Analyse.integral(vFunction, vFrom, vTo)));
            }
            aResult.setText(vText.append("</html>").toString());
            if (vText.length() == "<html></html>".length()) {
                aResult.setText(" ");
            }
        } catch (RuntimeException vException) {
            aResult.setText(MoteurCalcul.ERREUR + " : " + vException.getMessage());
        }
    }

    private static double number(JTextField pField) {
        return FonctionsScientifiques.parseNumbers(pField.getText())[0];
    }
}
