package stni.smr;

import java.io.IOException;

/**
 *
 */
public interface SmrPart {
    Smr getSmr();

    void save() throws IOException;
}
