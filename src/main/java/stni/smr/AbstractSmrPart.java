package stni.smr;

import java.io.File;

/**
 *
 */
abstract class AbstractSmrPart implements SmrPart {
    protected final Smr smr;
    protected final File file;

    protected AbstractSmrPart(Smr smr, File file) {
        this.smr = smr;
        this.file = file;
    }

    public Smr getSmr() {
        return smr;
    }
}
