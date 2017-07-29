package net.kineticraft.lostcity.data.reflect.behavior.generic;

import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.reflect.JsonSerializer;
import net.kineticraft.lostcity.data.reflect.behavior.SpecialStore;
import net.kineticraft.lostcity.item.display.GUIItem;

import java.lang.reflect.Field;
import java.util.function.Consumer;

/**
 * Attempt to store a generic object as JSON.
 * Created by Kneesnap on 7/28/2017.
 */
public class ObjectStore extends SpecialStore<Object> {

    public ObjectStore() {
        super(Object.class);
    }

    @Override
    public JsonData serialize(Object value) {
        return new JsonData(JsonSerializer.save(value, true).getAsJsonObject());
    }

    @Override
    public Object getField(JsonData data, String key, Field field) {
        return JsonSerializer.fromJson(field.getType(), data.getJson(key));
    }

    @Override
    protected void editItem(GUIItem item, Object value, Consumer<Object> setter) {

    }
}
