package com.kalkul;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Sous-onglet Matrices et systèmes : deux matrices saisies en texte, une opération, le résultat.
 * Le calcul est fait par {@link CalculMatriciel}.
 */
class PanneauMatrices extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JTextArea aMatrixA = new JTextArea("1 1\n2 -1", 4, 14);
    private final JTextArea aMatrixB = new JTextArea("3\n0", 4, 14);
    private final JComboBox<String> aOperation = new JComboBox<>(CalculMatriciel.OPERATIONS);
    private final JTextArea aResult = new JTextArea(6, 30);

    PanneauMatrices() {
        setLayout(new BorderLayout(4, 4));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JPanel vInputs = new JPanel(new GridLayout(1, 2, 6, 0));
        vInputs.add(titled("Matrice A (une ligne par ligne)", aMatrixA));
        vInputs.add(titled("Matrice B ou second membre", aMatrixB));

        // L'exemple par défaut est un système : x + y = 3 ; 2x - y = 0
        aOperation.setSelectedItem("Résoudre A·x = B");
        JButton vCompute = new JButton("Calculer");
        vCompute.addActionListener(pEvent -> compute());
        JPanel vActions = new JPanel(new BorderLayout(4, 0));
        vActions.add(aOperation, BorderLayout.CENTER);
        vActions.add(vCompute, BorderLayout.EAST);

        JPanel vTop = new JPanel(new BorderLayout(0, 4));
        vTop.add(vInputs, BorderLayout.CENTER);
        vTop.add(vActions, BorderLayout.SOUTH);
        add(vTop, BorderLayout.NORTH);

        aResult.setEditable(false);
        aResult.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        add(titled("Résultat", aResult), BorderLayout.CENTER);
    }

    private void compute() {
        try {
            aResult.setText(CalculMatriciel.apply(
                    (String) aOperation.getSelectedItem(), aMatrixA.getText(), aMatrixB.getText()));
        } catch (RuntimeException vException) {
            aResult.setText(MoteurCalcul.ERREUR + " : " + vException.getMessage());
        }
    }

    private static JPanel titled(String pTitle, JTextArea pArea) {
        pArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        JPanel rPanel = new JPanel(new BorderLayout());
        rPanel.setBorder(BorderFactory.createTitledBorder(pTitle));
        rPanel.add(new JScrollPane(pArea), BorderLayout.CENTER);
        rPanel.add(new JLabel(" "), BorderLayout.SOUTH);
        return rPanel;
    }
}
