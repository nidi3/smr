package stni.smr.geo;

import java.util.List;

/**
 *
 */
public interface GeoAreaService {
    Area switzerland();

    List<Area> cantons();

    List<Area> districts();

    List<Area> lakes();
}
