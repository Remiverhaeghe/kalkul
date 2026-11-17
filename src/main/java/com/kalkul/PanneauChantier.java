package com.kalkul;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Onglet Chantier : revêtement en paquets, béton d'une dalle et pente.
 * Le calcul est fait par {@link Chantier}.
 */
class PanneauChantier extends JPanel {
    private static final long serialVersionUID = 1L;

    PanneauChantier() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(buildPacks());
        add(buildConcrete());
        add(buildSlope());
    }

    private static double number(JTextField pField) {
        return FonctionsScientifiques.parseNumbers(pField.getText())[0];
    }

    private JPanel buildPacks() {
        JPanel vSection = PanneauOutils.section("Revêtement : parquet, carrelage, isolant en paquets");
        JTextField vLength = new JTextField("5", 5);
        JTextField vWidth = new JTextField("4", 5);
        JTextField vMargin = new JTextField("10", 4);
        JTextField vPack = new JTextField("2.22", 5);
        JLabel vResult = new JLabel(" ");
        vSection.add(PanneauOutils.row(new JLabel("Pièce"), vLength, new JLabel("m ×"), vWidth, new JLabel("m")));
        vSection.add(PanneauOutils.row(new JLabel("Chutes"), vMargin, new JLabel("%   Un paquet couvre"), vPack,
                new JLabel("m²"), PanneauOutils.button("Calculer", vResult, () -> {
                    double vSurface = number(vLength) * number(vWidth);
                    int vPacks = Chantier.paquets(vSurface, number(vMargin), number(vPack));
                    return PanneauOutils.format(vSurface) + " m² : " + vPacks + " paquet(s), soit "
                            + PanneauOutils.format(vPacks * number(vPack)) + " m²";
                })));
        vSection.add(vResult);
        return vSection;
    }

    private JPanel buildConcrete() {
        JPanel vSection = PanneauOutils.section("Béton d'une dalle, dosé à 350 kg/m³");
        JTextField vLength = new JTextField("4", 5);
        JTextField vWidth = new JTextField("3", 5);
        JTextField vThickness = new JTextField("12", 4);
        JLabel vResult = new JLabel(" ");
        vSection.add(PanneauOutils.row(new JLabel("Dalle"), vLength, new JLabel("m ×"), vWidth,
                new JLabel("m, épaisseur"), vThickness, new JLabel("cm"),
                PanneauOutils.button("Calculer", vResult, () -> {
                    Chantier.Beton vBeton = Chantier.beton(number(vLength), number(vWidth), number(vThickness));
                    return PanneauOutils.format(Math.round(vBeton.volumeM3() * 100) / 100.0) + " m³ : "
                            + vBeton.sacsCiment() + " sacs de ciment de 35 kg, "
                            + Math.round(vBeton.sableKg()) + " kg de sable, " + Math.round(vBeton.gravierKg())
                            + " kg de gravillons, " + Math.round(vBeton.eauL()) + " l d'eau";
                })));
        vSection.add(vResult);
        return vSection;
    }

    private JPanel buildSlope() {
        JPanel vSection = PanneauOutils.section("Pente : évacuation, terrasse, allée");
        JTextField vLength = new JTextField("5", 5);
        JTextField vSlope = new JTextField("2", 4);
        JTextField vDrop = new JTextField("", 4);
        JLabel vResult = new JLabel(" ");
        vSection.add(PanneauOutils.row(new JLabel("Sur"), vLength, new JLabel("m, pente"), vSlope, new JLabel("%"),
                PanneauOutils.button("Dénivelé", vResult, () -> "Dénivelé de "
                        + PanneauOutils.format(Chantier.deniveleCm(number(vLength), number(vSlope))) + " cm")));
        vSection.add(PanneauOutils.row(new JLabel("ou dénivelé de"), vDrop, new JLabel("cm"),
                PanneauOutils.button("Pente", vResult, () -> {
                    double vPercent = Chantier.pentePourcent(number(vDrop), number(vLength));
                    return "Pente de " + PanneauOutils.format(vPercent) + " %, soit "
                            + PanneauOutils.format(Conversions.convert(vPercent, "% de pente", "° de pente")) + "°";
                })));
        vSection.add(vResult);
        return vSection;
    }
}
