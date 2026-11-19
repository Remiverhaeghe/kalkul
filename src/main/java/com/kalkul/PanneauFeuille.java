package com.kalkul;

import java.awt.BorderLayout;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

/**
 * Onglet Feuille : cellules nommées et formule enregistrée calculée avec elles.
 * Le calcul est fait par {@link FeuilleCalcul} et {@link CarnetFormules}.
 */
class PanneauFeuille extends JPanel {
    private static final long serialVersionUID = 1L;

    private final transient FeuilleCalcul aFeuille;
    private final DefaultListModel<String> aLignes = new DefaultListModel<>();
    private final JList<String> aListe = new JList<>(aLignes);
    private final JTextField aNom = new JTextField("surface", 8);
    private final JTextField aFormule = new JTextField("4.2*3.5", 14);
    private final JLabel aResultat = new JLabel(" ");

    PanneauFeuille(FeuilleCalcul pFeuille, CarnetFormules pCarnet) {
        super(new BorderLayout(4, 4));
        aFeuille = pFeuille;
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JButton vDefinir = new JButton("Définir");
        vDefinir.addActionListener(pEvent -> definir());
        JButton vRetirer = new JButton("Retirer");
        vRetirer.addActionListener(pEvent -> {
            int vIndex = aListe.getSelectedIndex();
            if (vIndex >= 0) {
                aFeuille.supprimer(aFeuille.liste().get(vIndex).nom());
            }
        });
        aListe.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Choisir une cellule la remet dans les champs pour la corriger
        aListe.addListSelectionListener(pEvent -> {
            int vIndex = aListe.getSelectedIndex();
            if (!pEvent.getValueIsAdjusting() && vIndex >= 0 && vIndex < aFeuille.liste().size()) {
                aNom.setText(aFeuille.liste().get(vIndex).nom());
                aFormule.setText(aFeuille.liste().get(vIndex).formule());
            }
        });

        JPanel vHaut = PanneauOutils.section("Cellules nommées (" + FeuilleCalcul.MAX + " au plus)");
        vHaut.add(PanneauOutils.row(aNom, new JLabel("="), aFormule, vDefinir, vRetirer));
        vHaut.add(new JLabel("Une cellule peut reprendre les autres (prix = surface*24.9) et sert de variable "
                + "aux formules enregistrées."));
        add(vHaut, BorderLayout.NORTH);
        add(new JScrollPane(aListe), BorderLayout.CENTER);
        add(construireApplication(pCarnet), BorderLayout.SOUTH);

        aFeuille.ecouter(this::rafraichir);
        rafraichir();
    }

    private JPanel construireApplication(CarnetFormules pCarnet) {
        JPanel rSection = PanneauOutils.section("Formule enregistrée calculée avec les cellules");
        ChoixFormule vChoix = new ChoixFormule(pCarnet);
        JTextField vX = new JTextField("0", 5);
        rSection.add(PanneauOutils.row(new JLabel("f(x) ="), vChoix, new JLabel("pour x ="), vX,
                PanneauOutils.button("Calculer", aResultat, () -> {
                    CarnetFormules.Formule vFormule = vChoix.choisie();
                    if (vFormule == null) {
                        return "Enregistre une formule dans l'onglet Graphique pour la calculer ici.";
                    }
                    double vValeurX = FonctionsScientifiques.parseNumbers(vX.getText())[0];
                    Map<String, Double> vCellules = aFeuille.valeurs();
                    return CarnetFormules.ecrire(vFormule, vValeurX, vCellules) + " = "
                            + Affichage.format(CarnetFormules.appliquer(vFormule, vValeurX, vCellules));
                })));
        rSection.add(aResultat);
        return rSection;
    }

    private void definir() {
        try {
            aFeuille.definir(aNom.getText(), aFormule.getText());
            aResultat.setText(" ");
        } catch (RuntimeException vException) {
            aResultat.setText(MoteurCalcul.ERREUR + " : " + vException.getMessage());
        }
    }

    private void rafraichir() {
        aLignes.clear();
        Map<String, Double> vValeurs = aFeuille.valeurs();
        for (FeuilleCalcul.Cellule vCellule : aFeuille.liste()) {
            Double vValeur = vValeurs.get(vCellule.nom());
            String vTexte = vValeur == null ? MoteurCalcul.ERREUR : Affichage.format(vValeur);
            aLignes.addElement(vCellule + "   →   " + vTexte);
        }
    }
}
