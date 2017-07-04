package net.kineticraft.lostcity.data.reflect.behavior;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.data.JsonData;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Store primitives.
 * Created by Kneesnap on 7/3/2017.
 */
public class PrimitiveStore<T> extends DataStore<T> {

    private Method getter;

    public PrimitiveStore(Class<T> apply, String typeName) {
        this(apply, typeName, "setNum");
    }

    public PrimitiveStore(Class<T> apply, String typeName, String setter) {
        super(apply, setter);
        getter = getMethod(typeName, String.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getField(JsonData data, String key, Field field) {
        try {
            return (T) getter.invoke(data, key);
        } catch (Exception e) {
            e.printStackTrace();
            Core.warn("Failed to get primitive " + key + ".");
        }
        return null;
    }
}
