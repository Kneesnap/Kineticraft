package net.kineticraft.lostcity.data;

import com.google.gson.*;
import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.data.lists.JsonList;
import net.kineticraft.lostcity.data.lists.SaveableList;
import net.kineticraft.lostcity.data.maps.SaveableMap;
import net.kineticraft.lostcity.data.reflect.JsonSerializer;
import net.kineticraft.lostcity.utils.ReflectionUtil;
import net.kineticraft.lostcity.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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

    public JsonData(Jsonable jsonable) {
        this(jsonable.save().getAsJsonObject());
    }

    /**
     * Gets the JsonElement with the specified key
     * @param key
     * @return element
     */
    private JsonElement get(String key) {
        return getJsonObject().get(key);
    }

    /**
     * Does this data have the given key?
     * @param key
     * @return hasKey
     */
    public boolean has(String key) {
        JsonElement element = get(key);
        return element != null && !element.isJsonNull();
    }

    /**
     * Remove the given key.
     * @param key
     * @return this
     */
    public JsonData remove(String key) {
        getJsonObject().remove(key);
        return this;
    }

    /**
     * Load a List from json
     * @param key
     * @param type
     * @param args
     * @param <T>
     * @return jsonList
     */
    public <T extends SaveableList> T getList(String key, Class<T> type, Object... args) {
        return JsonSerializer.fromJson(type, getArray(key), args);
    }

    /**
     * Save a JSON list.
     * @param key
     * @param list
     * @return this
     */
    public JsonData setList(String key, SaveableList<?> list) {
        if (list == null || list.isEmpty())
            return remove(key);
        return setElement(key, list.save());
    }

    /**
     * Load a Map from json.
     * @param key
     * @param args
     * @param <T>
     * @return map
     */
    @SuppressWarnings("unchecked")
    public <T extends SaveableMap> T getMap(String key, Class<T> type, Object... args) {
        return JsonSerializer.fromJson(type, getObject(key), args);
    }

    /**
     * Load a JsonArray
     * @param key
     * @return array
     */
    public JsonArray getArray(String key) {
        return has(key) ? get(key).getAsJsonArray() : new JsonArray();
    }

    /**
     * Store JSONable data.
     * @param key
     * @param jsonable
     * @return this
     */
    public JsonData setElement(String key, Jsonable jsonable) {
        return jsonable == null ? remove(key) : setElement(key, jsonable.save());
    }

    /**
     * Store JSONData by a key.
     * @param key
     * @param data
     * @return this
     */
    public JsonData setElement(String key, JsonData data) {
        return data == null ? remove(key) : setElement(key, data.getJsonObject());
    }

    /**
     * Set a JsonElement.
     * @param key
     * @param val
     * @return this
     */
    public JsonData setElement(String key, JsonElement val) {
        if (val == null || val.isJsonNull())
            return remove(key);
        getJsonObject().add(key, val);
        return this;
    }

    /**
     * Loads a boolean value.
     * @param key
     * @return boolean
     */
    public boolean getBoolean(String key) {
        return has(key) && get(key).getAsBoolean();
    }

    /**
     * Get a Json Object.
     * @param key
     * @return object
     */
    public JsonObject getObject(String key) {
        return has(key) ? getJsonObject().get(key).getAsJsonObject() : new JsonObject();
    }

    /**
     * Load a double value.
     * @param key
     * @return double
     */
    public double getDouble(String key) {
        return getDouble(key, 0D);
    }

    /**
     * Load a double value. Defaults to the fallback.
     * @param key
     * @param fallback
     * @return double
     */
    public double getDouble(String key, double fallback) {
        return has(key) ? get(key).getAsDouble() : fallback;
    }

    /**
     * Load a float value.
     * @param key
     * @return float
     */
    public float getFloat(String key) {
        return getFloat(key, 0F);
    }

    /**
     * Load a float value. Defaults to the fallback.
     * @param key
     * @param fallback
     * @return float
     */
    public float getFloat(String key, float fallback) {
        return has(key) ? get(key).getAsFloat() : fallback;
    }

    /**
     * Load an integer value.
     * @param key
     * @return int
     */
    public int getInt(String key) {
        return getInt(key, 0);
    }

    /**
     * Loads an integer value. Defaults to the fallback.
     * @param key
     * @param fallback
     * @return int
     */
    public int getInt(String key, int fallback) {
        return has(key) ? get(key).getAsInt() : fallback;
    }

    /**
     * Load a byte value.
     * @param key
     * @return byte
     */
    public byte getByte(String key) {
        return getByte(key, (byte) 0);
    }

    /**
     *
     * @param key
     * @return
     */
    public byte getByte(String key, byte fallback) {
        return has(key) ? get(key).getAsByte() : fallback;
    }

    /**
     * Load a short value.
     * @param key
     * @return short
     */
    public short getShort(String key) {
        return getShort(key, (short) 0);
    }

    /**
     * Load a short value.  Defaults to the fallback.
     * @param key
     * @param fallback
     * @return short
     */
    public short getShort(String key, short fallback) {
        return has(key) ? get(key).getAsShort() : fallback;
    }

    /**
     * Load a long value.
     * @param key
     * @return long
     */
    public long getLong(String key) {
        return getLong(key, 0L);
    }

    /**
     * Load a long value. Defaults to fallback.
     * @param key
     * @param fallback
     * @return long
     */
    public long getLong(String key, long fallback) {
        return has(key) ? get(key).getAsLong() : fallback;
    }

    /**
     * Store a number value.
     * @param key
     * @param value
     * @return this
     */
    public JsonData setNum(String key, Number value) {
        getJsonObject().addProperty(key, value);
        return this;
    }

    /**
     * Store a boolean value.
     * @param key
     * @param value
     * @return this
     */
    public JsonData setBoolean(String key, boolean value) {
        getJsonObject().addProperty(key, value);
        return this;
    }

    /**
     * Return the set of keys that make up this object.
     * @return keys
     */
    public Set<String> keySet() {
        return getJsonObject().entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    /**
     * Gets a stored string value.
     * @param key
     * @return string
     */
    public String getString(String key) {
        return getString(key, null);
    }

    /**
     * Loads a stored string. Defaults to fallback.
     * @param key
     * @param fallback
     * @return string
     */
    public String getString(String key, String fallback) {
        return has(key) ? get(key).getAsString() : fallback;
    }

    /**
     * Sets a string value.
     * @param key
     * @param value
     * @return this
     */
    public JsonData setString(String key, String value) {
        remove(key);
        getJsonObject().addProperty(key, value);
        return this;
    }

    /**
     * Store an enum value.
     * @param key
     * @param e
     * @return this
     */
    public JsonData setEnum(String key, Enum<?> e) {
        setString(key, e != null ? e.name() : null);
        return this;
    }

    /**
     * Gets an enum value from the given class. Returns null if not found.
     * @param key
     * @param clazz
     * @return enumValue
     */
    public <T extends Enum<T>> T getEnum(String key, Class<T> clazz) {
        return getEnum(key, clazz, null);
    }

    /**
     * Gets an enum value, falling back on a default value.
     * @param key
     * @param defaultValue
     * @return enumValue
     */
    public <T extends Enum<T>> T getEnum(String key, T defaultValue) {
        return getEnum(key, (Class<T>) defaultValue.getClass(), defaultValue);
    }

    private <T extends Enum<T>> T getEnum(String key, Class<T> clazz, T defaultValue) {
        return Utils.getEnum(getString(key), clazz, defaultValue);
    }

    /**
     * Load saved JsonData
     * @param key
     * @return this
     */
    public JsonData getData(String key) {
        return new JsonData(getObject(key));
    }

    public JsonElement getJson(String key) {
        return get(key);
    }

    /**
     * Loads a UUID from stored data. Defaults to null.
     * @param key
     * @return uuid
     */
    public UUID getUUID(String key) {
        return has(key) ? UUID.fromString(getString(key)) : null;
    }

    /**
     * Store a uuid value.
     * @param key
     * @param uuid
     * @return data
     */
    public JsonData setUUID(String key, UUID uuid) {
        return setString(key, uuid != null ? uuid.toString() : null);
    }

    /**
     * Load JSON from a file.
     * @param path
     * @return jsonData
     */
    public static JsonData fromFile(String path) {
        return fromFile(getFile(path));
    }

    /**
     * Load JSON from a file.
     * @param file
     * @return jsonData.
     */
    public static JsonData fromFile(File file) {
        if (!file.exists())
            return new JsonData();

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
     * @param path
     */
    public void toFile(String path) {
        File file = getFile(path);

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
     * Is this file a valid json file?
     * @param path
     * @return isJson
     */
    public static boolean isJson(String path) {
        return getFile(path).exists();
    }

    private static File getFile(String name) {
        return Core.getFile(name + ".json");
    }

    /**
     * Convert this object into a pretty json string.
     * @return formattedJson
     */
    public String toPrettyJson() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(getJsonObject());
    }

    @Override
    public String toString() {
        return getJsonObject().toString();
    }
}