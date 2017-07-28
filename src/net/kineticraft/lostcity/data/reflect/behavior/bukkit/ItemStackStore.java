package net.kineticraft.lostcity.data.reflect.behavior.bukkit;

import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.reflect.JsonSerializer;
import net.kineticraft.lostcity.data.reflect.behavior.SpecialStore;
import net.kineticraft.lostcity.guis.staff.GUIItemEditor;
import net.kineticraft.lostcity.item.display.GUIItem;
import net.kineticraft.lostcity.utils.NBTWrapper;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.function.Consumer;

/**
 * Store an ItemStack
 * Created by Kneesnap on 7/5/2017.
 */
public class ItemStackStore extends SpecialStore<ItemStack> {

    public ItemStackStore() {
        super(ItemStack.class);
    }

    @Override
    public JsonData serialize(ItemStack item) {
        return NBTWrapper.toJson(item);
    }

    @Override
    public ItemStack getField(JsonData data, String key, Field field) {
        return JsonSerializer.fromJson(NBTWrapper.class, data.getJson(key)).getItem();
    }

    @Override
    public void editItem(GUIItem item, Object value, Consumer<Object> setter) {
        ItemStack i = (ItemStack) value;
        item.leftClick(ce -> new GUIItemEditor(ce.getPlayer(), i, iw -> setter.accept(iw.generateItem()))).setIcon(Material.STICK)
                .addLore("Item: " + ChatColor.GOLD + Utils.getItemName(i), "").addLoreAction("Left", "Edit Item");
        setNull(item, value, setter);
    }
}
