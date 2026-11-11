package com.kalkul;

/**
 * Matrice de nombres réels : addition, produit, transposée, déterminant, inverse et résolution
 * de systèmes linéaires. Une matrice ne change jamais après sa création.
 */
public final class Matrice {
    // Sous ce seuil, un pivot est considéré nul : la matrice n'est pas inversible
    private static final double PIVOT_EPSILON = 1e-12;

    private final double[][] aValues;

    public Matrice(double[][] pValues) {
        if (pValues.length == 0 || pValues[0].length == 0) {
            throw new IllegalArgumentException("Matrice vide");
        }
        aValues = new double[pValues.length][];
        for (int vRow = 0; vRow < pValues.length; vRow++) {
            if (pValues[vRow].length != pValues[0].length) {
                throw new IllegalArgumentException("Toutes les lignes doivent avoir le même nombre de colonnes");
            }
            aValues[vRow] = pValues[vRow].clone();
        }
    }

    /**
     * Lit une matrice : une ligne par ligne de texte (ou séparée par « ; »), nombres séparés par des espaces.
     */
    public static Matrice parse(String pText) {
        String[] vLines = pText.trim().split("[\\n;]+");
        double[][] vValues = new double[vLines.length][];
        for (int vRow = 0; vRow < vLines.length; vRow++) {
            vValues[vRow] = FonctionsScientifiques.parseNumbers(vLines[vRow]);
        }
        Matrice rMatrix = new Matrice(vValues);
        return rMatrix;
    }

    public int rows() {
        return aValues.length;
    }

    public int columns() {
        return aValues[0].length;
    }

    public double get(int pRow, int pColumn) {
        return aValues[pRow][pColumn];
    }

    public double[] row(int pRow) {
        double[] rRow = aValues[pRow].clone();
        return rRow;
    }

    public Matrice add(Matrice pOther) {
        return combine(pOther, 1);
    }

    public Matrice subtract(Matrice pOther) {
        return combine(pOther, -1);
    }

    private Matrice combine(Matrice pOther, int pSign) {
        if (rows() != pOther.rows() || columns() != pOther.columns()) {
            throw new IllegalArgumentException("Les deux matrices doivent avoir les mêmes dimensions");
        }
        double[][] vResult = new double[rows()][columns()];
        for (int vRow = 0; vRow < rows(); vRow++) {
            for (int vColumn = 0; vColumn < columns(); vColumn++) {
                vResult[vRow][vColumn] = aValues[vRow][vColumn] + pSign * pOther.aValues[vRow][vColumn];
            }
        }
        return new Matrice(vResult);
    }

    public Matrice multiply(Matrice pOther) {
        if (columns() != pOther.rows()) {
            throw new IllegalArgumentException("Produit impossible : " + columns()
                    + " colonnes à gauche, " + pOther.rows() + " lignes à droite");
        }
        double[][] vResult = new double[rows()][pOther.columns()];
        for (int vRow = 0; vRow < rows(); vRow++) {
            for (int vColumn = 0; vColumn < pOther.columns(); vColumn++) {
                double vSum = 0;
                for (int vK = 0; vK < columns(); vK++) {
                    vSum += aValues[vRow][vK] * pOther.aValues[vK][vColumn];
                }
                vResult[vRow][vColumn] = vSum;
            }
        }
        return new Matrice(vResult);
    }

    public Matrice transpose() {
        double[][] vResult = new double[columns()][rows()];
        for (int vRow = 0; vRow < rows(); vRow++) {
            for (int vColumn = 0; vColumn < columns(); vColumn++) {
                vResult[vColumn][vRow] = aValues[vRow][vColumn];
            }
        }
        return new Matrice(vResult);
    }

    public double determinant() {
        requireSquare();
        double[][] vWork = copyValues();
        int vSize = rows();
        double rDeterminant = 1;
        for (int vColumn = 0; vColumn < vSize; vColumn++) {
            int vPivotRow = pivotRow(vWork, vColumn);
            if (Math.abs(vWork[vPivotRow][vColumn]) < PIVOT_EPSILON) {
                return 0;
            }
            if (vPivotRow != vColumn) {
                swapRows(vWork, vPivotRow, vColumn);
                rDeterminant = -rDeterminant;
            }
            rDeterminant *= vWork[vColumn][vColumn];
            for (int vRow = vColumn + 1; vRow < vSize; vRow++) {
                double vFactor = vWork[vRow][vColumn] / vWork[vColumn][vColumn];
                for (int vK = vColumn; vK < vSize; vK++) {
                    vWork[vRow][vK] -= vFactor * vWork[vColumn][vK];
                }
            }
        }
        return rDeterminant;
    }

