package stni.smr;

/**
 *
 */
public final class MapCoord {
    private final int x;
    private final int y;

    private MapCoord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static MapCoord ofXy(int x, int y) {
        return new MapCoord(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MapCoord cityCoord = (MapCoord) o;

        if (x != cityCoord.x) {
            return false;
        }
        if (y != cityCoord.y) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "MapCoord{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
