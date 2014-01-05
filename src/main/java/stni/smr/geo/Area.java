package stni.smr.geo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Area {
    private final String id;
    private final String name;
    private final List<double[]> coordinates;

    public Area(String id, String name, List<double[]> coordinates) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
    }

    public Area(String id, String name) {
        this(id, name, new ArrayList<double[]>());
    }

    public void addCoordinates(double[] coordinates) {
        this.coordinates.add(coordinates);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<double[]> getCoordinates() {
        return coordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        Area area = (Area) o;

        if (!coordinates.equals(area.coordinates)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + coordinates.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Area{" +
                "name=" + name +
                "}";
    }
}