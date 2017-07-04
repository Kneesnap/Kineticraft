package net.kineticraft.lostcity.data.reflect.behavior;

import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.reflect.JsonSerializer;

import java.lang.reflect.Field;

/**
 * Load / save Json data.
 * Created by Kneesnap on 7/3/2017.
 */
public class JsonableStore extends DataStore<Jsonable> {

    public JsonableStore() {
        super(Jsonable.class, "setElement");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Jsonable getField(JsonData data, String key, Field field) {
        return JsonSerializer.load((Class<? extends Jsonable>) field.getType(), data.getObject(key));
    }
}
