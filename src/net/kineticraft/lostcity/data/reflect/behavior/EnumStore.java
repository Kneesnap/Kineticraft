package net.kineticraft.lostcity.data.reflect.behavior;

import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.guis.data.GUIEnumPicker;
import net.kineticraft.lostcity.item.display.GUIItem;
import org.bukkit.Material;

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

    @SuppressWarnings("unchecked")
    @Override
    public void editItem(GUIItem item, Field f, Jsonable data) {
        item.leftClick(ce -> new GUIEnumPicker(ce.getPlayer(), (Enum[])f.getType().getEnumConstants(), val -> {
            set(f, data, val);
            ce.getGUI().openPrevious();
        })).setIcon(Material.GOLD_BLOCK).addLoreAction("Left", "Set Value");
    }
}
