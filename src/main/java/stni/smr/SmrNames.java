package stni.smr;

import stni.smr.model.Names;
import stni.smr.model.TextKeyType;

import java.io.File;
import java.util.*;

/**
 *
 */
public class SmrNames extends JaxbSmrPart<Names> {

    private static final String CITY_KEY_PREFIX = "TAG_CITY_NAME_";

    public SmrNames(Smr smr, File dir) {
        super(smr, new File(dir, "rrt_names_" + dir.getName() + ".xml"));
    }

    @Override
    public void save() {
        deduplicate();
        super.save();
    }

    public String addCityName(String name) {
        final TextKeyType entry = new TextKeyType();
        final String key = cityKey(name);
        entry.setTag(key);
        entry.setText(name);
        entry.setGender("M");
        data.getRRTCities().getTextKey().add(entry);
        return key;
    }

    public String removeCityName(String name) {
        final String key = cityKey(name);
        removeCityKey(key);
        return key;
    }

    public void removeAllCityNames() {
        for (Iterator<TextKeyType> iter = data.getRRTCities().getTextKey().iterator(); iter.hasNext(); ) {
            TextKeyType entry = iter.next();
            if (entry.getTag().startsWith(CITY_KEY_PREFIX)) {
                iter.remove();
            }
        }
    }

    public void removeCityKey(String key) {
        for (Iterator<TextKeyType> iter = data.getRRTCities().getTextKey().iterator(); iter.hasNext(); ) {
            TextKeyType entry = iter.next();
            if (entry.getTag().equals(key)) {
                iter.remove();
            }
        }
    }

    private String cityKey(String name) {
        return CITY_KEY_PREFIX + name.toUpperCase().replace(' ', '_');
    }

    private void deduplicate() {
        final Set<TextKeyType> keySet = new TreeSet<>(new Comparator<TextKeyType>() {
            @Override
            public int compare(TextKeyType o1, TextKeyType o2) {
                return o1.getTag().compareTo(o2.getTag());
            }
        });
        final List<TextKeyType> keyList = data.getRRTCities().getTextKey();
        keySet.addAll(keyList);
        keyList.clear();
        keyList.addAll(keySet);
    }

}
