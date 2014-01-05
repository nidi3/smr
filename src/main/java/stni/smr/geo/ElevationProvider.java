package stni.smr.geo;

import java.io.IOException;

/**
 *
 */
public interface ElevationProvider {
    double[][] getElevation(int w, int h, WorldCoord northEast, WorldCoord southWest) throws IOException;
}
