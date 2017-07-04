package net.kineticraft.lostcity.data.reflect.behavior;

import net.kineticraft.lostcity.data.JsonData;

import java.lang.reflect.Field;

/**
 * Save / load an enum.
 * Created by Kneesnap on 7/3/2017.
 */
public class EnumStore extends DataStore<Enum> {

    public EnumStore() {
        super(Enum.class, "setEnum");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Enum getField(JsonData data, String key, Field field) {
        return data.getEnum(key, (Class<Enum>) field.getType());
    }
}
