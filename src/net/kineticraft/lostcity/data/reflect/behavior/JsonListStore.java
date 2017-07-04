package net.kineticraft.lostcity.data.reflect.behavior;

import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.lists.JsonList;
import net.kineticraft.lostcity.utils.ReflectionUtil;

import java.lang.reflect.Field;

/**
 * Load / store JsonLists.
 * Created by Kneesnap on 7/3/2017.
 */
public class JsonListStore extends DataStore<JsonList> {

    public JsonListStore() {
        super(JsonList.class, "setList");
    }

    @SuppressWarnings("unchecked")
    @Override
    public JsonList getField(JsonData data, String key, Field field) {
        return data.getJsonList(key, (Class<? extends Jsonable>) ReflectionUtil.getGenericType(field));
    }
}
