package net.kineticraft.lostcity.data.reflect.behavior;

import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.guis.staff.GUIItemPicker;
import net.kineticraft.lostcity.item.ItemManager;
import net.kineticraft.lostcity.item.display.GUIItem;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
    public void editItem(GUIItem item, Field f, Jsonable data) throws IllegalAccessException {

        List<ItemStack> items = new ArrayList<>();
        for (Enum e : (Enum[]) f.getType().getEnumConstants())
            items.add(ItemManager.createItem(Material.LAPIS_BLOCK, ChatColor.YELLOW + e.name()));

        item.leftClick(ce -> new GUIItemPicker(ce.getPlayer(), items, i -> {
            try {
                f.set(data, Utils.getEnum(ChatColor.stripColor(Utils.getItemName(i)), (Class<? extends Enum>) f.getType()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }));
    }
}
