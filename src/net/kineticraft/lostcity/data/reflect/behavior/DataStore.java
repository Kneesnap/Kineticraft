package net.kineticraft.lostcity.data.reflect.behavior;

import lombok.Getter;
import net.kineticraft.lostcity.data.JsonData;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A serializer template.
 *
 * @param <T>
 * Created by Kneesnap on 7/3/2017.
 */
@Getter
public abstract class DataStore<T> {

    private Class<T> applyTo;
    private Method saveMethod;

    public DataStore(Class<T> apply, String setMethod) {
        this.applyTo = apply;
        this.saveMethod = getMethod(setMethod, String.class, setMethod.equals("setNum") ? Double.TYPE : applyTo);
    }

    protected static Method getMethod(String methodName, Class... argTypes) {
        try {
            return JsonData.class.getDeclaredMethod(methodName, argTypes);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Failed to find method " + methodName + ".");
        }
        return null;
    }

    /**
     * Get the value of a field from serialized json.
     * @param data
     * @param key
     * @param field
     * @return loaded
     */
    public abstract T getField(JsonData data, String key, Field field);

    /**
     * Load a field from json.
     * @param data
     * @param to
     * @param field
     */
    public void loadField(JsonData data, Object to, Field field) throws Exception {
        String key = field.getName();
        T value = data.has(key) ? getField(data, key, field) : null;

        if (value != null) // Set the value if there is one.
            field.set(to, value);
    }

    /**
     * Save a field to json.
     * @param data
     * @param value
     * @param key
     */
    public void saveField(JsonData data, Object value, String key) throws Exception {
        getSaveMethod().invoke(data, key, value);
    }
}
