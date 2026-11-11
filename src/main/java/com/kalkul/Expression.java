package com.kalkul;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.DoubleUnaryOperator;

/**
 * Formule saisie par l'utilisateur, par exemple {@code 2x + 1} ou {@code a*sin(x) + b}.
 *
 * <p>Opérateurs : + - * / ^ (la puissance se lit de droite à gauche), parenthèses, multiplication
 * implicite ({@code 2x}, {@code x(x+1)}). Constantes : pi et e. Fonctions : sin, cos, tan, asin, acos,
 * atan, exp, ln, log, sqrt, abs, avec les angles en radians. Toute autre lettre est une variable.
 * Le séparateur décimal est le point.
 */
public final class Expression {
    private static final Set<String> FUNCTIONS = new HashSet<>(Arrays.asList(
            "sin", "cos", "tan", "asin", "acos", "atan", "exp", "ln", "log", "sqrt", "abs"));

    private interface Node {
        double eval(Map<String, Double> pVariables);
    }

    private final Node aRoot;
    private final Set<String> aVariables;

    private Expression(Node pRoot, Set<String> pVariables) {
        aRoot = pRoot;
        aVariables = pVariables;
    }

    /**
     * Lit une formule. Une formule incorrecte lève une IllegalArgumentException avec un message en français.
     */
    public static Expression parse(String pText) {
        Parser vParser = new Parser(pText);
        Node vRoot = vParser.parseAll();
        Expression rExpression = new Expression(vRoot, vParser.aVariables);
        return rExpression;
    }

    /**
     * Évalue la formule pour x. Une autre variable que x demande {@link #evaluate(Map)}.
     */
    public double evaluate(double pX) {
        Map<String, Double> vVariables = new HashMap<>();
        vVariables.put("x", pX);
        double rValue = evaluate(vVariables);
        return rValue;
    }

    public double evaluate(Map<String, Double> pVariables) {
        double rValue = aRoot.eval(pVariables);
        return rValue;
    }

    /**
     * Formule vue comme une fonction de x : les autres variables prennent les valeurs données.
     */
    public DoubleUnaryOperator bind(Map<String, Double> pParameters) {
        DoubleUnaryOperator rFunction = pX -> {
            Map<String, Double> vVariables = new HashMap<>(pParameters);
            vVariables.put("x", pX);
            return aRoot.eval(vVariables);
        };
        return rFunction;
    }

    /**
     * Noms des variables utilisées, dans l'ordre d'apparition.
     */
    public Set<String> variables() {
        Set<String> rVariables = new LinkedHashSet<>(aVariables);
        return rVariables;
    }

    /**
     * Lit des paramètres du type {@code a=2 b=1,5 c=-3} (séparés par des espaces ou des points-virgules).
     */
    public static Map<String, Double> parseParameters(String pText) {
        Map<String, Double> rParameters = new HashMap<>();
        String vClean = pText.trim();
        if (vClean.isEmpty()) {
            return rParameters;
        }
        for (String vPart : vClean.split("[\\s;]+")) {
            int vEqual = vPart.indexOf('=');
            if (vEqual < 1) {
                throw new IllegalArgumentException("Paramètre invalide : " + vPart + " (attendu : nom=valeur)");
            }
            String vName = vPart.substring(0, vEqual);
            try {
                rParameters.put(vName, Double.parseDouble(vPart.substring(vEqual + 1).replace(',', '.')));
            } catch (NumberFormatException vException) {
                throw new IllegalArgumentException("Valeur invalide pour " + vName);
            }
        }
        return rParameters;
    }

    private static final class Parser {
        private final String aText;
        private int aPosition = 0;
        private final Set<String> aVariables = new LinkedHashSet<>();

        private Parser(String pText) {
            aText = pText;
        }

        private Node parseAll() {
            if (aText.trim().isEmpty()) {
                throw new IllegalArgumentException("Formule vide");
            }
            Node rRoot = parseSum();
            skipSpaces();
            if (aPosition < aText.length()) {
                throw error("Caractère inattendu « " + aText.charAt(aPosition) + " »");
            }
            return rRoot;
        }

        private Node parseSum() {
            Node vLeft = parseProduct();
            while (true) {
                char vSign = peek();
                if (vSign != '+' && vSign != '-') {
                    break;
                }
                aPosition++;
                final Node vA = vLeft;
                final Node vB = parseProduct();
                vLeft = vSign == '+' ? pVars -> vA.eval(pVars) + vB.eval(pVars)
                        : pVars -> vA.eval(pVars) - vB.eval(pVars);
            }
            return vLeft;
        }

