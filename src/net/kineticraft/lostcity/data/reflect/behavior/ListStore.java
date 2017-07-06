package net.kineticraft.lostcity.data.reflect.behavior;

import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.lists.SaveableList;

import java.lang.reflect.Field;

/**
 * Store / Load list storage.
 * Created by Kneesnap on 7/3/2017.
 */
public class ListStore extends DataStore<SaveableList> {

    public ListStore() {
        super(SaveableList.class, "setList");
    }

    @SuppressWarnings("unchecked")
    @Override
    public SaveableList<?> getField(JsonData data, String key, Field field) {
        return data.getList(key, (Class<? extends SaveableList>) field.getType(), getArgs(field));
    }
}