package com.kalkul;

import java.awt.Component;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Sous-onglet Mémoires : garder un résultat sous un nom, le retrouver dans les formules, l'oublier.
 */
final class PanneauMemoires {

    private PanneauMemoires() {
    }

    static JPanel construire(Memoires pMemoires) {
        JPanel rPage = new JPanel();
        rPage.setLayout(new BoxLayout(rPage, BoxLayout.Y_AXIS));

        JLabel vListe = new JLabel();
        Runnable vRafraichir = () -> vListe.setText(resume(pMemoires));

        JPanel vSaisie = PanneauOutils.section("Garder une valeur sous un nom");
        JTextField vNom = new JTextField("prix", 8);
        JTextField vFormule = new JTextField(14);
        JLabel vEtat = new JLabel("Le calcul peut citer d'autres mémoires : prix * 3.");
        vSaisie.add(PanneauOutils.row(vNom, new JLabel("="), vFormule,
                PanneauOutils.button("Enregistrer", vEtat, () -> {
                    double vValeur = pMemoires.enregistrer(vNom.getText(), vFormule.getText());
                    vRafraichir.run();
                    return vNom.getText().strip().toLowerCase() + " = " + PanneauOutils.format(vValeur);
                })));
        vSaisie.add(vEtat);

        JPanel vOubli = PanneauOutils.section("Oublier une mémoire");
        JTextField vNomOubli = new JTextField(8);
        JLabel vOubliEtat = new JLabel(" ");
        vOubli.add(PanneauOutils.row(vNomOubli, PanneauOutils.button("Supprimer", vOubliEtat, () -> {
            boolean vFait = pMemoires.supprimer(vNomOubli.getText());
            vRafraichir.run();
            return vFait ? "Mémoire supprimée" : "Aucune mémoire de ce nom";
        })));
        vOubli.add(vOubliEtat);

        JPanel vGardees = PanneauOutils.section("Mémoires gardées");
        vListe.setAlignmentX(Component.LEFT_ALIGNMENT);
        vGardees.add(vListe);

        rPage.add(vSaisie);
        rPage.add(vOubli);
        rPage.add(vGardees);
        vRafraichir.run();
        return rPage;
    }

    /** Les mémoires sur une ligne, ou l'invitation à en créer une. */
    static String resume(Memoires pMemoires) {
        if (pMemoires.noms().isEmpty()) {
            return "Aucune mémoire : enregistrez-en une ci-dessus.";
        }
        StringBuilder rResume = new StringBuilder();
        for (String vNom : pMemoires.noms()) {
            if (rResume.length() > 0) {
                rResume.append(" · ");
            }
            rResume.append(vNom).append(" = ").append(PanneauOutils.format(pMemoires.valeur(vNom)));
        }
        return rResume.toString();
    }
}
