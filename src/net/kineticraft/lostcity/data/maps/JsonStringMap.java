package net.kineticraft.lostcity.data.maps;

import net.kineticraft.lostcity.data.JsonData;

/**
 * Json String map
 *
 * Created by Kneesnap on 6/10/2017.
 */
public class JsonStringMap extends SaveableMap<String, String> {

    public JsonStringMap() {

    }

    public JsonStringMap(JsonData data) {
        super(data);
    }

    @Override
    protected void save(JsonData data, String key, String value) {
        data.setString(key, value);
    }

    @Override
    protected void load(JsonData data, String key) {
        getMap().put(key, data.getString(key));
    }
}
