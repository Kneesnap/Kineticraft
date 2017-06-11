package net.kineticraft.lostcity.data.maps;

import lombok.Getter;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.utils.Utils;

/**
 * Allows storing data by an enum index.
 *
 * Created by Kneesnap on 6/7/2017.
 */
@Getter
public class JsonEnumMap<K extends Enum<K>, V extends Jsonable> extends SaveableMap<K, V> {

    private Class<K> enumClass;
    private Class<V> valueClass;

    public JsonEnumMap() {

    }

    public JsonEnumMap(JsonData data,  Class<K> enumClass, Class<V> valueClass) {
        this.enumClass = enumClass;
        this.valueClass = valueClass;
        load(data);
    }

    @Override
    protected void save(JsonData data, K key, V value) {
        data.setElement(key.name(), value.save());
    }

    @Override
    protected void load(JsonData data, String key) {
        if (getEnumClass() != null && getValueClass() != null)
            getMap().put(Utils.getEnum(key, getEnumClass()), Utils.fromJson(getValueClass(), data.getObject(key)));
    }
}
