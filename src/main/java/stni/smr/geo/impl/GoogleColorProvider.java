package stni.smr.geo.impl;

import stni.smr.geo.ColorProvider;
import stni.smr.geo.WorldCoord;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;

/**
 *
 */
public class GoogleColorProvider extends AbstractXmlHttpService implements ColorProvider {
    private static final int MERCATOR_RANGE = 256;
    private static final double PIXEL_ORIGIN = MERCATOR_RANGE / 2D;
    private static final double PIXELS_PER_LON_DEGREE = MERCATOR_RANGE / 360D;
    private static final double PIXELS_PER_LON_RADIAN = MERCATOR_RANGE / (2 * Math.PI);

    private final int w, h;
    private final double scale;
    private final double centerX, centerY;
    private final BufferedImage image;

    public GoogleColorProvider(WorldCoord center, int zoom, int w, int h) throws IOException {
        scale = Math.pow(2, zoom) / .8;
        centerX = xOf(center);
        centerY = yOf(center);

        image = ImageIO.read(streamFromHttpGet(URI.create("http://maps.googleapis.com/maps/api/staticmap?center=" +
                center + "&zoom=" + zoom + "&size=" + w + "x" + h + "&maptype=satellite&sensor=false")));
        this.w = image.getWidth();
        this.h = image.getHeight();
    }

    @Override
    public Color colorAt(WorldCoord coord) {
        final Point2D point = mapOf(coord);
        if (point.getX() < 0 || point.getX() >= w || point.getY() < 0 || point.getY() >= h) {
            throw new IllegalArgumentException("Point outside of image " + point);
        }
        return new Color(image.getRGB((int) Math.round(point.getX()), (int) Math.round(point.getY())));
    }

    public WorldCoord ofNorthEastCorner() {
        return coordOf(centerX - (w / 2) / scale, centerY - (h / 2) / scale);
    }

    public WorldCoord ofSouthWestCorner() {
        return coordOf(centerX + (w / 2) / scale, centerY + (h / 2) / scale);
    }

    public Point2D mapOf(WorldCoord coord) {
        double northEastX = centerX - (w / 2) / scale;
        double northEastY = centerY - (h / 2) / scale;
        return new Point2D.Double(
                (xOf(coord) - northEastX) * scale,
                (yOf(coord) - northEastY) * scale);
    }

    private double xOf(WorldCoord coord) {
        return PIXEL_ORIGIN + coord.getLng() * PIXELS_PER_LON_DEGREE;
    }

    private double yOf(WorldCoord coord) {
        double siny = bound(Math.sin(degreesToRadians(coord.getLat())), -0.9999, 0.9999);
        return PIXEL_ORIGIN + 0.5 * Math.log((1 + siny) / (1 - siny)) * -PIXELS_PER_LON_RADIAN;
    }

    private WorldCoord coordOf(double x, double y) {
        double lng = (x - PIXEL_ORIGIN) / PIXELS_PER_LON_DEGREE;
        double latRadians = (y - PIXEL_ORIGIN) / -PIXELS_PER_LON_RADIAN;
        double lat = radiansToDegrees(2 * Math.atan(Math.exp(latRadians)) - Math.PI / 2);
        return WorldCoord.ofLatLng(lat, lng);
    }

    private double bound(double value, Double opt_min, Double opt_max) {
        if (opt_min != null) {
            value = Math.max(value, opt_min);
        }
        if (opt_max != null) {
            value = Math.min(value, opt_max);
        }
        return value;
    }

    private double degreesToRadians(double deg) {
        return deg * (Math.PI / 180);
    }

    private double radiansToDegrees(double rad) {
        return rad / (Math.PI / 180);
    }

}
