package stni.smr.geo.impl;

import com.vividsolutions.jts.geom.MultiPolygon;
import org.opengis.feature.simple.SimpleFeature;
import stni.smr.geo.Area;

/**
 *
 */
class G3g12GeoAttributes implements GeoAttributes {
    private static final String ATTR_POLYGON = "the_geom";
    private static final String ATTR_CANTON = "KT";
    private static final String ATTR_DISTRICT = "BEZIRK";
    private static final String ATTR_NAME = "NAME";
    private static final String ATTR_MUNICIPAL = "GMDE";

    @Override
    public MultiPolygon polygonOf(SimpleFeature feature) {
        return (MultiPolygon) feature.getAttribute(ATTR_POLYGON);
    }

    @Override
    public AreaLoader cantonAreaLoader() {
        return new AreaLoader() {
            @Override
            public Area load(SimpleFeature feature) {
                return new Area("" + cantonIdOf(feature), nameOf(feature));
            }
        };
    }

    @Override
    public AreaLoader municipalAreaLoader() {
        return new AreaLoader() {
            @Override
            public Area load(SimpleFeature feature) {
                return new Area("" + municipalIdOf(feature), nameOf(feature));
            }
        };
    }

    @Override
    public AreaLoader districtAreaLoader() {
        return new AreaLoader() {
            @Override
            public Area load(SimpleFeature feature) {
                return new Area("" + districtIdOf(feature), nameOf(feature));
            }
        };
    }

    private String nameOf(SimpleFeature feature) {
        return (String) feature.getAttribute(ATTR_NAME);
    }

    private Integer cantonIdOf(SimpleFeature feature) {
        return (Integer) feature.getAttribute(ATTR_CANTON);
    }

    private Integer municipalIdOf(SimpleFeature feature) {
        return (Integer) feature.getAttribute(ATTR_MUNICIPAL);
    }

    private Integer districtIdOf(SimpleFeature feature) {
        return (Integer) feature.getAttribute(ATTR_DISTRICT);
    }


}
