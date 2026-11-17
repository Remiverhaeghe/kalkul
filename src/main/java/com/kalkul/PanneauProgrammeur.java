package com.kalkul;

import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Onglet Programmeur : entiers en hexadécimal, décimal, octal et binaire, opérations bit à bit et
 * décalages. Le calcul est fait par {@link Programmeur}.
 */
class PanneauProgrammeur extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final String[] NOMS_BASES = {"Hexadécimal", "Décimal", "Octal", "Binaire"};
    private static final String[] OPERATIONS = {"AND", "OR", "XOR", "NOT", "<<", ">>", "+", "-", "*", "/", "mod"};

    private final JComboBox<String> aBase = new JComboBox<>(NOMS_BASES);
    private final JComboBox<String> aSize = new JComboBox<>(new String[] {"8 bits", "16 bits", "32 bits", "64 bits"});
    private final JTextField aLeft = new JTextField("FF", 20);
    private final JTextField aRight = new JTextField("0F", 20);
    private final JLabel[] aValues = new JLabel[Programmeur.BASES.length];
    private final JLabel aBits = new JLabel(" ");
    private final JLabel aMessage = new JLabel(" ");
    private long aResult;

    PanneauProgrammeur() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        aSize.setSelectedIndex(2);

        JPanel vInputs = PanneauOutils.section("Nombres");
        vInputs.add(PanneauOutils.row(new JLabel("Base"), aBase, new JLabel("Taille"), aSize));
        vInputs.add(PanneauOutils.row(new JLabel("A"), aLeft));
        vInputs.add(PanneauOutils.row(new JLabel("B"), aRight));
        add(vInputs);

        JPanel vOperations = PanneauOutils.section("Opérations (NOT ne prend que A)");
        JPanel vButtons = new JPanel(new GridLayout(2, 6, 4, 4));
        for (String vOperation : OPERATIONS) {
            JButton vButton = new JButton(Habillage.signe(vOperation));
            vButton.setFocusable(false);
            vButton.addActionListener(pEvent -> operate(vOperation));
            vButtons.add(vButton);
        }
        JButton vReuse = new JButton("Résultat → A");
        vReuse.setFocusable(false);
        vReuse.addActionListener(pEvent -> aLeft.setText(Programmeur.ecrire(aResult, base(), bits())));
        vButtons.add(vReuse);
        vOperations.add(vButtons);
        vOperations.add(aMessage);
        add(vOperations);

        JPanel vResult = PanneauOutils.section("Résultat");
        for (int vIndex = 0; vIndex < aValues.length; vIndex++) {
            aValues[vIndex] = new JLabel(" ");
            aValues[vIndex].setFont(Habillage.chiffres(Font.PLAIN, 15));
            JLabel vName = new JLabel(abbreviation(vIndex));
            vName.setFont(Habillage.chiffres(Font.BOLD, 13));
            vName.setForeground(Habillage.TEXTE_DOUX);
            vResult.add(PanneauOutils.row(vName, aValues[vIndex]));
        }
        vResult.add(aBits);
        add(vResult);

        // A tapé seul s'affiche dans toutes les bases ; changer de base réécrit A et B dans la nouvelle
        aLeft.getDocument().addDocumentListener(new Ecoute(this::showLeft));
        aBase.addActionListener(new java.awt.event.ActionListener() {
            private int aPrevious = aBase.getSelectedIndex();

            @Override
            public void actionPerformed(java.awt.event.ActionEvent pEvent) {
                int vOld = Programmeur.BASES[aPrevious];
                aPrevious = aBase.getSelectedIndex();
                convert(aLeft, vOld);
                convert(aRight, vOld);
            }
        });
        aSize.addActionListener(pEvent -> showLeft());
        showLeft();
    }

    private static String abbreviation(int pIndex) {
        return new String[] {"HEX", "DEC", "OCT", "BIN"}[pIndex];
    }

    private int base() {
        return Programmeur.BASES[aBase.getSelectedIndex()];
    }

    private int bits() {
        return Programmeur.TAILLES[aSize.getSelectedIndex()];
    }

    private void convert(JTextField pField, int pOldBase) {
        try {
            pField.setText(Programmeur.ecrire(Programmeur.lire(pField.getText(), pOldBase, bits()), base(), bits()));
        } catch (RuntimeException vException) {
            pField.setText("");
        }
    }

    private void showLeft() {
        try {
            show(Programmeur.lire(aLeft.getText(), base(), bits()));
            aMessage.setText(" ");
        } catch (RuntimeException vException) {
            aMessage.setText(MoteurCalcul.ERREUR + " : " + vException.getMessage());
        }
    }

    private void operate(String pOperation) {
        try {
            long vLeft = Programmeur.lire(aLeft.getText(), base(), bits());
            long vResult = "NOT".equals(pOperation) ? Programmeur.non(vLeft, bits())
                    : Programmeur.calculer(vLeft, Programmeur.lire(aRight.getText(), base(), bits()), pOperation,
                            bits());
            show(vResult);
            aMessage.setText(" ");
        } catch (RuntimeException vException) {
            aMessage.setText(MoteurCalcul.ERREUR + " : " + vException.getMessage());
        }
    }

    private void show(long pValue) {
        aResult = pValue;
        for (int vIndex = 0; vIndex < aValues.length; vIndex++) {
            aValues[vIndex].setText(Programmeur.ecrire(pValue, Programmeur.BASES[vIndex], bits()));
        }
        aBits.setText(Programmeur.bitsAUn(pValue, bits()) + " bit(s) à 1 sur " + bits());
    }

    /** Relance l'affichage à chaque frappe. */
    private static final class Ecoute implements DocumentListener {
        private final Runnable aAction;

        Ecoute(Runnable pAction) {
            aAction = pAction;
        }

        @Override
        public void insertUpdate(DocumentEvent pEvent) {
            aAction.run();
        }

        @Override
        public void removeUpdate(DocumentEvent pEvent) {
            aAction.run();
        }

        @Override
        public void changedUpdate(DocumentEvent pEvent) {
            aAction.run();
        }
    }
}
