package stni.smr.geo.impl;

import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.simple.SimpleFeature;
import stni.smr.geo.Area;
import stni.smr.geo.GeoAreaService;
import stni.smr.geo.WorldCoord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 *
 */
public class DefaultGeoAreaService implements GeoAreaService {

    private final Area switzerland;
    private final List<Area> cantons;
    private final List<Area> districts;
    private final List<Area> lakes;
    private final SimpleFeatureIterable lakeFeatures, swissFeatures, cantonFeatures, districtFeatures;

    public DefaultGeoAreaService() throws IOException, CQLException {
        ShapeLoader loader = new ShapeLoader();
        swissFeatures = loader.switzerland();
        lakeFeatures = loader.lakes();
        cantonFeatures = loader.cantons();
        switzerland = calcSwitzerland(swissFeatures);
        cantons = getAreas(cantonFeatures, cantonFeatures.getGeoAttributes().cantonAreaLoader());
        districtFeatures = loader.districts();
        districts = getAreas(districtFeatures, districtFeatures.getGeoAttributes().districtAreaLoader());
        lakes = getAreas(lakeFeatures, lakeFeatures.getGeoAttributes().municipalAreaLoader());
    }

    public boolean isInLake(WorldCoord position) {
        return isIn(position, lakeFeatures);
    }

    public boolean isInSwitzerland(WorldCoord position) {
        return isIn(position, swissFeatures);
    }

    public void discardLakesOutsideOf(WorldCoord northWest, WorldCoord southEast) {
        discardFeaturesOutsideOf(northWest, southEast, lakeFeatures);
    }

    public void discardFeaturesOutsideOf(WorldCoord northWest, WorldCoord southEast, SimpleFeatureIterable features) {
        final double[] converted = features.convertToSource(
                northWest,
                northWest.withLng(southEast),
                southEast,
                southEast.withLng(northWest),
                northWest);
        final Polygon bounding = new Polygon(new LinearRing(new PackedCoordinateSequence.Double(converted, 2), features.getFactory()), null, features.getFactory());
        for (Iterator<SimpleFeature> iter = features.iterator(); iter.hasNext(); ) {
            SimpleFeature feature = iter.next();
            if (!bounding.intersects(features.getGeoAttributes().polygonOf(feature))) {
                iter.remove();
            }
        }
    }

    public boolean isIn(WorldCoord position, SimpleFeatureIterable features) {
        Point point = new Point(new PackedCoordinateSequence.Double(features.convertToSource(position), 2), features.getFactory());
        for (SimpleFeature feature : features) {
            final MultiPolygon multiPolygon = features.getGeoAttributes().polygonOf(feature);
            if (multiPolygon.contains(point)) {
                return true;
            }
        }
        return false;
    }

    private Area calcSwitzerland(SimpleFeatureIterable features) throws IOException {
        final MultiPolygon geo = features.getGeoAttributes().polygonOf(features.iterator().next());
        return new Area("1", "Switzerland", Collections.singletonList(features.convertToWorld(geo.getCoordinates())));
    }


    private List<Area> getAreas(SimpleFeatureIterable features, AreaLoader areaLoader) {
        List<Area> res = new ArrayList<>();
        for (SimpleFeature feature : features) {
            final MultiPolygon multiPolygon = features.getGeoAttributes().polygonOf(feature);
            Area area = areaLoader.load(feature);
            res.add(area);
            for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
                final Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);
                area.addCoordinates(features.convertToWorld(polygon.getExteriorRing().getCoordinates()));
                for (int j = 0; j < polygon.getNumInteriorRing(); j++) {
                    area.addCoordinates(features.convertToWorld(polygon.getInteriorRingN(j).getCoordinates()));
                }
            }
        }
        return res;
    }

    public Area switzerland() {
        return switzerland;
    }

    public List<Area> cantons() {
        return cantons;
    }

    public List<Area> districts() {
        return districts;
    }

    public List<Area> lakes() {
        return lakes;
    }

}
