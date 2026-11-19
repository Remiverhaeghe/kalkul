package com.kalkul;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Sous-onglet Terrassement et mélanges : fouille, remblai en big bags, gâchée de béton, mortier ou enduit.
 * Le calcul est fait par {@link Terrassement} et {@link Melanges}.
 */
class PanneauTerrassement extends JPanel {
    private static final long serialVersionUID = 1L;

    PanneauTerrassement() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(construireFouille());
        add(construireApport());
        add(construireGachee());
    }

    private static String arrondi(double pValeur) {
        String rTexte = PanneauOutils.format(Math.round(pValeur * 100) / 100.0);
        return rTexte;
    }

    private JPanel construireFouille() {
        JPanel vSection = PanneauOutils.section("Fouille : volume à creuser et à évacuer");
        JTextField vLongueur = new JTextField("4", 4);
        JTextField vLargeur = new JTextField("3", 4);
        JTextField vProfondeur = new JTextField("30", 4);
        JTextField vFoisonnement = new JTextField(PanneauOutils.format(Terrassement.FOISONNEMENT_POURCENT), 3);
        JTextField vBigBag = new JTextField("1", 3);
        JLabel vResultat = new JLabel(" ");
        vSection.add(PanneauOutils.row(new JLabel("Fouille"), vLongueur, new JLabel("m ×"), vLargeur,
                new JLabel("m sur"), vProfondeur, new JLabel("cm")));
        vSection.add(PanneauOutils.row(new JLabel("Foisonnement"), vFoisonnement, new JLabel("%, big bag de"), vBigBag,
                new JLabel("m³"), PanneauOutils.button("Calculer", vResultat, () -> {
                    double vEnPlace = Terrassement.volume(PanneauChantier.number(vLongueur),
                            PanneauChantier.number(vLargeur), PanneauChantier.number(vProfondeur));
                    double vSorti = Terrassement.foisonne(vEnPlace, PanneauChantier.number(vFoisonnement));
                    return arrondi(vEnPlace) + " m³ en place, " + arrondi(vSorti) + " m³ une fois creusés : "
                            + Terrassement.bigBags(vSorti, PanneauChantier.number(vBigBag)) + " big bag(s)";
                })));
        vSection.add(vResultat);
        return vSection;
    }

    private JPanel construireApport() {
        JPanel vSection = PanneauOutils.section("Remblai : sable ou gravier en big bags");
        JTextField vLongueur = new JTextField("4", 4);
        JTextField vLargeur = new JTextField("3", 4);
        JTextField vEpaisseur = new JTextField("15", 4);
        JComboBox<Terrassement.Materiau> vMateriau = new JComboBox<>(Terrassement.Materiau.values());
        JTextField vMarge = new JTextField("10", 3);
        JTextField vBigBag = new JTextField("1", 3);
        JLabel vResultat = new JLabel(" ");
        vSection.add(PanneauOutils.row(new JLabel("Lit de"), vLongueur, new JLabel("m ×"), vLargeur,
                new JLabel("m sur"), vEpaisseur, new JLabel("cm"), vMateriau));
        vSection.add(PanneauOutils.row(new JLabel("Tassement et pertes"), vMarge, new JLabel("%, big bag de"), vBigBag,
                new JLabel("t"), PanneauOutils.button("Calculer", vResultat, () -> {
                    Terrassement.Materiau vChoix = (Terrassement.Materiau) vMateriau.getSelectedItem();
                    Terrassement.Apport vApport = Terrassement.apport(
                            Terrassement.volume(PanneauChantier.number(vLongueur), PanneauChantier.number(vLargeur),
                                    PanneauChantier.number(vEpaisseur)),
                            vChoix, PanneauChantier.number(vMarge), PanneauChantier.number(vBigBag));
                    return arrondi(vApport.volumeM3()) + " m³, environ " + arrondi(vApport.masseT()) + " t à "
                            + PanneauOutils.format(vChoix.densite()) + " t/m³ (indicatif) : "
                            + vApport.bigBags() + " big bag(s)";
                })));
        vSection.add(vResultat);
        return vSection;
    }

    private JPanel construireGachee() {
        JPanel vSection = PanneauOutils.section("Gâchée : béton, mortier, enduit");
        JComboBox<Melanges.Type> vType = new JComboBox<>(Melanges.Type.values());
        JTextField vVolume = new JTextField("250", 4);
        JTextField vDosage = new JTextField(PanneauOutils.format(Melanges.Type.BETON.dosageKg()), 4);
        JTextField vSac = new JTextField("35", 3);
        JTextField vSeau = new JTextField("10", 3);
        JLabel vTotal = new JLabel(" ");
        JLabel vParSac = new JLabel(" ");
        // Le dosage courant suit le mélange choisi, l'utilisateur peut ensuite le changer
        vType.addActionListener(pEvent -> vDosage.setText(
                PanneauOutils.format(((Melanges.Type) vType.getSelectedItem()).dosageKg())));
        vSection.add(PanneauOutils.row(vType, vVolume, new JLabel("l, dosé à"), vDosage, new JLabel("kg/m³")));
        vSection.add(PanneauOutils.row(new JLabel("Sac de"), vSac, new JLabel("kg, seau de"), vSeau, new JLabel("l"),
                PanneauOutils.button("Calculer", vTotal, () -> {
                    Melanges.Gachee vGachee = Melanges.gachee((Melanges.Type) vType.getSelectedItem(),
                            PanneauChantier.number(vVolume), PanneauChantier.number(vDosage),
                            PanneauChantier.number(vSac), PanneauChantier.number(vSeau));
                    vParSac.setText("Par sac : " + arrondi(vGachee.sableSeauxParSac()) + " seau(x) de sable, "
                            + arrondi(vGachee.gravierSeauxParSac()) + " de gravier, environ "
                            + arrondi(vGachee.eauLParSac()) + " l d'eau");
                    return arrondi(vGachee.cimentKg()) + " kg de ciment (" + vGachee.sacs() + " sac(s)), "
                            + arrondi(vGachee.sableSeaux()) + " seau(x) de sable, " + arrondi(vGachee.gravierSeaux())
                            + " de gravier, environ " + arrondi(vGachee.eauL()) + " l d'eau";
                })));
        vSection.add(vTotal);
        vSection.add(vParSac);
        vSection.add(new JLabel("Proportions courantes, indicatives : l'eau s'ajuste à la consistance."));
        return vSection;
    }
}
