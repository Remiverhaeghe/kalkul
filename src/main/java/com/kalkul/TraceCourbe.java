package com.kalkul;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

/**
 * Zone de dessin d'une courbe : repère gradué, courbe, tangente en un point et aire sous la courbe.
 * Le dessin utilise seulement Java2D.
 */
class TraceCourbe extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int MARGIN_LEFT = 46;
    private static final int MARGIN_OTHER = 14;
    private static final int MARGIN_BOTTOM = 24;
    private static final Color GRID = new Color(225, 225, 225);
    private static final Color AXIS = new Color(90, 90, 90);
    private static final Color CURVE = new Color(25, 100, 200);
    private static final Color TANGENT = new Color(210, 70, 40);
    private static final Color AREA = new Color(25, 100, 200, 60);

    private transient EchantillonCourbe aSample;
    private transient Double aTangentX;
    private transient double aTangentY;
    private transient double aTangentSlope;
    private transient Double aAreaFrom;
    private transient double aAreaTo;

    TraceCourbe() {
        setBackground(Color.WHITE);
    }

    void setCurve(EchantillonCourbe pSample) {
        aSample = pSample;
        aTangentX = null;
        aAreaFrom = null;
        repaint();
    }

    void setTangent(double pX, double pY, double pSlope) {
        aTangentX = pX;
        aTangentY = pY;
        aTangentSlope = pSlope;
        repaint();
    }

    void setArea(double pFrom, double pTo) {
        aAreaFrom = Math.min(pFrom, pTo);
        aAreaTo = Math.max(pFrom, pTo);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics pGraphics) {
        super.paintComponent(pGraphics);
        Graphics2D vG = (Graphics2D) pGraphics.create();
        vG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (aSample == null) {
            vG.setColor(AXIS);
            vG.drawString("Saisis une formule puis clique sur Tracer.", MARGIN_LEFT, getHeight() / 2);
            vG.dispose();
            return;
        }
        Rectangle2D vPlot = new Rectangle2D.Double(MARGIN_LEFT, MARGIN_OTHER,
                Math.max(10, getWidth() - MARGIN_LEFT - MARGIN_OTHER),
                Math.max(10, getHeight() - MARGIN_OTHER - MARGIN_BOTTOM));
        drawGrid(vG, vPlot);
        vG.setClip(vPlot);
        drawArea(vG, vPlot);
        drawCurve(vG, vPlot);
        drawTangent(vG, vPlot);
        vG.dispose();
    }

    private double toPixelX(Rectangle2D pPlot, double pX) {
        double rPixel = pPlot.getX() + (pX - aSample.xMin()) / (aSample.xMax() - aSample.xMin()) * pPlot.getWidth();
        return rPixel;
    }

    private double toPixelY(Rectangle2D pPlot, double pY) {
        double rPixel = pPlot.getY() + (aSample.yMax() - pY) / (aSample.yMax() - aSample.yMin()) * pPlot.getHeight();
        return rPixel;
    }

    private void drawGrid(Graphics2D pG, Rectangle2D pPlot) {
        FontMetrics vMetrics = pG.getFontMetrics();
        double vStepX = EchantillonCourbe.niceStep(aSample.xMax() - aSample.xMin());
        double vStepY = EchantillonCourbe.niceStep(aSample.yMax() - aSample.yMin());
        pG.setStroke(new BasicStroke(1f));
        for (double vX = Math.ceil(aSample.xMin() / vStepX) * vStepX; vX <= aSample.xMax(); vX += vStepX) {
            double vPixel = toPixelX(pPlot, vX);
            pG.setColor(GRID);
            pG.draw(new Line2D.Double(vPixel, pPlot.getMinY(), vPixel, pPlot.getMaxY()));
            pG.setColor(AXIS);
            String vLabel = Affichage.format(vX);
            pG.drawString(vLabel, (float) (vPixel - vMetrics.stringWidth(vLabel) / 2.0), (float) (pPlot.getMaxY() + 16));
        }
        for (double vY = Math.ceil(aSample.yMin() / vStepY) * vStepY; vY <= aSample.yMax(); vY += vStepY) {
            double vPixel = toPixelY(pPlot, vY);
            pG.setColor(GRID);
            pG.draw(new Line2D.Double(pPlot.getMinX(), vPixel, pPlot.getMaxX(), vPixel));
            pG.setColor(AXIS);
            String vLabel = Affichage.format(vY);
            pG.drawString(vLabel, (float) (pPlot.getMinX() - 6 - vMetrics.stringWidth(vLabel)), (float) (vPixel + 4));
        }
        pG.setColor(AXIS);
        pG.draw(pPlot);
        pG.setStroke(new BasicStroke(1.6f));
        if (aSample.yMin() <= 0 && aSample.yMax() >= 0) {
            double vPixel = toPixelY(pPlot, 0);
            pG.draw(new Line2D.Double(pPlot.getMinX(), vPixel, pPlot.getMaxX(), vPixel));
        }
        if (aSample.xMin() <= 0 && aSample.xMax() >= 0) {
            double vPixel = toPixelX(pPlot, 0);
            pG.draw(new Line2D.Double(vPixel, pPlot.getMinY(), vPixel, pPlot.getMaxY()));
        }
    }

    private void drawCurve(Graphics2D pG, Rectangle2D pPlot) {
        pG.setColor(CURVE);
        pG.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D vPath = new Path2D.Double();
        boolean vPenDown = false;
        for (int vIndex = 0; vIndex < aSample.size(); vIndex++) {
            double vY = aSample.y(vIndex);
            if (!EchantillonCourbe.isUsable(vY)) {
                // Trou dans la courbe : asymptote ou valeur non définie
                vPenDown = false;
                continue;
            }
            double vPx = toPixelX(pPlot, aSample.x(vIndex));
            double vPy = toPixelY(pPlot, vY);
            if (vPenDown) {
                vPath.lineTo(vPx, vPy);
            } else {
                vPath.moveTo(vPx, vPy);
                vPenDown = true;
            }
        }
        pG.draw(vPath);
    }

    private void drawArea(Graphics2D pG, Rectangle2D pPlot) {
        if (aAreaFrom == null) {
            return;
        }
        double vBaseline = toPixelY(pPlot, 0);
        Path2D vPath = new Path2D.Double();
        boolean vOpen = false;
        double vLastX = 0;
        for (int vIndex = 0; vIndex < aSample.size(); vIndex++) {
            double vX = aSample.x(vIndex);
            double vY = aSample.y(vIndex);
            boolean vInside = vX >= aAreaFrom && vX <= aAreaTo && EchantillonCourbe.isUsable(vY);
            if (vInside) {
                double vPx = toPixelX(pPlot, vX);
                if (!vOpen) {
                    vPath.moveTo(vPx, vBaseline);
                    vOpen = true;
                }
                vPath.lineTo(vPx, toPixelY(pPlot, vY));
                vLastX = vPx;
            }
        }
        if (vOpen) {
            vPath.lineTo(vLastX, vBaseline);
            vPath.closePath();
            pG.setColor(AREA);
            pG.fill(vPath);
        }
    }

    private void drawTangent(Graphics2D pG, Rectangle2D pPlot) {
        if (aTangentX == null) {
            return;
        }
        double vFromX = aSample.xMin();
        double vToX = aSample.xMax();
        double vFromY = aTangentY + aTangentSlope * (vFromX - aTangentX);
        double vToY = aTangentY + aTangentSlope * (vToX - aTangentX);
        pG.setColor(TANGENT);
        pG.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[] {6f, 4f}, 0f));
        pG.draw(new Line2D.Double(toPixelX(pPlot, vFromX), toPixelY(pPlot, vFromY),
                toPixelX(pPlot, vToX), toPixelY(pPlot, vToY)));
        pG.setStroke(new BasicStroke(1f));
        double vPx = toPixelX(pPlot, aTangentX);
        double vPy = toPixelY(pPlot, aTangentY);
        pG.fill(new java.awt.geom.Ellipse2D.Double(vPx - 4, vPy - 4, 8, 8));
    }
}
