package net.kineticraft.lostcity.data.maps;

import lombok.Getter;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.reflect.JsonSerializer;

/**
 * JsonMap - Used for storing objects by a key value.
 *
 * Created by Kneesnap on 6/1/2017.
 */
@Getter
public class JsonMap<T extends Jsonable> extends SaveableMap<String, T> {

    private Class<T> classType;

    public JsonMap() { // When creating new data.

    }

    public JsonMap(Class<T> cls) {
        this.classType = cls;
    }

    @Override
    protected void save(JsonData data, String key, T value) {
        data.setElement(key, value.save());
    }

    @Override
    protected void load(JsonData data, String key) {
        getMap().put(key, JsonSerializer.fromJson(getClassType(), data.getObject(key)));
    }
}
