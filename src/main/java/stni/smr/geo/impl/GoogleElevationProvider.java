package stni.smr.geo.impl;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import stni.smr.geo.ElevationProvider;
import stni.smr.geo.WorldCoord;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URI;

/**
 *
 */
public class GoogleElevationProvider extends AbstractXmlHttpService implements ElevationProvider {


    @Override
    public double[][] getElevation(int w, int h, WorldCoord northEast, WorldCoord southWest) throws IOException {
        try {
            double[][] res = new double[w][h];
            final double dLat = (southWest.getLat() - northEast.getLat()) / (h - 1);
            for (int y = 0; y < h; y++) {
                final WorldCoord currentEast = northEast.withLat(northEast.getLat() + dLat * y);
                final InputSource inputSource = sourceFromHttpGet(URI.create("http://maps.googleapis.com/maps/api/elevation/xml?sensor=false&path=" + currentEast + "%7C" + currentEast.withLng(southWest.getLng()) + "&samples=" + w));
                NodeList elevations = (NodeList) xPath().compile("//elevation").evaluate(inputSource, XPathConstants.NODESET);
                for (int i = 0; i < elevations.getLength(); i++) {
                    res[i][y] = (int) Double.parseDouble(elevations.item(i).getTextContent());
                }
            }
            return res;
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }
}