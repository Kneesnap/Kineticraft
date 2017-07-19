package net.kineticraft.lostcity.data.reflect.behavior.bukkit;

import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.reflect.JsonSerializer;
import net.kineticraft.lostcity.data.reflect.behavior.SpecialStore;
import net.kineticraft.lostcity.utils.NBTWrapper;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

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
}
