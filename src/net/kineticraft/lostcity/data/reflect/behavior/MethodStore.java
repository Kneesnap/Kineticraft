package net.kineticraft.lostcity.data.reflect.behavior;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.data.JsonData;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Allows simple storing and loading of values by knowing their methods.
 * @param <T>
 * Created by Kneesnap on 7/4/2017.
 */
public abstract class MethodStore<T> extends DataStore<T> {

    private Method getter;

    public MethodStore(Class<T> apply, String base) {
        this(apply, "set" + base, "get" + base);
    }

    public MethodStore(Class<T> apply, String setMethod, String getMethod) {
        super(apply, setMethod);
        getter = getMethod(getMethod, String.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getField(JsonData data, String key, Field field) {
        try {
            return (T) getter.invoke(data, key);
        } catch (Exception e) {
            e.printStackTrace();
            Core.warn("Failed to get data " + key + ".");
        }
        return null;
    }
}
