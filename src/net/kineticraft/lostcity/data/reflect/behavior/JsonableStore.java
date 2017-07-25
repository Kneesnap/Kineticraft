package net.kineticraft.lostcity.data.reflect.behavior;

import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.reflect.JsonSerializer;
import net.kineticraft.lostcity.guis.staff.GUIJsonEditor;
import net.kineticraft.lostcity.item.display.GUIItem;

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
        return JsonSerializer.fromJson((Class<? extends Jsonable>) field.getType(), data.getObject(key));
    }

    @Override
    public void editItem(GUIItem item, Field f, Jsonable data) throws IllegalAccessException {
        Jsonable j = (Jsonable) f.get(data);
        if (j != null)
           item.leftClick(ce -> new GUIJsonEditor(ce.getPlayer(), j)).addLore("Left-Click: Edit");
    }
}