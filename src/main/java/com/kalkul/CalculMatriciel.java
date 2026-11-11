package com.kalkul;

/**
 * Opérations proposées dans l'onglet Matrices, avec leur résultat sous forme de texte.
 */
final class CalculMatriciel {
    static final String[] OPERATIONS = {
        "A + B", "A − B", "A × B", "Transposée de A", "Déterminant de A", "Inverse de A",
        "Résoudre A·x = B", "Produit scalaire (1re ligne de A et de B)",
        "Produit vectoriel (1re ligne de A et de B)", "Norme (1re ligne de A)"
    };

    private CalculMatriciel() {
    }

    /**
     * Applique une opération de {@link #OPERATIONS}. La matrice B peut être vide pour les opérations sur A seule.
     */
    static String apply(String pOperation, String pTextA, String pTextB) {
        Matrice vA = Matrice.parse(pTextA);
        String rResult;
        switch (pOperation) {
            case "A + B":
                rResult = vA.add(Matrice.parse(pTextB)).toString();
                break;
            case "A − B":
                rResult = vA.subtract(Matrice.parse(pTextB)).toString();
                break;
            case "A × B":
                rResult = vA.multiply(Matrice.parse(pTextB)).toString();
                break;
            case "Transposée de A":
                rResult = vA.transpose().toString();
                break;
            case "Déterminant de A":
                rResult = "det(A) = " + Affichage.format(vA.determinant());
                break;
            case "Inverse de A":
                rResult = vA.inverse().toString();
                break;
            case "Résoudre A·x = B":
                rResult = describeSolution(vA.solve(Matrice.parse(pTextB)));
                break;
            case "Produit scalaire (1re ligne de A et de B)":
                rResult = "A · B = " + Affichage.format(Vecteurs.dot(vA.row(0), Matrice.parse(pTextB).row(0)));
                break;
            case "Produit vectoriel (1re ligne de A et de B)":
                rResult = new Matrice(new double[][] {
                    Vecteurs.cross(vA.row(0), Matrice.parse(pTextB).row(0))}).toString();
                break;
            case "Norme (1re ligne de A)":
                rResult = "‖A‖ = " + Affichage.format(Vecteurs.norm(vA.row(0)));
                break;
            default:
                throw new IllegalArgumentException("Opération inconnue : " + pOperation);
        }
        return rResult;
    }

    // Une inconnue par ligne : x1 = ..., x2 = ... (une colonne de solutions par second membre)
    private static String describeSolution(Matrice pSolution) {
        StringBuilder rText = new StringBuilder();
        for (int vRow = 0; vRow < pSolution.rows(); vRow++) {
            if (vRow > 0) {
                rText.append('\n');
            }
            rText.append('x').append(vRow + 1).append(" =");
            for (int vColumn = 0; vColumn < pSolution.columns(); vColumn++) {
                rText.append("  ").append(Affichage.format(pSolution.get(vRow, vColumn)));
            }
        }
        return rText.toString();
    }
}
