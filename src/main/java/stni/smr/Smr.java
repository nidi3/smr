package stni.smr;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class Smr {
    private final SmrPart[] parts;

    public Smr(File dir) throws IOException {
        parts = new SmrPart[]{
                new SmrMap(this, dir),
                new SmrNames(this, dir),
                new SmrCities(this, dir),
        };
    }

    public void save() throws IOException {
        for (SmrPart part : parts) {
            part.save();
        }
    }

    public SmrMap getMap() {
        return (SmrMap) parts[0];
    }

    public SmrNames getNames() {
        return (SmrNames) parts[1];
    }

    public SmrCities getCities() {
        return (SmrCities) parts[2];
    }
}
