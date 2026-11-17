package com.kalkul;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    /** Calculs gardés dans l'historique, les plus anciens sortent en premier. */
    public static final int HISTORIQUE_MAX = 50;

    // Signes affichés dans l'historique
    private static final Map<String, String> SIGNS = Map.of("/", "÷", "*", "×", "-", "−");

    /** Un calcul terminé : ce qui a été tapé et le résultat. */
    public record Calcul(String expression, String resultat) {
    }

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
    // Calculs terminés, le plus récent en tête
    private final Deque<Calcul> aHistory = new ArrayDeque<>();
    // Calcul terminé par la dernière touche, null sinon
    private Calcul aLastCalculation;

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
        aLastCalculation = null;
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
            double vValue = Double.parseDouble(aDisplay);
            double vResult = FonctionsScientifiques.apply(pName, vValue, aDegrees);
            aDisplay = trimZeros(vResult);
            aStartNewNumber = true;
            record(unaryExpression(pName, trimZeros(vValue)), aDisplay);
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
            String vExpression = trimZeros(aOperand) + " " + SIGNS.getOrDefault(aOperator, aOperator) + " "
                    + trimZeros(vCurrent);
            aDisplay = trimZeros(vResult);
            record(vExpression, aDisplay);
            aOperator = "";
            aStartNewNumber = true;
            aOperand = vResult;
        } catch (RuntimeException vException) {
            showError();
        }
    }

    /**
     * Écriture d'une fonction appliquée à un nombre : 3², √9, 5!, sin(30).
     */
    static String unaryExpression(String pName, String pValue) {
        String rText;
        switch (pName) {
            case "x²":
                rText = pValue + "²";
                break;
            case "√":
                rText = "√" + pValue;
                break;
            case "n!":
                rText = pValue + "!";
                break;
            default:
                rText = pName + "(" + pValue + ")";
                break;
        }
        return rText;
    }

    private void record(String pExpression, String pResult) {
        aLastCalculation = new Calcul(pExpression, pResult);
        aHistory.addFirst(aLastCalculation);
        if (aHistory.size() > HISTORIQUE_MAX) {
            aHistory.removeLast();
        }
    }

    /**
     * Calculs terminés, du plus récent au plus ancien.
     */
    public List<Calcul> getHistorique() {
        List<Calcul> rHistory = List.copyOf(new ArrayList<>(aHistory));
        return rHistory;
    }

    /**
     * Calcul terminé par la dernière touche, ou null si elle n'a rien calculé.
     */
    public Calcul getDernierCalcul() {
        Calcul rLast = aLastCalculation;
        return rLast;
    }

    public void effacerHistorique() {
        aHistory.clear();
    }

    /**
     * Remet un résultat de l'historique à l'écran pour continuer le calcul avec ; ignore un texte qui
     * n'est pas un nombre.
     */
    public void reprendre(String pValue) {
        try {
            aDisplay = trimZeros(Double.parseDouble(pValue));
            aStartNewNumber = true;
        } catch (RuntimeException vException) {
            // Un texte qui n'est pas un nombre ne change rien
            return;
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
