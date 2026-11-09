package com.calculatrice;

/**
 * Logique de la calculatrice, sans dépendance à Swing pour pouvoir être testée.
 * L'interface lui transmet chaque touche et affiche le texte qu'elle renvoie.
 */
public class MoteurCalcul {
    public static final String ERREUR = "ERREUR";

    // Texte affiché à l'écran
    private String aDisplay = "0";
    // Valeur mémorisée avant l'opérateur
    private double aOperand = 0;
    // Opérateur en attente (+, -, *, /), vide s'il n'y en a pas
    private String aOperator = "";
    // Vrai quand la prochaine touche chiffre commence un nouveau nombre
    private boolean aStartNewNumber = true;

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
        } else if ("+".equals(pInput) || "-".equals(pInput) || "*".equals(pInput) || "/".equals(pInput)) {
            operatorPressed(pInput);
        } else if ("=".equals(pInput) || "\n".equals(pInput) || "\r".equals(pInput)) {
            equalPressed();
        } else if ("C".equals(pInput) || "c".equals(pInput)) {
            clearAll();
        } else if ("±".equals(pInput)) {
            plusMinus();
        } else if ("%".equals(pInput)) {
            percent();
        } else if ("\b".equals(pInput)) {
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
        String rText;
        if (pValue == (long) pValue) {
            rText = String.format("%d", (long) pValue);
        } else {
            rText = String.valueOf(pValue);
        }
        return rText;
    }
}
