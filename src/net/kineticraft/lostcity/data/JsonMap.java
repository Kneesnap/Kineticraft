package net.kineticraft.lostcity.data;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * JsonMap - Used for storing objects by a key value.
 *
 * Created by Kneesnap on 6/1/2017.
 */
@Getter
public class JsonMap<T extends Jsonable> implements Jsonable {

    private Map<String, T> map = new HashMap<>();
    private Class<T> classType;

    public JsonMap() { // When creating new data.

    }

    public JsonMap(JsonData obj) {
        this(obj, null); // Null is ok only when we aren't loading data.
    }

    public JsonMap(JsonData object, Class<T> cls) {
        this.classType = cls;
        load(object);
    }

    /**
     * Get the element with the specified key.
     * @param key
     * @return
     */
    public T get(String key) {
        return getMap().get(key);
    }

    /**
     * Does this map contain the listed key?
     * @param s
     * @return
     */
    public boolean containsKey(String s) {
        return getMap().containsKey(s);
    }

    /**
     * Get a list of keys in this set.
     * @return
     */
    public Set<String> keySet() {
        return getMap().keySet();
    }

    /**
     * Remove an element from this object.
     * @param key
     * @return
     */
    public T remove(String key) {
        return getMap().remove(key);
    }

    /**
     * Set a value in the json map.
     * @param key
     * @param value
     */
    public void put(String key, T value) {
        getMap().put(key, value);
    }

    /**
     * Return the number of elements in this map.
     * @return
     */
    public int size() {
        return keySet().size();
    }

    @Override
    public void load(JsonData data) {
        data.keySet().forEach(key -> getMap().put(key, JsonUtil.fromJson(getClassType(), data.getObject(key))));
    }

    @Override
    public JsonData save() {
        JsonData data = new JsonData();
        for (String key : keySet())
        data.setElement(key, get(key).save().getJsonObject());
        return data;
    }
}
