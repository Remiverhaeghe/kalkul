package com.kalkul;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Logique de la calculatrice, sans dépendance à Swing pour pouvoir être testée.
 * L'interface lui transmet chaque touche et affiche le texte qu'elle renvoie.
 */
public class MoteurCalcul {
    public static final String ERREUR = "ERREUR";

    // Fonctions à un seul nombre, appliquées tout de suite à l'écran
    private static final Set<String> UNARY_FUNCTIONS = new HashSet<>(
            Arrays.asList("x²", "√", "sin", "cos", "tan", "asin", "acos", "atan", "exp", "ln", "log", "n!"));

    // Chiffres significatifs gardés à l'affichage : masque les erreurs d'arrondi (0,1 + 0,2)
    private static final int DISPLAY_DIGITS = 12;

    // Texte affiché à l'écran
    private String aDisplay = "0";
    // Valeur mémorisée avant l'opérateur
    private double aOperand = 0;
    // Opérateur en attente (+, -, *, /), vide s'il n'y en a pas
    private String aOperator = "";
    // Vrai quand la prochaine touche chiffre commence un nouveau nombre
    private boolean aStartNewNumber = true;
    // Angles en degrés (vrai) ou en radians (faux) pour les fonctions trigonométriques
    private boolean aDegrees = true;

    public void setDegrees(boolean pDegrees) {
        aDegrees = pDegrees;
    }

    public boolean isDegrees() {
        boolean rDegrees = aDegrees;
        return rDegrees;
    }

    public String getDisplay() {
        String rDisplay = aDisplay;
        return rDisplay;
    }

    /**
     * Traite une touche : chiffre, point, opérateur, =, C, ±, % ou retour arrière.
     *
     * @param pInput texte de la touche
     */
    public void handleInput(String pInput) {
        if (pInput.matches("[0-9]")) {
            numberPressed(pInput);
        } else if (".".equals(pInput)) {
            dotPressed();
        } else if ("+".equals(pInput) || "-".equals(pInput) || "*".equals(pInput) || "/".equals(pInput)
                || "^".equals(pInput) || "mod".equals(pInput)) {
            operatorPressed(pInput);
        } else if (UNARY_FUNCTIONS.contains(pInput)) {
            unaryPressed(pInput);
        } else if ("=".equals(pInput) || "\n".equals(pInput) || "\r".equals(pInput)) {
            equalPressed();
        } else if ("C".equals(pInput) || "c".equals(pInput)) {
            clearAll();
        } else if ("±".equals(pInput)) {
            plusMinus();
        } else if ("%".equals(pInput)) {
            percent();
        } else if ("\b".equals(pInput) || "⌫".equals(pInput)) {
            backspace();
        }
    }

    private void numberPressed(String pDigit) {
        if (aStartNewNumber || "0".equals(aDisplay)) {
            aDisplay = pDigit;
            aStartNewNumber = false;
        } else {
            aDisplay = aDisplay + pDigit;
        }
    }

    private void dotPressed() {
        if (aStartNewNumber) {
            aDisplay = "0.";
            aStartNewNumber = false;
        } else if (!aDisplay.contains(".")) {
            aDisplay = aDisplay + ".";
        }
    }

    private void operatorPressed(String pOperator) {
        try {
            double vCurrent = Double.parseDouble(aDisplay);
            if (!aOperator.isEmpty()) {
                aOperand = calculate(aOperand, vCurrent, aOperator);
            } else {
                aOperand = vCurrent;
            }
            aOperator = pOperator;
            aDisplay = trimZeros(aOperand);
            aStartNewNumber = true;
        } catch (RuntimeException vException) {
            showError();
        }
    }

    private void unaryPressed(String pName) {
        try {
            double vResult = FonctionsScientifiques.apply(pName, Double.parseDouble(aDisplay), aDegrees);
            aDisplay = trimZeros(vResult);
            aStartNewNumber = true;
        } catch (RuntimeException vException) {
            showError();
        }
    }

    private void equalPressed() {
        if (aOperator.isEmpty()) {
            return;
        }
        try {
            double vCurrent = Double.parseDouble(aDisplay);
            double vResult = calculate(aOperand, vCurrent, aOperator);
            aDisplay = trimZeros(vResult);
            aOperator = "";
            aStartNewNumber = true;
            aOperand = vResult;
        } catch (RuntimeException vException) {
            showError();
        }
    }

    static double calculate(double pLeft, double pRight, String pOperator) {
        double rResult;
        switch (pOperator) {
            case "+":
                rResult = pLeft + pRight;
                break;
            case "-":
                rResult = pLeft - pRight;
                break;
            case "*":
                rResult = pLeft * pRight;
                break;
            case "/":
                if (pRight == 0) {
                    throw new ArithmeticException("Division par zéro impossible");
                }
                rResult = pLeft / pRight;
                break;
            case "mod":
                if (pRight == 0) {
                    throw new ArithmeticException("Division par zéro impossible");
                }
                rResult = pLeft % pRight;
                break;
            case "^":
                rResult = Math.pow(pLeft, pRight);
                if (Double.isNaN(rResult) || Double.isInfinite(rResult)) {
                    throw new ArithmeticException("Puissance impossible");
                }
                break;
            default:
                rResult = pRight;
                break;
        }
        return rResult;
    }

    private void clearAll() {
        aDisplay = "0";
        aOperand = 0;
        aOperator = "";
        aStartNewNumber = true;
    }

    private void plusMinus() {
        try {
            aDisplay = trimZeros(-Double.parseDouble(aDisplay));
        } catch (RuntimeException vException) {
            showError();
        }
    }

    private void percent() {
        try {
            aDisplay = trimZeros(Double.parseDouble(aDisplay) / 100.0);
            aStartNewNumber = true;
        } catch (RuntimeException vException) {
            showError();
        }
    }

    private void backspace() {
        if (aDisplay.length() <= 1 || ERREUR.equals(aDisplay)) {
            aDisplay = "0";
            aStartNewNumber = true;
        } else {
            aDisplay = aDisplay.substring(0, aDisplay.length() - 1);
        }
    }

    private void showError() {
        aDisplay = ERREUR;
        aOperator = "";
        aStartNewNumber = true;
    }

    static String trimZeros(double pValue) {
        // BigDecimal lève une NumberFormatException pour NaN et l'infini : l'appelant affiche ERREUR
        String rText = new BigDecimal(pValue)
                .round(new MathContext(DISPLAY_DIGITS))
                .stripTrailingZeros()
                .toPlainString();
        return rText;
    }
}
