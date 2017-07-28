package net.kineticraft.lostcity.data.reflect.behavior;

import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.reflect.JsonSerializer;
import net.kineticraft.lostcity.guis.data.GUIJsonEditor;
import net.kineticraft.lostcity.item.display.GUIItem;
import org.bukkit.Material;

import java.lang.reflect.Field;
import java.util.function.Consumer;

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
    public void editItem(GUIItem item, Object value, Consumer<Object> setter) {
        Jsonable j = (Jsonable) value;
        if (j != null)
           item.leftClick(ce -> new GUIJsonEditor(ce.getPlayer(), j)).addLoreAction("Left", "Open Value");
        item.setIcon(Material.MOB_SPAWNER);
        setNull(item, value, setter);
    }
}