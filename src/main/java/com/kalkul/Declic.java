package com.kalkul;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Petit déclic de touche, fabriqué à la volée : une note brève qui s'éteint vite, comme le
 * contact d'une touche mécanique. Aucun fichier son ; sans carte son, rien ne se passe.
 */
final class Declic {
    static final float FREQUENCE_ECHANTILLONNAGE = 44100f;
    static final int DUREE_MS = 18;
    private static final double NOTE_HZ = 2400;
    private static final double VOLUME = 0.35;

    private Declic() {
    }

    /**
     * Échantillons 16 bits du déclic : sinus amorti, en octets petit-boutiste.
     */
    static byte[] echantillons() {
        int vNombre = (int) (FREQUENCE_ECHANTILLONNAGE * DUREE_MS / 1000);
        byte[] rOctets = new byte[vNombre * 2];
        for (int vIndex = 0; vIndex < vNombre; vIndex++) {
            double vTemps = vIndex / FREQUENCE_ECHANTILLONNAGE;
            double vAmorti = Math.exp(-vIndex * 6.0 / vNombre);
            short vValeur = (short) (Math.sin(2 * Math.PI * NOTE_HZ * vTemps) * vAmorti * VOLUME * Short.MAX_VALUE);
            rOctets[2 * vIndex] = (byte) vValeur;
            rOctets[2 * vIndex + 1] = (byte) (vValeur >> 8);
        }
        return rOctets;
    }

    /**
     * Joue le déclic sans bloquer l'interface.
     */
    static void jouer() {
        Thread vLecture = new Thread(() -> {
            AudioFormat vFormat = new AudioFormat(FREQUENCE_ECHANTILLONNAGE, 16, 1, true, false);
            byte[] vOctets = echantillons();
            try (SourceDataLine vLigne = AudioSystem.getSourceDataLine(vFormat)) {
                vLigne.open(vFormat);
                vLigne.start();
                vLigne.write(vOctets, 0, vOctets.length);
                vLigne.drain();
            } catch (LineUnavailableException | IllegalArgumentException vException) {
                // Pas de sortie son : la calculatrice reste muette
            }
        }, "declic");
        vLecture.setDaemon(true);
        vLecture.start();
    }
}
