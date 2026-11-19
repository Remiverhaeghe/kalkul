package com.kalkul;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Sous-onglet Toiture : pente en %, degrés et cm par mètre, rampant et pente minimale indicative.
 * Le calcul est fait par {@link Toiture}.
 */
class PanneauToiture extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JTextField aProjection = new JTextField("4", 4);
    private final JComboBox<Toiture.Couverture> aCouverture = new JComboBox<>(Toiture.Couverture.values());

    PanneauToiture() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel vSection = PanneauOutils.section("Pente de toiture");
        JTextField vPourcent = new JTextField("45", 4);
        JTextField vDegres = new JTextField("", 4);
        JTextField vMontee = new JTextField("", 4);
        JTextField vSur = new JTextField("", 4);
        JLabel vResultat = new JLabel(" ");
        JLabel vAvis = new JLabel(" ");
        vSection.add(PanneauOutils.row(new JLabel("Couverture"), aCouverture, new JLabel("projection horizontale"),
                aProjection, new JLabel("m")));
        vSection.add(PanneauOutils.row(new JLabel("Pente"), vPourcent, new JLabel("% ou cm/m"),
                PanneauOutils.button("Depuis %", vResultat,
                        () -> decrire(Toiture.depuisPourcent(PanneauChantier.number(vPourcent)), vAvis)),
                new JLabel("   ou"), vDegres, new JLabel("°"),
                PanneauOutils.button("Depuis degrés", vResultat,
                        () -> decrire(Toiture.depuisDegres(PanneauChantier.number(vDegres)), vAvis))));
        vSection.add(PanneauOutils.row(new JLabel("ou montée de"), vMontee, new JLabel("cm sur"), vSur,
                new JLabel("m à l'horizontale"), PanneauOutils.button("Depuis la montée", vResultat,
                        () -> decrire(Toiture.depuisMontee(PanneauChantier.number(vMontee),
                                PanneauChantier.number(vSur)), vAvis))));
        vSection.add(vResultat);
        vSection.add(vAvis);
        vSection.add(new JLabel("Minimums indicatifs : ils varient avec le modèle, la région et la longueur "
                + "du rampant. À vérifier auprès du fabricant."));
        add(vSection);
    }

    private String decrire(Toiture.Pente pPente, JLabel pAvis) {
        Toiture.Couverture vCouverture = (Toiture.Couverture) aCouverture.getSelectedItem();
        boolean vSuffit = Toiture.suffit(pPente, vCouverture);
        pAvis.setText(vCouverture + " : minimum indicatif " + PanneauOutils.format(vCouverture.minimumPourcent())
                + " %, " + (vSuffit ? "la pente convient" : "pente trop faible"));
        String rTexte = PanneauOutils.format(Math.round(pPente.pourcent() * 10) / 10.0) + " %, soit "
                + PanneauOutils.format(Math.round(pPente.degres() * 10) / 10.0) + "° ou "
                + PanneauOutils.format(Math.round(pPente.cmParMetre() * 10) / 10.0) + " cm par mètre ; rampant de "
                + PanneauOutils.format(Math.round(Toiture.rampant(PanneauChantier.number(aProjection), pPente) * 100)
                        / 100.0) + " m";
        return rTexte;
    }
}
