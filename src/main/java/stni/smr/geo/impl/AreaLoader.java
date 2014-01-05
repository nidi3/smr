package stni.smr.geo.impl;

import org.opengis.feature.simple.SimpleFeature;
import stni.smr.geo.Area;

/**
*
*/
interface AreaLoader {
    Area load(SimpleFeature feature);
}
