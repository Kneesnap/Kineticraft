package net.kineticraft.lostcity.data.reflect.behavior;

import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.guis.data.GUIEnumPicker;
import net.kineticraft.lostcity.item.display.GUIItem;
import org.bukkit.Material;

import java.lang.reflect.Field;
import java.util.function.Consumer;

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

    @Override
    protected void editItem(GUIItem item, Object value, Consumer<Object> setter) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void editItem(GUIItem item, Object value, Consumer<Object> setter, Class<?> type) {
        item.leftClick(ce -> new GUIEnumPicker(ce.getPlayer(), (Enum[]) type.getEnumConstants(), setter::accept))
                .setIcon(Material.GOLD_BLOCK).addLoreAction("Left", "Set Value");
    }
}
