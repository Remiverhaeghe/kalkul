package com.kalkul;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.Locale;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Onglet des formules financières : intérêts, prêt et amortissement, VAN, TRI, trésorerie
 * et conversion monétaire. Le calcul est fait par {@link FormulesFinancieres}.
 */
class PanneauFinance extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JTextArea aSchedule = new JTextArea(6, 40);

    PanneauFinance() {
        setLayout(new BorderLayout());
        JPanel vForms = new JPanel();
        vForms.setLayout(new BoxLayout(vForms, BoxLayout.Y_AXIS));
        vForms.add(buildInterest());
        vForms.add(buildLoan());
        vForms.add(buildCashFlows());
        vForms.add(buildCurrency());
        add(new JScrollPane(vForms), BorderLayout.CENTER);

        aSchedule.setEditable(false);
        aSchedule.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        add(new JScrollPane(aSchedule), BorderLayout.SOUTH);
    }

    private JPanel buildInterest() {
        JPanel vSection = PanneauOutils.section("Intérêts (capital, taux %, années, capitalisations par an)");
        JTextField vPrincipal = new JTextField(7);
        JTextField vRate = new JTextField(4);
        JTextField vYears = new JTextField(4);
        JTextField vPeriods = new JTextField("1", 3);
        JLabel vResult = new JLabel(" ");
        vSection.add(PanneauOutils.row(vPrincipal, vRate, vYears, vPeriods,
                PanneauOutils.button("Calculer", vResult, () -> {
                    double vCapital = number(vPrincipal);
                    double vPercent = number(vRate);
                    double vDuration = number(vYears);
                    double vSimple = FormulesFinancieres.simpleInterest(vCapital, vPercent, vDuration);
                    double vCompound = FormulesFinancieres.compoundAmount(
                            vCapital, vPercent, vDuration, (int) number(vPeriods));
                    return "Simple : " + money(vSimple) + " d'intérêts, capital final " + money(vCapital + vSimple)
                            + " | composé : capital final " + money(vCompound);
                })));
        vSection.add(vResult);
        return vSection;
    }

    private JPanel buildLoan() {
        JPanel vSection = PanneauOutils.section("Prêt (capital, taux annuel %, durée en mois)");
        JTextField vPrincipal = new JTextField(7);
        JTextField vRate = new JTextField(4);
        JTextField vMonths = new JTextField(4);
        JLabel vResult = new JLabel(" ");
        vSection.add(PanneauOutils.row(vPrincipal, vRate, vMonths,
                PanneauOutils.button("Mensualité et tableau", vResult, () -> {
                    double vCapital = number(vPrincipal);
                    double vPercent = number(vRate);
                    int vDuration = (int) number(vMonths);
                    double[][] vRows = FormulesFinancieres.amortization(vCapital, vPercent, vDuration);
                    showSchedule(vRows);
                    double vInterests = vRows[vRows.length - 1][1] * vDuration - vCapital;
                    return "Mensualité " + money(vRows[0][1]) + " | coût total des intérêts " + money(vInterests);
                })));
        vSection.add(vResult);
        return vSection;
    }

    private JPanel buildCashFlows() {
        JPanel vSection = PanneauOutils.section("Flux de trésorerie (le premier est à la date 0, souvent négatif)");
        JTextField vFlows = new JTextField(22);
        JTextField vRate = new JTextField(4);
        JLabel vResult = new JLabel(" ");
        vSection.add(PanneauOutils.row(vFlows, new JLabel("taux %"), vRate,
                PanneauOutils.button("VAN, TRI, cumul", vResult, () -> {
                    double[] vValues = FonctionsScientifiques.parseNumbers(vFlows.getText());
                    String rText = "VAN " + money(FormulesFinancieres.npv(number(vRate), vValues));
                    try {
                        rText += " | TRI " + PanneauOutils.format(FormulesFinancieres.irr(vValues) * 100) + " %";
                    } catch (ArithmeticException vException) {
                        rText += " | " + vException.getMessage();
                    }
                    int vPayback = FormulesFinancieres.paybackPeriod(vValues);
                    rText += vPayback < 0 ? " | jamais rentabilisé" : " | rentabilisé à la période " + vPayback;
                    return rText + " | cumul " + listOf(FormulesFinancieres.cumulative(vValues));
                })));
        vSection.add(vResult);
        return vSection;
    }

    private JPanel buildCurrency() {
        JPanel vSection = PanneauOutils.section("Conversion monétaire (montant, taux : 1 départ = x arrivée)");
        JTextField vAmount = new JTextField(8);
        JTextField vRate = new JTextField(6);
        JLabel vResult = new JLabel(" ");
        vSection.add(PanneauOutils.row(vAmount, vRate,
                PanneauOutils.button("Convertir", vResult, () ->
                        money(FormulesFinancieres.convertCurrency(number(vAmount), number(vRate))))));
        vSection.add(vResult);
        return vSection;
    }

    private void showSchedule(double[][] pRows) {
        StringBuilder vText = new StringBuilder();
        vText.append(String.format(Locale.FRANCE, "%4s %12s %12s %12s %14s%n",
                "Mois", "Mensualité", "Intérêts", "Capital", "Restant dû"));
        for (double[] vRow : pRows) {
            vText.append(String.format(Locale.FRANCE, "%4d %12.2f %12.2f %12.2f %14.2f%n",
                    (int) vRow[0], vRow[1], vRow[2], vRow[3], Math.max(0, vRow[4])));
        }
        aSchedule.setText(vText.toString());
        aSchedule.setCaretPosition(0);
    }

    private static double number(JTextField pField) {
        return FonctionsScientifiques.parseNumbers(pField.getText())[0];
    }

    private static String money(double pValue) {
        return String.format(Locale.FRANCE, "%,.2f", pValue);
    }

    private static String listOf(double[] pValues) {
        StringBuilder rText = new StringBuilder();
        for (double vValue : pValues) {
            if (rText.length() > 0) {
                rText.append("; ");
            }
            rText.append(money(vValue));
        }
        return rText.toString();
    }
}
