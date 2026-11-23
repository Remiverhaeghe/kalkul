package com.kalkul;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Boutons des formulaires, à plat : fond comme une touche de chiffre, coins arrondis, plus clair au
 * survol, plus sombre à l'appui. Les touches rondes du pavé se dessinent elles-mêmes et ne sont pas
 * concernées. Public parce que Swing le crée par son nom ({@code ButtonUI}).
 */
public final class BoutonPlat extends BasicButtonUI {
    private static final int ARRONDI = 10;

    /**
     * Fabrique appelée par Swing pour chaque bouton.
     */
    public static ComponentUI createUI(JComponent pComposant) {
        return new BoutonPlat();
    }

    @Override
    protected void installDefaults(AbstractButton pBouton) {
        super.installDefaults(pBouton);
        pBouton.setRolloverEnabled(true);
        pBouton.setOpaque(false);
        if (pBouton.getBorder() == null || pBouton.getBorder() instanceof UIResource) {
            pBouton.setBorder(new MargeBouton());
        }
    }

    /**
     * Fond du bouton selon son état : appuyé, survolé ou au repos.
     */
    static Color fond(ButtonModel pModele) {
        Color rFond = Habillage.Role.CHIFFRE.aFond;
        if (pModele.isPressed()) {
            rFond = Touche.nuance(rFond, -12);
        } else if (pModele.isRollover() && pModele.isEnabled()) {
            rFond = Touche.eclaircir(rFond);
        }
        return rFond;
    }

    @Override
    public void paint(Graphics pGraphics, JComponent pComposant) {
        AbstractButton vBouton = (AbstractButton) pComposant;
        if (vBouton.isContentAreaFilled()) {
            Graphics2D vG = (Graphics2D) pGraphics.create();
            vG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D vForme = new RoundRectangle2D.Double(0.5, 0.5, vBouton.getWidth() - 1.0,
                    vBouton.getHeight() - 1.0, ARRONDI, ARRONDI);
            vG.setColor(fond(vBouton.getModel()));
            vG.fill(vForme);
            vG.setColor(Habillage.BORDURE);
            vG.setStroke(new BasicStroke(1f));
            vG.draw(vForme);
            vG.dispose();
        }
        super.paint(pGraphics, pComposant);
    }

    @Override
    protected void paintButtonPressed(Graphics pGraphics, AbstractButton pBouton) {
        // Le fond plus sombre suffit
    }

    /** Marges intérieures des boutons de formulaire, remplaçables par une bordure choisie. */
    static final class MargeBouton extends AbstractBorder implements UIResource {
        private static final long serialVersionUID = 1L;

        @Override
        public Insets getBorderInsets(Component pComposant, Insets pMarges) {
            pMarges.set(6, 14, 6, 14);
            return pMarges;
        }
    }

    /**
     * Bordure d'un champ de saisie, enfoncé comme l'écran : cadre fin, ombre en haut à l'intérieur.
     */
    static javax.swing.border.Border champ() {
        return BorderFactory.createCompoundBorder(new ChampEnfonce(), BorderFactory.createEmptyBorder(4, 7, 4, 7));
    }

    /** Cadre du champ, avec l'ombre du bord supérieur. */
    static final class ChampEnfonce extends AbstractBorder implements UIResource {
        private static final long serialVersionUID = 1L;

        @Override
        public void paintBorder(Component pComposant, Graphics pGraphics, int pX, int pY, int pLargeur,
                int pHauteur) {
            Graphics2D vG = (Graphics2D) pGraphics.create();
            vG.setColor(Habillage.BORDURE);
            vG.drawRect(pX, pY, pLargeur - 1, pHauteur - 1);
            vG.setColor(new Color(0, 0, 0, Habillage.CLAIR ? 30 : 90));
            vG.drawLine(pX + 1, pY + 1, pX + pLargeur - 2, pY + 1);
            vG.setColor(new Color(0, 0, 0, Habillage.CLAIR ? 14 : 45));
            vG.drawLine(pX + 1, pY + 2, pX + pLargeur - 2, pY + 2);
            vG.dispose();
        }

        @Override
        public Insets getBorderInsets(Component pComposant, Insets pMarges) {
            pMarges.set(2, 1, 1, 1);
            return pMarges;
        }
    }
}
