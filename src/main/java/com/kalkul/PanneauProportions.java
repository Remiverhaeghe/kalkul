package com.kalkul;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Section de l'onglet Outils : règle de trois, pourcentages, remises et prix au mètre carré.
 * Le calcul est fait par {@link Proportions}.
 */
final class PanneauProportions {
    private PanneauProportions() {
    }

    private static double nombre(JTextField pChamp) {
        double rNombre = FonctionsScientifiques.parseNumbers(pChamp.getText())[0];
        return rNombre;
    }

    private static String arrondi(double pValeur) {
        String rTexte = PanneauOutils.format(Math.round(pValeur * 100) / 100.0);
        return rTexte;
    }

    static JPanel construire() {
        JPanel rSection = PanneauOutils.section("Règle de trois et pourcentages");
        JLabel vResultat = new JLabel(" ");

        JTextField vA = new JTextField("3", 4);
        JTextField vB = new JTextField("7.5", 4);
        JTextField vC = new JTextField("5", 4);
        rSection.add(PanneauOutils.row(new JLabel("Si"), vA, new JLabel("donne"), vB, new JLabel(", alors"), vC,
                PanneauOutils.button("donne ?", vResultat, () -> "Si " + PanneauOutils.format(nombre(vA))
                        + " donne " + PanneauOutils.format(nombre(vB)) + ", " + PanneauOutils.format(nombre(vC))
                        + " donne " + arrondi(Proportions.regleDeTrois(nombre(vA), nombre(vB), nombre(vC))))));

        JTextField vValeur = new JTextField("80", 5);
        JTextField vAutre = new JTextField("20", 5);
        rSection.add(PanneauOutils.row(new JLabel("Valeur"), vValeur, new JLabel("et"), vAutre,
                PanneauOutils.button("% de", vResultat, () -> PanneauOutils.format(nombre(vAutre)) + " % de "
                        + PanneauOutils.format(nombre(vValeur)) + " = "
                        + arrondi(Proportions.pourcentageDe(nombre(vAutre), nombre(vValeur)))),
                PanneauOutils.button("Part", vResultat, () -> PanneauOutils.format(nombre(vAutre)) + " sur "
                        + PanneauOutils.format(nombre(vValeur)) + " = "
                        + arrondi(Proportions.part(nombre(vAutre), nombre(vValeur))) + " %"),
                PanneauOutils.button("Variation", vResultat, () -> "De " + PanneauOutils.format(nombre(vValeur))
                        + " à " + PanneauOutils.format(nombre(vAutre)) + " : "
                        + arrondi(Proportions.variation(nombre(vValeur), nombre(vAutre))) + " %")));

        JTextField vPrix = new JTextField("120", 5);
        JTextField vRemise = new JTextField("20", 3);
        JTextField vRemise2 = new JTextField("", 3);
        rSection.add(PanneauOutils.row(new JLabel("Prix"), vPrix, new JLabel("remise"), vRemise,
                new JLabel("% puis"), vRemise2, new JLabel("%"),
                PanneauOutils.button("Après remise", vResultat, () -> {
                    double vPrixFinal = vRemise2.getText().isBlank()
                            ? Proportions.apresRemises(nombre(vPrix), nombre(vRemise))
                            : Proportions.apresRemises(nombre(vPrix), nombre(vRemise), nombre(vRemise2));
                    return "Prix remisé " + arrondi(vPrixFinal) + ", soit "
                            + arrondi(Proportions.variation(nombre(vPrix), vPrixFinal)) + " % au total";
                }),
                PanneauOutils.button("Avant remise", vResultat, () -> "Prix avant " + arrondi(
                        Proportions.avantRemise(nombre(vPrix), nombre(vRemise))))));

        JTextField vSurface = new JTextField("2.22", 4);
        rSection.add(PanneauOutils.row(new JLabel("Prix ci-dessus pour"), vSurface, new JLabel("m²"),
                PanneauOutils.button("Prix au m²", vResultat, () -> arrondi(Proportions.prixAuM2(nombre(vPrix),
                        nombre(vSurface))) + " le m²")));
        rSection.add(vResultat);
        return rSection;
    }
}
