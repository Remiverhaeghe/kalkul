package com.kalkul;

/**
 * Mise en forme des nombres pour l'affichage.
 */
final class Affichage {
    private Affichage() {
    }

    /**
     * Nombre arrondi à 12 chiffres significatifs, ou « indéfini » pour NaN et l'infini.
     */
    static String format(double pValue) {
        String rText;
        if (Double.isNaN(pValue) || Double.isInfinite(pValue)) {
            rText = "indéfini";
        } else {
            rText = MoteurCalcul.trimZeros(pValue);
        }
        return rText;
    }
}
