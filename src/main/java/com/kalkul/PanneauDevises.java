package com.kalkul;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Sous-onglet Devises : conversion avec des taux gardés sur l'ordinateur, tapés à la main ou importés
 * d'un fichier. Aucune connexion : la date des taux est toujours affichée, avec une alerte quand ils
 * datent de plus d'une semaine.
 */
final class PanneauDevises {

    private static final DateTimeFormatter JOUR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private PanneauDevises() {
    }

    static JPanel construire(Devises pDevises) {
        JPanel rPage = new JPanel();
        rPage.setLayout(new BoxLayout(rPage, BoxLayout.Y_AXIS));

        JPanel vConversion = PanneauOutils.section("Convertir");
        JTextField vMontant = new JTextField(8);
        JComboBox<String> vDe = new JComboBox<>();
        JComboBox<String> vVers = new JComboBox<>();
        JLabel vResultat = new JLabel(" ");
        JLabel vEtat = new JLabel(" ");
        Runnable vRafraichir = () -> {
            remplir(vDe, pDevises);
            remplir(vVers, pDevises);
            vEtat.setText(etat(pDevises, LocalDate.now()));
        };
        vConversion.add(PanneauOutils.row(vMontant, vDe, new JLabel("vers"), vVers,
                PanneauOutils.button("Convertir", vResultat, () -> {
                    double vNombre = FonctionsScientifiques.parseNumbers(vMontant.getText())[0];
                    String vSource = (String) vDe.getSelectedItem();
                    String vCible = (String) vVers.getSelectedItem();
                    return PanneauOutils.format(vNombre) + " " + vSource + " = "
                            + PanneauOutils.format(pDevises.convertir(vNombre, vSource, vCible)) + " " + vCible;
                })));
        vConversion.add(vResultat);
        vConversion.add(vEtat);

        JPanel vSaisie = PanneauOutils.section("Taper un taux (pour 1 EUR)");
        JTextField vCode = new JTextField("USD", 4);
        JTextField vTaux = new JTextField(8);
        JLabel vSaisieEtat = new JLabel(" ");
        vSaisie.add(PanneauOutils.row(new JLabel("1 EUR ="), vTaux, vCode,
                PanneauOutils.button("Enregistrer", vSaisieEtat, () -> {
                    pDevises.saisir(vCode.getText(), FonctionsScientifiques.parseNumbers(vTaux.getText())[0],
                            LocalDate.now());
                    vRafraichir.run();
                    return "Taux enregistré, daté d'aujourd'hui";
                })));
        vSaisie.add(vSaisieEtat);

        JPanel vImport = PanneauOutils.section("Importer un fichier de taux");
        JLabel vImportEtat = new JLabel("Fichier eurofxref.csv de la BCE, ou des lignes « USD;1,08 ».");
        JButton vChoisir = new JButton("Choisir le fichier…");
        vChoisir.addActionListener(pEvent -> {
            JFileChooser vChooser = new JFileChooser();
            if (vChooser.showOpenDialog(rPage) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            try {
                String vTexte = Files.readString(vChooser.getSelectedFile().toPath(), StandardCharsets.UTF_8);
                int vNombre = pDevises.importer(vTexte, LocalDate.now());
                vImportEtat.setText(vNombre + " devises importées");
                vRafraichir.run();
            } catch (IOException | IllegalArgumentException vException) {
                vImportEtat.setText(MoteurCalcul.ERREUR + " : " + vException.getMessage());
            }
        });
        vImport.add(PanneauOutils.row(vChoisir));
        vImport.add(vImportEtat);

        rPage.add(vConversion);
        rPage.add(vSaisie);
        rPage.add(vImport);
        vRafraichir.run();
        return rPage;
    }

    /** Date des taux, et l'alerte quand ils sont anciens ou absents. */
    static String etat(Devises pDevises, LocalDate pAujourdhui) {
        String rEtat;
        if (pDevises.date() == null) {
            rEtat = "Aucun taux : tapez-en un ou importez un fichier.";
        } else if (pDevises.anciens(pAujourdhui)) {
            rEtat = "Taux du " + JOUR.format(pDevises.date()) + ", plus d'une semaine : à mettre à jour.";
        } else {
            rEtat = "Taux du " + JOUR.format(pDevises.date()) + ".";
        }
        return rEtat;
    }

    private static void remplir(JComboBox<String> pChoix, Devises pDevises) {
        Object vAvant = pChoix.getSelectedItem();
        pChoix.removeAllItems();
        pDevises.codes().forEach(pChoix::addItem);
        if (vAvant != null) {
            pChoix.setSelectedItem(vAvant);
        }
    }
}
