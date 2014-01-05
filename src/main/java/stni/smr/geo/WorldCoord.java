package stni.smr.geo;

/**
 *
 */
public final class WorldCoord {
    private static final double RADIUS = 6371;
    private static final double TO_RAD = Math.PI / 180;

    private final double lat;
    private final double lng;

    private WorldCoord(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public static WorldCoord ofLatLng(double lat, double lng) {
        return new WorldCoord(lat, lng);
    }

    public static WorldCoord ofSwissCoord(double x, double y) {
        double yAux = x / 1000000 - .6;
        double xAux = y / 1000000 - .2;

        double lat = (16.9023892 + (3.238272 * xAux))
                - (0.270978 * Math.pow(yAux, 2))
                - (0.002528 * Math.pow(xAux, 2))
                - (0.0447 * Math.pow(yAux, 2) * xAux)
                - (0.0140 * Math.pow(xAux, 3));
        double lng = (2.6779094 + (4.728982 * yAux)
                + (0.791484 * yAux * xAux)
                + (0.1306 * yAux * Math.pow(xAux, 2)))
                - (0.0436 * Math.pow(yAux, 3));
        return ofLatLng(lat * 100 / 36, lng * 100 / 36);
    }

    public double distance(WorldCoord coord) {
        final double thisLat = lat * TO_RAD;
        final double thatLat = coord.lat * TO_RAD;
        return RADIUS * Math.acos(
                Math.sin(thisLat) * Math.sin(thatLat) +
                        Math.cos(thisLat) * Math.cos(thatLat) * Math.cos((coord.lng - lng) * TO_RAD));
    }

    public WorldCoord withLat(double v) {
        return ofLatLng(v, lng);
    }

    public WorldCoord withLng(double v) {
        return ofLatLng(lat, v);
    }

    public WorldCoord withLng(WorldCoord c) {
        return ofLatLng(lat, c.lng);
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public boolean equals(WorldCoord worldCoord, double delta) {
        return worldCoord != null && Math.abs(lat - worldCoord.lat) < delta && Math.abs(lng - worldCoord.lng) < delta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WorldCoord that = (WorldCoord) o;

        if (Double.compare(that.lat, lat) != 0) {
            return false;
        }
        if (Double.compare(that.lng, lng) != 0) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(lat);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lng);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return lat + "," + lng;
    }

}
