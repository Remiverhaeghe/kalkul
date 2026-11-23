package com.kalkul;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Onglet Historique : les calculs terminés, du plus récent au plus ancien. Un double-clic ou Entrée remet
 * le résultat à l'écran ; un champ cherche dans la liste, un bouton l'exporte en CSV.
 */
class PanneauHistorique extends JPanel {
    private static final long serialVersionUID = 1L;

    private final DefaultListModel<MoteurCalcul.Calcul> aModel = new DefaultListModel<>();
    private final JList<MoteurCalcul.Calcul> aList = new JList<>(aModel);
    private final JTextField aSearch = new JTextField(18);
    private List<MoteurCalcul.Calcul> aAll = new ArrayList<>();

    PanneauHistorique(Consumer<String> pOnReuse, Runnable pOnClear) {
        super(new BorderLayout(0, 8));
        setBackground(Habillage.SURFACE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        aList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        aList.setBackground(Habillage.CHAMP);
        aList.setCellRenderer(new Rendu());
        aList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent pEvent) {
                if (pEvent.getClickCount() == 2) {
                    reuse(pOnReuse);
                }
            }
        });
        aList.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "reprendre");
        aList.getActionMap().put("reprendre", new javax.swing.AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent pEvent) {
                reuse(pOnReuse);
            }
        });

        JLabel vHint = new JLabel("Double-clic sur un calcul pour reprendre son résultat.");
        vHint.setForeground(Habillage.TEXTE_DOUX);
        aSearch.setToolTipText("Chercher un calcul ou un résultat");
        aSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent pEvent) {
                filtrer();
            }

            @Override
            public void removeUpdate(DocumentEvent pEvent) {
                filtrer();
            }

            @Override
            public void changedUpdate(DocumentEvent pEvent) {
                filtrer();
            }
        });
        JPanel vTop = new JPanel(new BorderLayout(0, 4));
        vTop.setOpaque(false);
        vTop.add(vHint, BorderLayout.NORTH);
        vTop.add(PanneauOutils.row(new JLabel("Chercher"), aSearch), BorderLayout.SOUTH);
        JButton vExport = new JButton("Exporter en CSV…");
        vExport.setFocusable(false);
        vExport.addActionListener(pEvent -> exporter());
        JButton vExportPdf = new JButton("Exporter en PDF…");
        vExportPdf.setFocusable(false);
        vExportPdf.addActionListener(pEvent -> exporterPdf());
        JButton vClear = new JButton("Vider l'historique");
        vClear.setFocusable(false);
        vClear.addActionListener(pEvent -> {
            pOnClear.run();
            aAll = new ArrayList<>();
            aModel.clear();
        });
        JPanel vBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        vBottom.setOpaque(false);
        vBottom.add(vClear);
        vBottom.add(javax.swing.Box.createHorizontalStrut(8));
        vBottom.add(vExport);
        vBottom.add(javax.swing.Box.createHorizontalStrut(8));
        vBottom.add(vExportPdf);

        add(vTop, BorderLayout.NORTH);
        add(new JScrollPane(aList), BorderLayout.CENTER);
        add(vBottom, BorderLayout.SOUTH);
    }

    /** Remplace la liste par l'historique du moteur. */
    void montrer(List<MoteurCalcul.Calcul> pHistory) {
        aAll = new ArrayList<>(pHistory);
        filtrer();
    }

    /** Cherche dans l'historique, comme si le texte avait été tapé dans le champ. */
    void chercher(String pTexte) {
        aSearch.setText(pTexte);
    }

    private void filtrer() {
        aModel.clear();
        Historique.filtrer(aAll, aSearch.getText()).forEach(aModel::addElement);
    }

    /** Calculs affichés, ceux que la recherche garde, en CSV. */
    String csv() {
        return Historique.enCsv(Historique.filtrer(aAll, aSearch.getText()));
    }

    private void exporter() {
        JFileChooser vChooser = new JFileChooser();
        vChooser.setSelectedFile(new File("historique-kalkul.csv"));
        if (vChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            // Marque UTF-8 en tête : sans elle, un tableur sous Windows abîme les accents et les signes
            Files.writeString(vChooser.getSelectedFile().toPath(), "\uFEFF" + csv(), StandardCharsets.UTF_8);
        } catch (IOException vException) {
            JOptionPane.showMessageDialog(this, "Le fichier n'a pas pu être écrit : " + vException.getMessage(),
                    "Export", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Calculs affichés, ceux que la recherche garde, en PDF. */
    byte[] pdf() {
        return Historique.enPdf(Historique.filtrer(aAll, aSearch.getText()), java.time.LocalDate.now());
    }

    private void exporterPdf() {
        JFileChooser vChooser = new JFileChooser();
        vChooser.setSelectedFile(new File("historique-kalkul.pdf"));
        if (vChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            Files.write(vChooser.getSelectedFile().toPath(), pdf());
        } catch (IOException vException) {
            JOptionPane.showMessageDialog(this, "Le fichier n'a pas pu être écrit : " + vException.getMessage(),
                    "Export", JOptionPane.ERROR_MESSAGE);
        }
    }

    int taille() {
        return aModel.getSize();
    }

    private void reuse(Consumer<String> pOnReuse) {
        MoteurCalcul.Calcul vSelected = aList.getSelectedValue();
        if (vSelected != null) {
            pOnReuse.accept(vSelected.resultat());
        }
    }

    /** Une ligne : le calcul en gris, le résultat en chiffres de l'écran. */
    private static final class Rendu extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList<?> pList, Object pValue, int pIndex, boolean pSelected,
                boolean pFocus) {
            MoteurCalcul.Calcul vCalcul = (MoteurCalcul.Calcul) pValue;
            String vTexte = "<html><span style='color:" + hex(Habillage.TEXTE_DOUX) + "'>" + vCalcul.expression()
                    + " =</span>&nbsp;&nbsp;<b>" + vCalcul.resultat() + "</b></html>";
            JLabel rLabel = (JLabel) super.getListCellRendererComponent(pList, vTexte, pIndex, pSelected, pFocus);
            rLabel.setFont(Habillage.chiffres(Font.PLAIN, 16));
            rLabel.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            return rLabel;
        }

        private static String hex(java.awt.Color pColor) {
            return String.format("#%06X", pColor.getRGB() & 0xFFFFFF);
        }
    }
}
