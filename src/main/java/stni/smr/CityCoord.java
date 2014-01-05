package stni.smr;

/**
 *
 */
public final class CityCoord {
    private final int x;
    private final int y;

    private CityCoord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static CityCoord ofXy(int x, int y) {
        return new CityCoord(x, y);
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

        CityCoord cityCoord = (CityCoord) o;

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
        return "CityCoord{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
