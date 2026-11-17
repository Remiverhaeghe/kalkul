package com.kalkul;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 * Liste déroulante des formules enregistrées, tenue à jour quand un autre onglet en enregistre une.
 */
class ChoixFormule extends JComboBox<CarnetFormules.Formule> {
    private static final long serialVersionUID = 1L;

    private final transient CarnetFormules aCarnet;
    // Vrai pendant le remplissage : la sélection qui change alors ne vient pas de l'utilisateur
    private boolean aFilling;

    ChoixFormule(CarnetFormules pCarnet) {
        aCarnet = pCarnet;
        setPrototypeDisplayValue(new CarnetFormules.Formule("a*x^2 + b*x + c", "a=1; b=0"));
        fill();
        pCarnet.ecouter(this::fill);
    }

    private void fill() {
        aFilling = true;
        Object vSelected = getSelectedItem();
        DefaultComboBoxModel<CarnetFormules.Formule> vModel = new DefaultComboBoxModel<>();
        aCarnet.liste().forEach(vModel::addElement);
        setModel(vModel);
        if (vSelected != null && aCarnet.liste().contains(vSelected)) {
            setSelectedItem(vSelected);
        }
        aFilling = false;
    }

    boolean enRemplissage() {
        return aFilling;
    }

    CarnetFormules.Formule choisie() {
        return (CarnetFormules.Formule) getSelectedItem();
    }
}
