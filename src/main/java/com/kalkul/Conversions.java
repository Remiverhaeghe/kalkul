package com.kalkul;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Conversion d'unités de longueur, de surface, de masse, de volume, de température et de pente.
 */
public final class Conversions {
    private static final String TEMPERATURE = "température";
    private static final String PENTE = "pente";

    private static final Map<String, Unit> UNITS = new LinkedHashMap<>();

    static {
        add("mm", "longueur", 0.001);
        add("cm", "longueur", 0.01);
        add("m", "longueur", 1);
        add("km", "longueur", 1000);
        add("pouce", "longueur", 0.0254);
        add("pied", "longueur", 0.3048);
        add("mile", "longueur", 1609.344);
        add("mm²", "surface", 1e-6);
        add("cm²", "surface", 1e-4);
        add("m²", "surface", 1);
        add("are", "surface", 100);
        add("hectare", "surface", 10000);
        add("pied²", "surface", 0.09290304);
        add("g", "masse", 0.001);
        add("kg", "masse", 1);
        add("oz", "masse", 0.028349523125);
        add("lb", "masse", 0.45359237);
        add("t", "masse", 1000);
        add("ml", "volume", 0.001);
        add("l", "volume", 1);
        add("dm³", "volume", 1);
        add("m³", "volume", 1000);
        add("brouette", "volume", 90);
        add("°C", TEMPERATURE, 1);
        add("°F", TEMPERATURE, 1);
        add("K", TEMPERATURE, 1);
        // Pente : % et mm/m sont proportionnels, l'angle passe par la tangente
        add("% de pente", PENTE, 1);
        add("mm/m", PENTE, 0.1);
        add("cm/m", PENTE, 1);
        add("° de pente", PENTE, 1);
    }

    private Conversions() {
    }

    private static final class Unit {
        private final String aCategory;
        // Valeur d'une unité exprimée dans l'unité de base de sa catégorie (mètre, kilogramme, litre)
        private final double aFactor;

        private Unit(String pCategory, double pFactor) {
            aCategory = pCategory;
            aFactor = pFactor;
        }
    }

    private static void add(String pName, String pCategory, double pFactor) {
        UNITS.put(pName, new Unit(pCategory, pFactor));
    }

    /**
     * Noms des unités, dans l'ordre d'affichage.
     */
    public static String[] units() {
        String[] rUnits = UNITS.keySet().toArray(new String[0]);
        return rUnits;
    }

    /**
     * Convertit une valeur entre deux unités de la même catégorie.
     */
    public static double convert(double pValue, String pFrom, String pTo) {
        Unit vFrom = find(pFrom);
        Unit vTo = find(pTo);
        if (!vFrom.aCategory.equals(vTo.aCategory)) {
            throw new IllegalArgumentException("Conversion impossible entre " + pFrom + " et " + pTo);
        }
        double rResult;
        if (TEMPERATURE.equals(vFrom.aCategory)) {
            rResult = fromCelsius(toCelsius(pValue, pFrom), pTo);
        } else if (PENTE.equals(vFrom.aCategory)) {
            rResult = fromPercent(toPercent(pValue, pFrom), pTo);
        } else {
            rResult = pValue * vFrom.aFactor / vTo.aFactor;
        }
        return rResult;
    }

    private static Unit find(String pName) {
        Unit rUnit = UNITS.get(pName);
        if (rUnit == null) {
            throw new IllegalArgumentException("Unité inconnue : " + pName);
        }
        return rUnit;
    }

    private static double toCelsius(double pValue, String pUnit) {
        double rCelsius;
        switch (pUnit) {
            case "°F":
                rCelsius = (pValue - 32) * 5 / 9;
                break;
            case "K":
                rCelsius = pValue - 273.15;
                break;
            default:
                rCelsius = pValue;
                break;
        }
        return rCelsius;
    }

    private static double fromCelsius(double pCelsius, String pUnit) {
        double rValue;
        switch (pUnit) {
            case "°F":
                rValue = pCelsius * 9 / 5 + 32;
                break;
            case "K":
                rValue = pCelsius + 273.15;
                break;
            default:
                rValue = pCelsius;
                break;
        }
        return rValue;
    }

    private static double toPercent(double pValue, String pUnit) {
        double rPercent;
        if ("° de pente".equals(pUnit)) {
            if (Math.abs(pValue) >= 90) {
                throw new IllegalArgumentException("Une pente se mesure entre -90° et 90°");
            }
            rPercent = Math.tan(Math.toRadians(pValue)) * 100;
        } else {
            rPercent = pValue * find(pUnit).aFactor;
        }
        return rPercent;
    }

    private static double fromPercent(double pPercent, String pUnit) {
        double rValue;
        if ("° de pente".equals(pUnit)) {
            rValue = Math.toDegrees(Math.atan(pPercent / 100));
        } else {
            rValue = pPercent / find(pUnit).aFactor;
        }
        return rValue;
    }
}
