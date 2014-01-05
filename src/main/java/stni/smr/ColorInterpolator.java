package stni.smr;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 *
 */
public class ColorInterpolator {
    private final BufferedImage image;

    public ColorInterpolator(BufferedImage image) {
        this.image = image;
    }

    public void noInterpolation(SmrMap.ColorProvider colorProvider) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                final Color color = colorProvider.colorAt(x, y);
                if (color != null) {
                    image.setRGB(x, y, color.getRGB());
                }
            }
        }
    }

    public void interpolateX(SmrMap.ColorProvider colorProvider) {
        for (int y = 0; y < image.getHeight(); y++) {
            interpolateX2(y, colorProvider);
        }
    }

    public void interpolateXY(SmrMap.ColorProvider colorProvider) {
        for (int y = 0; y < image.getHeight(); y += 2) {
            interpolateX2(y, colorProvider);
        }
        for (int y = 1; y < image.getHeight() - 1; y += 2) {
            for (int x = 0; x < image.getWidth(); x++) {
                interpolateRGB(x, y, x, y - 1, x, y + 1);
            }
        }
    }

    private void interpolateX2(int y, SmrMap.ColorProvider colorProvider) {
        for (int x = 0; x < image.getWidth(); x += 2) {
            final Color color = colorProvider.colorAt(x, y);
            if (color != null) {
                image.setRGB(x, y, color.getRGB());
            }
        }
        for (int x = 1; x < image.getWidth() - 1; x += 2) {
            interpolateRGB(x, y, x - 1, y, x + 1, y);
        }
    }

    private void interpolateRGB(int x, int y, int x0, int y0, int x1, int y1) {
        final int rgb0 = image.getRGB(x0, y0);
        final int rgb1 = image.getRGB(x1, y1);
        image.setRGB(x, y, (((rgb0 >> 24) + (rgb1 >> 24)) << 23) |
                (((rgb0 >> 16) + (rgb1 >> 16)) << 15) |
                (((rgb0 >> 8) + (rgb1 >> 8)) << 7) |
                (((rgb0 >> 0) + (rgb1 >> 0)) >> 1));
    }
}
