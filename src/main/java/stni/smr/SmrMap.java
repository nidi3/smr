package stni.smr;

import stni.smr.geo.WorldCoord;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 *
 */
public class SmrMap extends AbstractSmrPart {
    private final BufferedImage image;
    private Coords coords;

    public interface HeightProvider {
        int heightAt(int x, int y);
    }

    public enum Interpolation {
        NONE, X, XY
    }

    public interface ColorProvider {
        Color colorAt(int x, int y);
    }

    SmrMap(Smr smr, File dir) throws IOException {
        super(smr, new File(dir, "map_" + dir.getName() + ".tga"));
        image = TgaIO.read(file);
        if (image == null) {
            throw new IllegalArgumentException("Could not load map from " + file);
        }
    }

    public void fillHeight(HeightProvider heightProvider) {
        final BufferedImage heightMap = getHeightMap();
        for (int y = 0; y < pixelSize(); y++) {
            for (int x = 0; x < pixelSize(); x++) {
                final int height = heightProvider.heightAt(x, y);
                if (height >= 0) {
                    heightMap.setRGB(x, y, new Color(height, height, height).getRGB());
                }
            }
        }
    }

    public void fillColor(Interpolation interpolation, ColorProvider colorProvider) {
        final ColorInterpolator interpolator = new ColorInterpolator(getColorMap());
        switch (interpolation) {
            case NONE:
                interpolator.noInterpolation(colorProvider);
                break;
            case X:
                interpolator.interpolateX(colorProvider);
                break;
            case XY:
                interpolator.interpolateXY(colorProvider);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void setCoords(WorldCoord northEast, WorldCoord southWest) {
        coords = new Coords(pixelSize(), unitSize(), northEast, southWest);
    }

    public Coords coords() {
        return coords;
    }

    public int pixelSize() {
        return image.getWidth();
    }

    public int unitSize() {
        return (pixelSize() - 1) / 60;
    }

    public BufferedImage getHeightMap() {
        return image.getSubimage(0, 0, pixelSize(), pixelSize());
    }

    public BufferedImage getColorMap() {
        return image.getSubimage(0, 3 * pixelSize(), pixelSize(), pixelSize());
    }

    public BufferedImage getFillingMap() {
        return image.getSubimage(0, 7 * pixelSize(), pixelSize(), pixelSize());
    }

    public void save() throws IOException {
        TgaIO.write(image, file);
    }
}