        private Node parseProduct() {
            Node vLeft = parseUnary();
            while (true) {
                char vSign = peek();
                final Node vA = vLeft;
                if (vSign == '*' || vSign == '/') {
                    aPosition++;
                    final Node vB = parseUnary();
                    vLeft = vSign == '*' ? pVars -> vA.eval(pVars) * vB.eval(pVars)
                            : pVars -> vA.eval(pVars) / vB.eval(pVars);
                } else if (Character.isLetter(vSign) || vSign == '(') {
                    // Multiplication implicite : 2x, 3(x+1), x sin(x)
                    final Node vB = parsePower();
                    vLeft = pVars -> vA.eval(pVars) * vB.eval(pVars);
                } else {
                    break;
                }
            }
            return vLeft;
        }

        private Node parseUnary() {
            char vSign = peek();
            Node rNode;
            if (vSign == '-') {
                aPosition++;
                final Node vInner = parseUnary();
                rNode = pVars -> -vInner.eval(pVars);
            } else if (vSign == '+') {
                aPosition++;
                rNode = parseUnary();
            } else {
                rNode = parsePower();
            }
            return rNode;
        }

        private Node parsePower() {
            final Node vBase = parseAtom();
            Node rNode = vBase;
            if (peek() == '^') {
                aPosition++;
                final Node vExponent = parseUnary();
                rNode = pVars -> Math.pow(vBase.eval(pVars), vExponent.eval(pVars));
            }
            return rNode;
        }

        private Node parseAtom() {
            char vChar = peek();
            Node rNode;
            if (Character.isDigit(vChar) || vChar == '.') {
                rNode = parseNumber();
            } else if (vChar == '(') {
                aPosition++;
                rNode = parseSum();
                expect(')');
            } else if (Character.isLetter(vChar)) {
                rNode = parseName();
            } else if (vChar == '\0') {
                throw error("Formule incomplète");
            } else {
                throw error("Caractère inattendu « " + vChar + " »");
            }
            return rNode;
        }

        private Node parseNumber() {
            int vStart = aPosition;
            while (aPosition < aText.length()
                    && (Character.isDigit(aText.charAt(aPosition)) || aText.charAt(aPosition) == '.')) {
                aPosition++;
            }
            final double vValue;
            try {
                vValue = Double.parseDouble(aText.substring(vStart, aPosition));
            } catch (NumberFormatException vException) {
                throw error("Nombre invalide « " + aText.substring(vStart, aPosition) + " »");
            }
            Node rNode = pVars -> vValue;
            return rNode;
        }

        private Node parseName() {
            int vStart = aPosition;
            while (aPosition < aText.length() && Character.isLetter(aText.charAt(aPosition))) {
                aPosition++;
            }
            final String vName = aText.substring(vStart, aPosition).toLowerCase(Locale.ROOT);
            Node rNode;
            if (FUNCTIONS.contains(vName) && peek() == '(') {
                aPosition++;
                final Node vArgument = parseSum();
                expect(')');
                rNode = pVars -> applyFunction(vName, vArgument.eval(pVars));
            } else if ("pi".equals(vName)) {
                rNode = pVars -> Math.PI;
            } else if ("e".equals(vName)) {
                rNode = pVars -> Math.E;
            } else {
                aVariables.add(vName);
                rNode = pVars -> {
                    Double vValue = pVars.get(vName);
                    if (vValue == null) {
                        throw new IllegalArgumentException("Valeur manquante pour « " + vName + " »");
                    }
                    return vValue;
                };
            }
            return rNode;
        }

        private void expect(char pChar) {
            if (peek() != pChar) {
                throw error("« " + pChar + " » attendu");
            }
            aPosition++;
        }

        private char peek() {
            skipSpaces();
            char rChar = aPosition < aText.length() ? aText.charAt(aPosition) : '\0';
            return rChar;
        }

        private void skipSpaces() {
            while (aPosition < aText.length() && Character.isWhitespace(aText.charAt(aPosition))) {
                aPosition++;
            }
        }

        private IllegalArgumentException error(String pMessage) {
            return new IllegalArgumentException(pMessage + " (position " + (aPosition + 1) + ")");
        }
    }

    private static double applyFunction(String pName, double pValue) {
        double rResult;
        switch (pName) {
            case "sin":
                rResult = Math.sin(pValue);
                break;
            case "cos":
                rResult = Math.cos(pValue);
                break;
            case "tan":
                rResult = Math.tan(pValue);
                break;
            case "asin":
                rResult = Math.asin(pValue);
                break;
            case "acos":
                rResult = Math.acos(pValue);
                break;
            case "atan":
                rResult = Math.atan(pValue);
                break;
            case "exp":
                rResult = Math.exp(pValue);
                break;
            case "ln":
                rResult = Math.log(pValue);
                break;
            case "log":
                rResult = Math.log10(pValue);
                break;
            case "sqrt":
                rResult = Math.sqrt(pValue);
                break;
            default:
                rResult = Math.abs(pValue);
                break;
        }
        return rResult;
    }
}
