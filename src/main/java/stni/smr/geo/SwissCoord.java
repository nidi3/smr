package stni.smr.geo;

/**
 *
 */
public final class SwissCoord {
    private final double x;
    private final double y;

    private SwissCoord(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static SwissCoord ofXy(double x, double y) {
        return new SwissCoord(x, y);
    }

    public static SwissCoord ofLatLng(double lat, double lng) {
        double latAux = sexAngleToSeconds(decToSexAngle(lat));
        double lngAux = sexAngleToSeconds(decToSexAngle(lng));

        latAux = (latAux - 169028.66) / 10000;
        lngAux = (lngAux - 26782.5) / 10000;

        double y = ((200147.07 + (308807.95 * latAux)
                + (3745.25 * Math.pow(lngAux, 2))
                + (76.63 * Math.pow(latAux, 2)))
                - (194.56 * Math.pow(lngAux, 2) * latAux))
                + (119.79 * Math.pow(latAux, 3));

        double x = (600072.37 + (211455.93 * lngAux))
                - (10938.51 * lngAux * latAux)
                - (0.36 * lngAux * Math.pow(latAux, 2))
                - (44.54 * Math.pow(lngAux, 3));

        return ofXy(x, y);
    }

    public static SwissCoord ofWorldCoord(WorldCoord worldCoord) {
        if (worldCoord != null) {
            return ofLatLng(worldCoord.getLat(), worldCoord.getLng());
        }
        return null;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    private static double decToSexAngle(double dec) {
        int deg = (int) Math.floor(dec);
        int min = (int) Math.floor((dec - deg) * 60);
        double sec = (((dec - deg) * 60) - min) * 60;
        return deg + ((double) min / 100) + (sec / 10000);
    }

    private static double sexAngleToSeconds(double dms) {
        double deg = Math.floor(dms);
        double min = Math.floor((dms - deg) * 100);
        double sec = (((dms - deg) * 100) - min) * 100;
        return sec + (min * 60) + (deg * 3600);
    }

    public boolean equals(SwissCoord swissCoord, double delta) {
        return swissCoord != null && Math.abs(x - swissCoord.x) < delta && Math.abs(y - swissCoord.y) < delta;
    }

    @Override
    public String toString() {
        return "SwissCoord{" +
                "x=" + x +
                ", y=" + y +
                "} " + super.toString();
    }
}
