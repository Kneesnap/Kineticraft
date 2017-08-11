package net.kineticraft.lostcity.data.maps;

import lombok.Getter;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.reflect.JsonSerializer;
import net.kineticraft.lostcity.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * JsonMap - Used for storing objects by a key value.
 * Created by Kneesnap on 6/1/2017.
 */
@Getter
public class JsonMap<T> extends SaveableMap<String, T> {

    private Class<T> classType;

    public JsonMap() { // When creating new data.

    }

    public JsonMap(Class<T> cls) {
        this.classType = cls;
    }

    @Override
    protected void save(JsonData data, String key, T value) {
        data.setElement(key, JsonSerializer.addClass(value, getClassType(), JsonSerializer.save(value)));
    }

    @Override
    protected void load(JsonData data, String key) {
        getMap().put(key, JsonSerializer.fromJson(getClassType(), data.getJson(key)));
    }

    /**
     * Convert this Json map to a map indexed by enums.
     * @param enumClass
     * @param <E>
     * @return enumMap
     */
    public <E extends Enum<E>> Map<E, T> toEnumMap(Class<E> enumClass) {
        Map<E, T> map = new HashMap<>();
        forEach((k, v) -> map.put(Utils.getEnum(k, enumClass), get(k)));
        return map;
    }

    /**
     * Get a value by its enum key.
     * @param key
     * @param <E>
     * @return value
     */
    public <E extends Enum<E>> T getValue(E key) {
        return get(key.name());
    }
}
