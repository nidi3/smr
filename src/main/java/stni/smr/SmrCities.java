package stni.smr;

import stni.smr.model.CitySizeType;
import stni.smr.model.CityType;
import stni.smr.model.RRTCities;

import java.io.File;

/**
 *
 */
public class SmrCities extends JaxbSmrPart<RRTCities> {
    public SmrCities(Smr smr, File dir) {
        super(smr, new File(dir, "rrt_cities_" + dir.getName() + ".xml"));
    }

    public void removeAllCities() {
        smr.getNames().removeAllCityNames();
        data.getCity().clear();
    }

    public void removeCity(String name) {
        final String key = smr.getNames().removeCityName(name);
        CityType city = findCity(key);
        if (city != null) {
            data.getCity().remove(city);
        }
    }

    public void addCity(String name, CitySizeType size, boolean isStart, CityCoord coord) {
        final String key = smr.getNames().addCityName(name);
        CityType city = findCity(key);
        if (city == null) {
            city = new CityType();
            city.setSzName(key);
            data.getCity().add(city);
        }
        city.setType(size);
        city.setBIsStartLocation(isStart);
        city.setStartX(coord.getX());
        city.setStartY(coord.getY());
        city.setRotation(0);
        city.setSzDefaultTrackSide("SOUTH");
    }

    private CityType findCity(String key) {
        for (CityType city : data.getCity()) {
            if (city.getSzName().equals(key)) {
                return city;
            }
        }
        return null;
    }
}
