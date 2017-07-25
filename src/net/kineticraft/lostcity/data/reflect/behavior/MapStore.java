package net.kineticraft.lostcity.data.reflect.behavior;

import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.maps.SaveableMap;
import net.kineticraft.lostcity.guis.staff.GUIJsonEditor;
import net.kineticraft.lostcity.item.display.GUIItem;

import java.lang.reflect.Field;

/**
 * Handles dictionary saving / loading
 *
 * Created by Kneesnap on 7/4/2017.
 */
public class MapStore extends DataStore<SaveableMap> {

    public MapStore() {
        super(SaveableMap.class, "setElement");
    }

    @Override
    protected Class<Jsonable> getSaveArgument() {
        return Jsonable.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SaveableMap getField(JsonData data, String key, Field field) {
        return data.getMap(key, (Class<? extends SaveableMap>) field.getType(), getArgs(field));
    }

    @Override
    public void editItem(GUIItem item, Field f, Jsonable data) throws IllegalAccessException {
        Jsonable j = (Jsonable) f.get(data);
        if (j != null)
            item.leftClick(ce -> new GUIJsonEditor(ce.getPlayer(), j)).addLore("Left-Click: View");
    }
}
