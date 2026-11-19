package com.kalkul;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Sous-onglet Coupes : onglet de plinthe, coupe composée d'une pièce inclinée et coupes d'un chevron.
 * Le calcul est fait par {@link Coupes}.
 */
class PanneauCoupes extends JPanel {
    private static final long serialVersionUID = 1L;
    // Au-delà, la plupart des scies à onglet ne suivent plus : la coupe se trace et se fait à la main
    private static final double LAME_MAX_DEG = 48;

    PanneauCoupes() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(construireOnglet());
        add(construireComposee());
        add(construireChevron());
    }

    private static String angle(double pDegres) {
        String rTexte = PanneauOutils.format(Math.round(pDegres * 10) / 10.0) + "°";
        return rTexte;
    }

    private static String avertir(double pDegres) {
        String rTexte = pDegres > LAME_MAX_DEG ? " (au-delà de ce que penchent la plupart des scies)" : "";
        return rTexte;
    }

    private JPanel construireOnglet() {
        JPanel vSection = PanneauOutils.section("Onglet : plinthe debout contre le guide, cadre");
        JTextField vMur = new JTextField("90", 4);
        JLabel vResultat = new JLabel(" ");
        vSection.add(PanneauOutils.row(new JLabel("Angle du mur"), vMur, new JLabel("°"),
                PanneauOutils.button("Calculer", vResultat, () -> "Coupe à "
                        + angle(Coupes.onglet(PanneauChantier.number(vMur)))
                        + " sur chaque pièce, lame droite (angle rentrant ou sortant)")));
        vSection.add(vResultat);
        return vSection;
    }

    private JPanel construireComposee() {
        JPanel vSection = PanneauOutils.section("Coupe composée : corniche, pièce inclinée, pièce posée à plat");
        JTextField vMur = new JTextField("90", 4);
        JTextField vInclinaison = new JTextField("38", 4);
        JLabel vResultat = new JLabel(" ");
        vSection.add(PanneauOutils.row(new JLabel("Angle du mur"), vMur, new JLabel("°, pièce inclinée de"),
                vInclinaison, new JLabel("° sur la verticale"),
                PanneauOutils.button("Calculer", vResultat, () -> {
                    Coupes.Reglage vReglage = Coupes.composee(PanneauChantier.number(vMur),
                            PanneauChantier.number(vInclinaison));
                    return "Table à " + angle(vReglage.coupeDeg()) + ", lame inclinée à "
                            + angle(vReglage.lameDeg()) + avertir(vReglage.lameDeg());
                })));
        vSection.add(new JLabel("0° : pièce debout comme une plinthe ; 90° : pièce à plat comme un cadre."));
        vSection.add(vResultat);
        return vSection;
    }

    private JPanel construireChevron() {
        JPanel vSection = PanneauOutils.section("Chevron contre un arêtier");
        JTextField vPente = new JTextField("35", 4);
        JTextField vPlan = new JTextField("45", 4);
        JLabel vTrace = new JLabel(" ");
        JLabel vScie = new JLabel(" ");
        vSection.add(PanneauOutils.row(new JLabel("Pente du toit"), vPente, new JLabel("°, angle en plan"), vPlan,
                new JLabel("°"), PanneauOutils.button("Calculer", vTrace, () -> {
                    Coupes.Chevron vChevron = Coupes.chevron(PanneauChantier.number(vPente),
                            PanneauChantier.number(vPlan));
                    vScie.setText("Scie, chevron couché sur sa joue : table à "
                            + angle(vChevron.reglageArretier().coupeDeg()) + ", lame à "
                            + angle(vChevron.reglageArretier().lameDeg())
                            + avertir(vChevron.reglageArretier().lameDeg()));
                    return "À tracer depuis l'équerre : aplomb " + angle(vChevron.aplombDeg()) + ", pied "
                            + angle(vChevron.piedDeg()) + ", joue sur le dessus " + angle(vChevron.joueDessusDeg());
                })));
        vSection.add(vTrace);
        vSection.add(vScie);
        return vSection;
    }
}
