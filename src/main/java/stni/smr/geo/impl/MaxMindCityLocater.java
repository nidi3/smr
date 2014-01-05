package stni.smr.geo.impl;

import stni.smr.geo.CityLocater;
import stni.smr.geo.CityLocation;
import stni.smr.geo.WorldCoord;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class MaxMindCityLocater implements CityLocater {
    private final Map<String, Map<String, CityLocation>> locations = new HashMap<>();

    public MaxMindCityLocater(InputStream data, String... countries) throws IOException {
        this(copy(data, System.getProperty("java.io.tmpdir")), countries);
    }

    public MaxMindCityLocater(File data, String... countries) throws IOException {
        try (final RandomAccessFile raf = new RandomAccessFile(data, "r")) {
            for (String country : countries) {
                locations.put(country, new HashMap<String, CityLocation>());
                String first = locateCountry(raf, country);
                readLines(raf, country, first);
            }
        }
    }

    private static File copy(InputStream in, String destDir) throws IOException {
        File f = new File(destDir, "worldcitiespop.txt");
        if (!f.exists()) {
            byte[] buf = new byte[1000000];
            try (BufferedInputStream bis = new BufferedInputStream(in);
                 BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f))) {
                int read;
                while ((read = bis.read(buf)) > 0) {
                    bos.write(buf, 0, read);
                }
            }
        }
        return f;
    }

    private String locateCountry(RandomAccessFile raf, String country) throws IOException {
        long pos = Long.highestOneBit(raf.length());
        long step = pos >> 1;
        String a, b;
        do {
            raf.seek(pos);
            raf.readLine();
            a = raf.readLine();
            b = raf.readLine();
            if (a.compareTo(country) < 0) {
                pos += step;
            } else {
                pos -= step;
            }
            step >>= 1;
        } while (step > 0 && (a.startsWith(country) || !b.startsWith(country)));
        return b;
    }

    private void readLines(RandomAccessFile raf, String country, String first) throws IOException {
        String line = first;
        final Map<String, CityLocation> countryLocations = locations.get(country);
        do {
            String[] parts = line.split(",");
            final CityLocation cityLocation = countryLocations.get(parts[1]);
            if (cityLocation == null || cityLocation.getPopulation() == 0) {
                countryLocations.put(parts[1], new CityLocation(parts[0], parts[2],
                        parts[4].length() == 0 ? 0 : Integer.parseInt(parts[4]),
                        WorldCoord.ofLatLng(Double.parseDouble(parts[5]), Double.parseDouble(parts[6]))));
            }
            line = raf.readLine();
        } while (line.startsWith(country));
    }

    @Override
    public CityLocation locateCity(String country, String name) {
        return locations.get(country).get(name);
    }
}
