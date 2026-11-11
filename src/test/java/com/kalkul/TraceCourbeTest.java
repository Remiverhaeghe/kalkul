package com.kalkul;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;

class TraceCourbeTest {
    private static int coloredPixels(TraceCourbe pPlot) {
        pPlot.setSize(500, 350);
        BufferedImage vImage = new BufferedImage(500, 350, BufferedImage.TYPE_INT_RGB);
        Graphics2D vGraphics = vImage.createGraphics();
        pPlot.paint(vGraphics);
        vGraphics.dispose();
        int rCount = 0;
        for (int vX = 0; vX < 500; vX++) {
            for (int vY = 0; vY < 350; vY++) {
                if (vImage.getRGB(vX, vY) != Color.WHITE.getRGB()) {
                    rCount++;
                }
            }
        }
        return rCount;
    }

    @Test
    void emptyPlotShowsOnlyAHint() {
        int vEmpty = coloredPixels(new TraceCourbe());
        assertTrue(vEmpty > 0 && vEmpty < 3000);
    }

    @Test
    void curveTangentAndAreaAddPixelsToThePlot() {
        TraceCourbe vPlot = new TraceCourbe();
        vPlot.setCurve(EchantillonCourbe.sample(x -> x * x, -3, 3, 300));
        int vCurveOnly = coloredPixels(vPlot);
        vPlot.setTangent(1, 1, 2);
        vPlot.setArea(0, 2);
        int vWithExtras = coloredPixels(vPlot);
        assertTrue(vCurveOnly > 2000);
        assertTrue(vWithExtras > vCurveOnly);
    }

    @Test
    void plotWithGapsFromAnAsymptoteIsDrawnWithoutFailing() {
        TraceCourbe vPlot = new TraceCourbe();
        vPlot.setCurve(EchantillonCourbe.sample(x -> 1 / x, -5, 5, 301));
        assertTrue(coloredPixels(vPlot) > 1000);
    }
}
