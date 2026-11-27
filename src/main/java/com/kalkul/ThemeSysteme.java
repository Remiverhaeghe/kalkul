package com.kalkul;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Choix du thème au démarrage : clair ou sombre comme les applications Windows,
 * ou imposé par {@code -Dkalkul.theme=clair} ou {@code sombre}.
 */
final class ThemeSysteme {
    static final String PROPRIETE = "kalkul.theme";
    private static final String CLE = "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize";
    private static final String VALEUR = "AppsUseLightTheme";

    private ThemeSysteme() {
    }

    /**
     * Vrai si la sortie de {@code reg query} indique des applications en mode clair (valeur 0x1).
     */
    static boolean clairSelonRegistre(String pSortie) {
        boolean rClair = false;
        for (String vLigne : pSortie.split("\\R")) {
            String vTexte = vLigne.trim();
            if (vTexte.startsWith(VALEUR)) {
                rClair = vTexte.endsWith("0x1");
            }
        }
        return rClair;
    }

    /**
     * Thème clair ou non, sans réglage gardé : la propriété l'emporte, puis le réglage de Windows.
     */
    static boolean choisirClair(String pPropriete, String pSysteme, Supplier<String> pRegistre) {
        boolean rClair = choisirClair(pPropriete, "auto", pSysteme, pRegistre);
        return rClair;
    }

    /**
     * Thème clair ou non : la propriété l'emporte, puis le réglage gardé dans Outils > Réglages
     * («&nbsp;clair&nbsp;» ou «&nbsp;sombre&nbsp;»), puis le réglage de Windows ; sombre ailleurs.
     */
    static boolean choisirClair(String pPropriete, String pReglage, String pSysteme, Supplier<String> pRegistre) {
        String vPropriete = pPropriete == null ? "" : pPropriete.trim().toLowerCase(Locale.ROOT);
        String vReglage = pReglage == null ? "" : pReglage.trim().toLowerCase(Locale.ROOT);
        String vChoix = "clair".equals(vPropriete) || "sombre".equals(vPropriete) ? vPropriete : vReglage;
        boolean rClair;
        if ("clair".equals(vChoix)) {
            rClair = true;
        } else if ("sombre".equals(vChoix)) {
            rClair = false;
        } else if (pSysteme != null && pSysteme.startsWith("Windows")) {
            rClair = clairSelonRegistre(pRegistre.get());
        } else {
            rClair = false;
        }
        return rClair;
    }

    static boolean estClair() {
        boolean rClair = choisirClair(System.getProperty(PROPRIETE), Reglages.theme(),
                System.getProperty("os.name"), ThemeSysteme::lireRegistre);
        return rClair;
    }

    /**
     * Sortie de {@code reg query} pour le réglage du thème ; vide si la commande échoue ou tarde.
     */
    private static String lireRegistre() {
        String rSortie = "";
        try {
            Process vProcess = new ProcessBuilder("reg", "query", CLE, "/v", VALEUR).redirectErrorStream(true).start();
            if (vProcess.waitFor(2, TimeUnit.SECONDS)) {
                try (InputStream vFlux = vProcess.getInputStream()) {
                    rSortie = new String(vFlux.readAllBytes(), StandardCharsets.UTF_8);
                }
            } else {
                vProcess.destroy();
            }
        } catch (IOException vException) {
            rSortie = "";
        } catch (InterruptedException vException) {
            Thread.currentThread().interrupt();
        }
        return rSortie;
    }
}
