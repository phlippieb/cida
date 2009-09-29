/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.cirg.cida.components;

import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

/**
 *
 * @author andrich
 */
public class IntervalXYRenderer extends XYLineAndShapeRenderer {

    public static final int lineTickIntervalDefault = 100;
    private int lineTickInterval = lineTickIntervalDefault;

    public IntervalXYRenderer(boolean b, boolean b0) {
        super(b, b0);
    }

    @Override
    public boolean getItemShapeVisible(int series, int item) {
        if (item % lineTickInterval == 0) {
            return super.getItemShapeVisible(series, item);
        }
        ;
        return false;
    }

    public int getLineTickInterval() {
        return lineTickInterval;
    }

    public void setLineTickInterval(int lineTickInterval) {
        this.lineTickInterval = lineTickInterval;
    }
}
