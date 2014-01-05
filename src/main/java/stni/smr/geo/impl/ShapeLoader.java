package stni.smr.geo.impl;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.filter.Filter;

import java.io.IOException;
import java.net.URL;

/**
 *
 */
class ShapeLoader {
    private static final String
            SWISS = "g3g12_shp_121130/G3L12.shp",
            CANTONS = "g3g12_shp_121130/G3K12.shp",
            DISTRICTS = "g3g12_shp_121130/G3B12.shp",
            LAKES = /*"ne_10m_lakes/ne_10m_lakes.shp", //*/ "g3g12_shp_121130/G3G12.shp",
            LAKES_EU = "ne_10m_lakes_europe/ne_10m_lakes_europe.shp"; // "g3g12_shp_121130/G3G12.shp";

    private SimpleFeatureIterator load(String name) throws IOException {
        return load(name, Filter.INCLUDE);
    }

    private SimpleFeatureIterator load(String name, Filter filter) throws IOException {
        URL url = getClass().getResource(name);
        FileDataStore store = FileDataStoreFinder.getDataStore(url);
        return store.getFeatureSource().getFeatures(filter).features();
    }

    public SimpleFeatureIterable switzerland() throws IOException {
        return new SimpleFeatureIterable(new G3g12GeoAttributes(), true)
                .add(load(SWISS));
    }

    public SimpleFeatureIterable cantons() throws IOException {
        return new SimpleFeatureIterable(new G3g12GeoAttributes(), true)
                .add(load(CANTONS));
    }

    public SimpleFeatureIterable districts() throws IOException {
        return new SimpleFeatureIterable(new G3g12GeoAttributes(), true)
                .add(load(DISTRICTS));
    }

    public SimpleFeatureIterable lakes() throws IOException, CQLException {
        return new SimpleFeatureIterable(new G3g12GeoAttributes(), true)
                //.add(load(LAKES))
                //.add(load(LAKES_EU));
                .add(load(LAKES, CQL.toFilter("GMDE>=9000")));
    }

}