    /**
     * Inverse par la méthode de Gauss-Jordan ; échoue si la matrice n'est pas inversible.
     */
    public Matrice inverse() {
        requireSquare();
        int vSize = rows();
        double[][] vWork = new double[vSize][2 * vSize];
        for (int vRow = 0; vRow < vSize; vRow++) {
            System.arraycopy(aValues[vRow], 0, vWork[vRow], 0, vSize);
            vWork[vRow][vSize + vRow] = 1;
        }
        reduce(vWork, vSize);
        double[][] vResult = new double[vSize][vSize];
        for (int vRow = 0; vRow < vSize; vRow++) {
            System.arraycopy(vWork[vRow], vSize, vResult[vRow], 0, vSize);
        }
        return new Matrice(vResult);
    }

    /**
     * Résout A·x = b, où this est A (carrée) et pB une colonne (ou plusieurs seconds membres).
     */
    public Matrice solve(Matrice pB) {
        requireSquare();
        if (pB.rows() != rows()) {
            throw new IllegalArgumentException("Le second membre doit avoir " + rows() + " lignes");
        }
        int vSize = rows();
        int vRight = pB.columns();
        double[][] vWork = new double[vSize][vSize + vRight];
        for (int vRow = 0; vRow < vSize; vRow++) {
            System.arraycopy(aValues[vRow], 0, vWork[vRow], 0, vSize);
            System.arraycopy(pB.aValues[vRow], 0, vWork[vRow], vSize, vRight);
        }
        reduce(vWork, vSize);
        double[][] vResult = new double[vSize][vRight];
        for (int vRow = 0; vRow < vSize; vRow++) {
            System.arraycopy(vWork[vRow], vSize, vResult[vRow], 0, vRight);
        }
        return new Matrice(vResult);
    }

    // Gauss-Jordan avec choix du plus grand pivot : met la partie gauche (pSize colonnes) à l'identité
    private static void reduce(double[][] pWork, int pSize) {
        for (int vColumn = 0; vColumn < pSize; vColumn++) {
            int vPivotRow = pivotRow(pWork, vColumn);
            if (Math.abs(pWork[vPivotRow][vColumn]) < PIVOT_EPSILON) {
                throw new ArithmeticException("Matrice non inversible (déterminant nul)");
            }
            swapRows(pWork, vPivotRow, vColumn);
            double vPivot = pWork[vColumn][vColumn];
            for (int vK = 0; vK < pWork[vColumn].length; vK++) {
                pWork[vColumn][vK] /= vPivot;
            }
            for (int vRow = 0; vRow < pSize; vRow++) {
                if (vRow != vColumn) {
                    double vFactor = pWork[vRow][vColumn];
                    for (int vK = 0; vK < pWork[vRow].length; vK++) {
                        pWork[vRow][vK] -= vFactor * pWork[vColumn][vK];
                    }
                }
            }
        }
    }

    private static int pivotRow(double[][] pWork, int pColumn) {
        int rBest = pColumn;
        for (int vRow = pColumn + 1; vRow < pWork.length; vRow++) {
            if (Math.abs(pWork[vRow][pColumn]) > Math.abs(pWork[rBest][pColumn])) {
                rBest = vRow;
            }
        }
        return rBest;
    }

    private static void swapRows(double[][] pWork, int pFirst, int pSecond) {
        double[] vTemp = pWork[pFirst];
        pWork[pFirst] = pWork[pSecond];
        pWork[pSecond] = vTemp;
    }

    private void requireSquare() {
        if (rows() != columns()) {
            throw new IllegalArgumentException("La matrice doit être carrée (" + rows() + "x" + columns() + ")");
        }
    }

    private double[][] copyValues() {
        double[][] rCopy = new double[rows()][];
        for (int vRow = 0; vRow < rows(); vRow++) {
            rCopy[vRow] = aValues[vRow].clone();
        }
        return rCopy;
    }

    /**
     * Une ligne de texte par ligne de matrice, nombres séparés par des espaces.
     */
    @Override
    public String toString() {
        StringBuilder rText = new StringBuilder();
        for (int vRow = 0; vRow < rows(); vRow++) {
            if (vRow > 0) {
                rText.append('\n');
            }
            for (int vColumn = 0; vColumn < columns(); vColumn++) {
                if (vColumn > 0) {
                    rText.append("  ");
                }
                rText.append(Affichage.format(aValues[vRow][vColumn]));
            }
        }
        return rText.toString();
    }
}
