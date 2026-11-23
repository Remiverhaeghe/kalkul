package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StatistiquesTest {

    @Test
    void theSummaryOfFiveValuesIsExact() {
        Statistiques.Resume vResume = Statistiques.resumer(new double[] {9, 1, 5, 3, 7});

        assertEquals(5, vResume.nombre());
        assertEquals(5.0, vResume.moyenne(), 1e-9);
        assertEquals(5.0, vResume.mediane(), 1e-9);
        assertEquals(1.0, vResume.minimum(), 1e-9);
        assertEquals(9.0, vResume.maximum(), 1e-9);
        assertEquals(3.0, vResume.premierQuartile(), 1e-9);
        assertEquals(7.0, vResume.troisiemeQuartile(), 1e-9);
        assertEquals(Math.sqrt(10), vResume.ecartTypeEchantillon(), 1e-9);
        assertEquals(Math.sqrt(8), vResume.ecartTypePopulation(), 1e-9);
    }

    @Test
    void quartilesAreInterpolatedBetweenNeighbours() {
        Statistiques.Resume vResume = Statistiques.resumer(new double[] {2, 4});

        assertEquals(3.0, vResume.mediane(), 1e-9);
        assertEquals(2.5, vResume.premierQuartile(), 1e-9);
        assertEquals(3.5, vResume.troisiemeQuartile(), 1e-9);
    }

    @Test
    void theInputIsNotSorted() {
        double[] vValeurs = {3, 1, 2};

        Statistiques.resumer(vValeurs);

        assertArrayEquals(new double[] {3, 1, 2}, vValeurs, 1e-9);
    }

    @Test
    void aSingleValueHasNoSampleDeviation() {
        Statistiques.Resume vResume = Statistiques.resumer(new double[] {4});

        assertTrue(Double.isNaN(vResume.ecartTypeEchantillon()));
        assertEquals(0.0, vResume.ecartTypePopulation(), 1e-9);
        assertEquals(4.0, vResume.mediane(), 1e-9);
    }

    @Test
    void anEmptySeriesIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> Statistiques.resumer(new double[0]));
        assertThrows(IllegalArgumentException.class, () -> Statistiques.histogramme(new double[0], 3));
        assertThrows(IllegalArgumentException.class, () -> Statistiques.histogramme(new double[] {1}, 0));
    }

    @Test
    void sturgesGivesOnePlusLogTwoOfN() {
        assertEquals(1, Statistiques.classesSturges(0));
        assertEquals(1, Statistiques.classesSturges(1));
        assertEquals(5, Statistiques.classesSturges(10));
        assertEquals(8, Statistiques.classesSturges(100));
    }

    @Test
    void theMaximumFallsInTheLastClass() {
        int[] vEffectifs = Statistiques.histogramme(new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 5);

        assertArrayEquals(new int[] {2, 2, 2, 2, 2}, vEffectifs);
    }

    @Test
    void equalValuesAllLandInTheFirstClass() {
        assertArrayEquals(new int[] {3, 0, 0}, Statistiques.histogramme(new double[] {4, 4, 4}, 3));
    }

    @Test
    void theSummaryLineNamesEachFigure() {
        String vLigne = PanneauStatistiques.resume("9 1 5 3 7");

        assertTrue(vLigne.startsWith("n = 5 | moyenne "));
        assertTrue(vLigne.contains("| médiane "));
        assertTrue(vLigne.contains("| Q1 ") && vLigne.contains("| Q3 "));
        assertTrue(vLigne.contains("(population "));
        assertTrue(PanneauStatistiques.resume("4").contains("écart-type - (population "));
    }
}
