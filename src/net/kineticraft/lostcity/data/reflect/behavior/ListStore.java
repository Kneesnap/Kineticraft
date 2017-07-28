package net.kineticraft.lostcity.data.reflect.behavior;

import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.lists.SaveableList;
import net.kineticraft.lostcity.guis.data.GUIListEditor;
import net.kineticraft.lostcity.item.display.GUIItem;
import org.bukkit.Material;

import java.lang.reflect.Field;
import java.util.function.Consumer;

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

    @SuppressWarnings("unchecked")
    @Override
    public void editItem(GUIItem item, Object value, Consumer<Object> setter) {
        item.leftClick(ce -> new GUIListEditor<>(ce.getPlayer(), (SaveableList<?>) value))
                .setIcon(Material.MINECART).addLoreAction("Left", "Edit Values");
    }
}