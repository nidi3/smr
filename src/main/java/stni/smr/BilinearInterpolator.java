package stni.smr;

/**
 *
 */
public class BilinearInterpolator {
    private final double[][] values;
    private final double sx, sy;

    public BilinearInterpolator(double[][] values, double sx, double sy) {
        this.values = values;
        this.sx = sx;
        this.sy = sy;
    }

    public BilinearInterpolator(double[][] values) {
        this(values, 1, 1);
    }

    public BilinearInterpolator scaledTo(double w, double h) {
        return new BilinearInterpolator(values, (values.length - 1) / w, (values[0].length - 1) / h);
    }

    public double valueAt(double x, double y) {
        return valueAtScaled(x * sx, y * sy);
    }

    private double valueAtScaled(double x, double y) {
        final int x0 = (int) x;
        final int x1 = (int) x + 1;
        final int y0 = (int) y;
        final int y1 = (int) y + 1;
        return values[x0][y0] * (x1 - x) * (y1 - y) +
                values[x1][y0] * (x - x0) * (y1 - y) +
                values[x0][y1] * (x1 - x) * (y - y0) +
                values[x1][y1] * (x - x0) * (y - y0);
    }
}
