package net.kineticraft.lostcity.data;

import com.google.gson.*;
import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A Wrapper around JsonObject.
 *
 * Created by Kneesnap on 5/29/2017.
 */
@Getter
public class JsonData {

    private JsonObject jsonObject;

    public JsonData() {
        this(new JsonObject());
    }

    public JsonData(JsonObject object) {
        this.jsonObject = object;
    }

    /**
     * Gets the JsonElement with the specified key
     * @param key
     */
    private JsonElement get(String key) {
        return getJsonObject().get(key);
    }

    /**
     * Does this data have the given key?
     * @param key
     */
    public boolean has(String key) {
        return get(key) != null;
    }

    /**
     * Remove the given key.
     * @param key
     */
    public JsonData remove(String key) {
        getJsonObject().remove(key);
        return this;
    }

    /**
     * Load a Json List.
     * @param key
     * @param type
     */
    public <T extends Jsonable> JsonList<T> getList(String key, Class<T> type) {
        return JsonList.fromJson(getArray(key), type);
    }

    /**
     * Load a Json Map.
     * @param key
     * @param type
     * @return
     */
    public <T extends Jsonable> JsonMap<T> getMap(String key, Class<T> type) {
        return new JsonMap<T>(new JsonData(getObject(key)), type);
    }

    /**
     * Load a JsonArray
     * @param key
     * @return
     */
    public JsonArray getArray(String key) {
        return has(key) ? get(key).getAsJsonArray() : new JsonArray();
    }

    /**
     * Set a JsonElement.
     * @param key
     * @param val
     * @return
     */
    public JsonData setElement(String key, JsonElement val) {
        if (val == null || val.isJsonNull())
            return remove(key);
        getJsonObject().add(key, val);
        return this;
    }

    /**
     * Get a Json Element.
     * @param key
     * @return
     */
    public JsonObject getObject(String key) {
        return has(key) ? getJsonObject().get(key).getAsJsonObject() : new JsonObject();
    }

    /**
     * Load a double value.
     * @param key
     * @return
     */
    public double getDouble(String key) {
        return has(key) ? get(key).getAsDouble() : 0D;
    }

    /**
     * Load a float value.
     * @param key
     * @return
     */
    public float getFloat(String key) {
        return has(key) ? get(key).getAsFloat() : 0F;
    }

    /**
     * Load an integer value.
     * @param key
     * @return
     */
    public int getInt(String key) {
        return has(key) ? get(key).getAsInt() : 0;
    }

    /**
     * Store a number value.
     * @param key
     * @param value
     * @return
     */
    public JsonData setNum(String key, Number value) {
        if (value.doubleValue() == 0D)
            return remove(key);
        getJsonObject().addProperty(key, value);
        return this;
    }

    /**
     * Return the set of keys that make up this object.
     * @return
     */
    public Set<String> keySet() {
        return getJsonObject().entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    /**
     * Gets a stored string value.
     * @param key
     * @return
     */
    public String getString(String key) {
        return has(key) ? get(key).getAsString() : null;
    }

    /**
     * Sets a string value.
     * @param key
     * @param value
     */
    public JsonData setString(String key, String value) {
        if (value == null)
            return remove(key);
        getJsonObject().addProperty(key, value);
        return this;
    }

    /**
     * Store an enum value.
     * @param key
     * @param e
     */
    public JsonData setEnum(String key, Enum<?> e) {
        setString(key, e.name());
        return this;
    }

    /**
     * Gets an enum value from the given class. Returns null if not found.
     * @param key
     * @param clazz
     */
    public <T extends Enum<T>> T getEnum(String key, Class<T> clazz) {
        return getEnum(key, clazz, null);
    }

    /**
     * Gets an enum value, falling back on a default value.
     * @param key
     * @param defaultValue
     */
    public <T extends Enum<T>> T getEnum(String key, T defaultValue) {
        return getEnum(key, (Class<T>) defaultValue.getClass(), defaultValue);
    }

    private <T extends Enum<T>> T getEnum(String key, Class<T> clazz, T defaultValue) {
        return Utils.getEnum(getString(key), clazz, defaultValue);
    }

    /**
     * Load JSON from a file.
     * @param file
     * @return
     */
    public static JsonData fromFile(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            JsonData data = new JsonData(new JsonParser().parse(br).getAsJsonObject());
            br.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            Core.warn("Failed to load json '" + file.getName() + ".");
            return null;
        }
    }

    /**
     * Save this Json Object to a file.
     * @param file
     */
    public void toFile(File file) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(toPrettyJson());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            Core.warn("Failed to save '" + file.getName() + "'.");
        }
    }

    /**
     * Convert this object into a pretty json string.
     * @return
     */
    public String toPrettyJson() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(getJsonObject());
    }

    @Override
    public String toString() {
        return getJsonObject().toString();
    }
}
