package stni.smr.geo;

/**
 *
 */
public class CityLocation {
    private final String country;
    private final String name;
    private final int population;
    private final WorldCoord coord;

    public CityLocation(String country, String name, int population, WorldCoord coord) {
        this.country = country;
        this.name = name;
        this.population = population;
        this.coord = coord;
    }

    public String getCountry() {
        return country;
    }

    public String getName() {
        return name;
    }

    public int getPopulation() {
        return population;
    }

    public WorldCoord getCoord() {
        return coord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CityLocation that = (CityLocation) o;

        if (population != that.population) {
            return false;
        }
        if (!coord.equals(that.coord)) {
            return false;
        }
        if (!country.equals(that.country)) {
            return false;
        }
        if (!name.equals(that.name)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = country.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + population;
        result = 31 * result + coord.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CityLocation{" +
                "country='" + country + '\'' +
                ", name='" + name + '\'' +
                ", population=" + population +
                ", coord=" + coord +
                '}';
    }
}
