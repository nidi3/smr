package stni.smr.geo.impl;

import stni.smr.geo.ElevationProvider;
import stni.smr.geo.WorldCoord;

import java.io.*;

/**
 *
 */
public class FilesystemCachingElevationProvider implements ElevationProvider {
    private final ElevationProvider delegate;

    public FilesystemCachingElevationProvider(ElevationProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    public double[][] getElevation(int w, int h, WorldCoord northEast, WorldCoord southWest) throws IOException {
        final File file = new File("elevation(" + w + "x" + h + "),(" + northEast + "," + southWest + ")");
        if (file.exists()) {
            return readFromFile(file, w, h);
        }
        return writeToFile(file, delegate.getElevation(w, h, northEast, southWest));
    }

    private double[][] writeToFile(File file, double[][] elevation) throws IOException {
        try (final ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            for (int y = 0; y < elevation[0].length; y++) {
                for (int x = 0; x < elevation.length; x++) {
                    out.writeInt((int)elevation[x][y]); //TODO correct!
                }
            }
        }
        return elevation;
    }

    private double[][] readFromFile(File file, int w, int h) throws IOException {
        double[][] res = new double[w][h];
        try (final ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    res[x][y] = in.readInt();  //TODO correct!
                }
            }
        }
        return res;
    }
}
