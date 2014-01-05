package stni.smr.geo.impl;

import com.vividsolutions.jts.geom.MultiPolygon;
import org.opengis.feature.simple.SimpleFeature;

/**
 *
 */
public interface GeoAttributes {
    MultiPolygon polygonOf(SimpleFeature feature);

    AreaLoader cantonAreaLoader();

    AreaLoader municipalAreaLoader();

    AreaLoader districtAreaLoader();
}
