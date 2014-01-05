package stni.smr.geo.impl;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import stni.smr.geo.SwissCoord;
import stni.smr.geo.WorldCoord;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
class SimpleFeatureIterable implements Iterable<SimpleFeature> {
    private final GeoAttributes geoAttributes;
    private final boolean swissCoord;
    private final List<SimpleFeature> features;

    SimpleFeatureIterable(GeoAttributes geoAttributes, boolean swissCoord) {
        this.geoAttributes = geoAttributes;
        this.swissCoord = swissCoord;
        features = new ArrayList<>();
    }

    public SimpleFeatureIterable add(SimpleFeatureIterator iter) {
        while (iter.hasNext()) {
            features.add(iter.next());
        }
        iter.close();
//            GeoAttributes geoAttributes = new GeoAttributes();
//            for (SimpleFeature feature : features) {
//                geoAttributes.setPolygonOf(feature, DouglasPeuckerSimplifier.simplify(geoAttributes.polygonOf(feature), 500));
//            }
        return this;
    }

    public GeometryFactory getFactory() {
        if (features.isEmpty()) {
            throw new IllegalStateException("no features");
        }
        return geoAttributes.polygonOf(features.get(0)).getFactory();
    }

    public GeoAttributes getGeoAttributes() {
        return geoAttributes;
    }

    public double[] convertToSource(WorldCoord... coords) {
        return swissCoord ? convertSwissToSource(coords) : convertWorldToSource(coords);
    }

    public double[] convertToWorld(Coordinate[] coordinates) {
        return swissCoord ? convertSwissToWorld(coordinates) : convertWorldToWorld(coordinates);
    }

    private double[] convertWorldToSource(WorldCoord... coords) {
        double[] res = new double[coords.length * 2];
        for (int i = 0; i < coords.length; i++) {
            res[i * 2] = coords[i].getLng();
            res[i * 2 + 1] = coords[i].getLat();
        }
        return res;
    }

    private double[] convertSwissToSource(WorldCoord... coords) {
        double[] res = new double[coords.length * 2];
        for (int i = 0; i < coords.length; i++) {
            final SwissCoord swiss = SwissCoord.ofWorldCoord(coords[i]);
            res[i * 2] = swiss.getX();
            res[i * 2 + 1] = swiss.getY();
        }
        return res;
    }

    private double[] convertWorldToWorld(Coordinate[] coordinates) {
        double[] a = new double[coordinates.length * 2];
        for (int j = 0; j < coordinates.length; j++) {
            a[j * 2] = coordinates[j].x;
            a[j * 2 + 1] = coordinates[j].y;
        }
        return a;
    }

    private double[] convertSwissToWorld(Coordinate[] coordinates) {
        double[] a = new double[coordinates.length * 2];
        for (int j = 0; j < coordinates.length; j++) {
            WorldCoord worldCoord = WorldCoord.ofSwissCoord(coordinates[j].x, coordinates[j].y);
            a[j * 2] = worldCoord.getLat();
            a[j * 2 + 1] = worldCoord.getLng();
        }
        return a;
    }

    @Override
    public Iterator<SimpleFeature> iterator() {
        return features.iterator();
    }
}
