package stni.smr;

import stni.smr.geo.WorldCoord;

/**
 *
 */
public class Coords {
    private final WorldCoord northEast;
    private final WorldCoord southWest;
    private final double dLat, dLng;
    private final int yBase, dy;
    private final int xBase, dx;
    private final int pixelSize;

    Coords(int pixelSize, int unitSize, WorldCoord northEast, WorldCoord southWest) {
        this.northEast = northEast;
        this.southWest = southWest;
        this.pixelSize = pixelSize;
        dLat = (southWest.getLat() - northEast.getLat());
        dLng = (southWest.getLng() - northEast.getLng());
        yBase = 145 - 37 * unitSize; //10: -225
        dy = 1 + 75 * unitSize;   //10:751

        xBase = 145 - 37 * unitSize; //10: -225
        dx = 1 + 75 * unitSize;   //10: 751
    }

    public WorldCoord convertMapToWorld(int x, int y) {
        final double yAmount = (double) y / pixelSize;
        final double xAmount = (double) x / pixelSize;
        return WorldCoord.ofLatLng(northEast.getLat() + dLat * yAmount, northEast.getLng() + dLng * xAmount);
    }

    public MapCoord convertWorldToMap(WorldCoord coord) {
        final double latAmount = (coord.getLat() - northEast.getLat()) / dLat;
        final double lngAmount = (coord.getLng() - northEast.getLng()) / dLng;
        return MapCoord.ofXy((int) ((pixelSize - 1) * lngAmount), (int) ((pixelSize - 1) * latAmount));
    }

    public CityCoord convertWorldToCity(WorldCoord coord) {
        final double latAmount = (coord.getLat() - northEast.getLat()) / dLat;
        final double lngAmount = (coord.getLng() - northEast.getLng()) / dLng;
        return CityCoord.ofXy((int) (xBase + dx * lngAmount), (int) (yBase + dy * latAmount));
    }

    public boolean isVisible(WorldCoord coord) {
        final MapCoord mapCoord = convertWorldToMap(coord);
        return (mapCoord.getX() > 100 && mapCoord.getX() < pixelSize - 100 &&
                mapCoord.getY() > 50 && mapCoord.getY() < pixelSize - 100);
    }

    public WorldCoord center() {
        return WorldCoord.ofLatLng((southWest.getLat() + northEast.getLat()) / 2, (southWest.getLng() + northEast.getLng()) / 2);
    }

    public WorldCoord northEast() {
        return northEast;
    }

    public WorldCoord southWest() {
        return southWest;
    }
}
