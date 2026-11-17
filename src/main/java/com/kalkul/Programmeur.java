package com.kalkul;

import java.util.Locale;

/**
 * Calculs de programmeur sur des entiers de 8, 16, 32 ou 64 bits : bases 2, 8, 10 et 16,
 * opérations bit à bit et décalages. Les nombres négatifs sont en complément à deux.
 */
final class Programmeur {
    static final int[] TAILLES = {8, 16, 32, 64};
    static final int[] BASES = {16, 10, 8, 2};

    private Programmeur() {
    }

    private static long masque(int pBits) {
        long rMasque = pBits >= 64 ? -1L : (1L << pBits) - 1;
        return rMasque;
    }

    /**
     * Ramène une valeur sur pBits bits, en gardant le signe du complément à deux.
     */
    static long borner(long pValeur, int pBits) {
        long rValeur = pValeur;
        if (pBits < 64) {
            rValeur = pValeur & masque(pBits);
            if ((rValeur & (1L << (pBits - 1))) != 0) {
                rValeur |= ~masque(pBits);
            }
        }
        return rValeur;
    }

    /**
     * Lit un nombre écrit en base pBase ; espaces, tirets bas et préfixes 0x, 0b, 0o acceptés.
     */
    static long lire(String pTexte, int pBase, int pBits) {
        String vTexte = pTexte.replace(" ", "").replace("_", "").toLowerCase(Locale.ROOT);
        String vPrefixe = pBase == 16 ? "0x" : pBase == 2 ? "0b" : pBase == 8 ? "0o" : "";
        if (!vPrefixe.isEmpty() && vTexte.startsWith(vPrefixe)) {
            vTexte = vTexte.substring(2);
        }
        if (vTexte.isEmpty()) {
            throw new NumberFormatException("Nombre vide");
        }
        long vValeur = pBase == 10 ? Long.parseLong(vTexte) : Long.parseUnsignedLong(vTexte, pBase);
        if (pBase != 10 && pBits < 64 && (vValeur & ~masque(pBits)) != 0) {
            throw new NumberFormatException("Trop grand pour " + pBits + " bits");
        }
        long rValeur = borner(vValeur, pBits);
        return rValeur;
    }

    /**
     * Écrit une valeur en base pBase : signée en décimal, en complément à deux sinon,
     * binaire complet groupé par 4.
     */
    static String ecrire(long pValeur, int pBase, int pBits) {
        long vValeur = borner(pValeur, pBits);
        String rTexte;
        if (pBase == 10) {
            rTexte = Long.toString(vValeur);
        } else {
            rTexte = Long.toUnsignedString(vValeur & masque(pBits), pBase).toUpperCase(Locale.ROOT);
            if (pBase == 2) {
                // Tous les bits de la taille choisie, par paquets de 4
                rTexte = grouper("0".repeat(pBits - rTexte.length()) + rTexte, 4);
            }
        }
        return rTexte;
    }

    private static String grouper(String pChiffres, int pTaille) {
        StringBuilder vTexte = new StringBuilder();
        int vDebut = pChiffres.length() % pTaille;
        if (vDebut > 0) {
            vTexte.append("0".repeat(pTaille - vDebut));
        }
        vTexte.append(pChiffres);
        StringBuilder rTexte = new StringBuilder();
        for (int vIndex = 0; vIndex < vTexte.length(); vIndex += pTaille) {
            if (rTexte.length() > 0) {
                rTexte.append(' ');
            }
            rTexte.append(vTexte, vIndex, vIndex + pTaille);
        }
        return rTexte.toString();
    }

    /**
     * Opération entre deux entiers, résultat ramené sur pBits bits.
     */
    static long calculer(long pGauche, long pDroite, String pOperation, int pBits) {
        long vResultat;
        switch (pOperation) {
            case "AND":
                vResultat = pGauche & pDroite;
                break;
            case "OR":
                vResultat = pGauche | pDroite;
                break;
            case "XOR":
                vResultat = pGauche ^ pDroite;
                break;
            case "<<":
                vResultat = pGauche << decalage(pDroite, pBits);
                break;
            case ">>":
                vResultat = pGauche >> decalage(pDroite, pBits);
                break;
            case "+":
                vResultat = pGauche + pDroite;
                break;
            case "-":
                vResultat = pGauche - pDroite;
                break;
            case "*":
                vResultat = pGauche * pDroite;
                break;
            case "/":
            case "mod":
                if (pDroite == 0) {
                    throw new ArithmeticException("Division par zéro impossible");
                }
                vResultat = "/".equals(pOperation) ? pGauche / pDroite : pGauche % pDroite;
                break;
            default:
                throw new IllegalArgumentException("Opération inconnue : " + pOperation);
        }
        long rResultat = borner(vResultat, pBits);
        return rResultat;
    }

    private static int decalage(long pDecalage, int pBits) {
        if (pDecalage < 0 || pDecalage >= pBits) {
            throw new ArithmeticException("Décalage de 0 à " + (pBits - 1) + " bits");
        }
        return (int) pDecalage;
    }

    /** Tous les bits inversés. */
    static long non(long pValeur, int pBits) {
        long rValeur = borner(~pValeur, pBits);
        return rValeur;
    }

    /** Nombre de bits à 1 sur la taille choisie. */
    static int bitsAUn(long pValeur, int pBits) {
        int rNombre = Long.bitCount(pValeur & masque(pBits));
        return rNombre;
    }
}
