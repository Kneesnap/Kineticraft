package net.kineticraft.lostcity.data.reflect.behavior;

import net.kineticraft.lostcity.data.JsonData;

import java.lang.reflect.Field;

/**
 * All wrappers that have hardcoded handles should extend this class.
 * Created by Kneesnap on 7/5/2017.
 */
public abstract class SpecialStore<T> extends DataStore<T> {

    public SpecialStore(Class<T> apply) {
        super(apply);
    }

    @Override
    public Class<?> getSaveArgument() {
        return JsonData.class;
    }

    @Override
    public abstract JsonData serialize(T value);

    @Override
    public abstract T getField(JsonData data, String key, Field field);
}
