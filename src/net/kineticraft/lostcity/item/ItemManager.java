package net.kineticraft.lostcity.item;

import net.kineticraft.lostcity.item.guis.GenericItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages Item Functions.
 *
 * Created by Kneesnap on 6/2/2017.
 */
public class ItemManager {

    /**
     * Load an item object from an ItemStack.
     * @param itemStack
     * @return
     */
    public static ItemWrapper constructItem(ItemStack itemStack) {
        ItemType itemType = ItemWrapper.getType(itemStack);
        if (itemType == null) // Not a custom item, but we still want to edit it probably.
            return new GenericItem(itemStack);

        try {
            return itemType.getItemClass().getDeclaredConstructor(ItemStack.class).newInstance(itemStack);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Failed to construct item '" + itemType.name() + "'.");
            return null;
        }
    }

    /**
     * Build an ItemStack from the supplied parameters.
     * @param material
     * @param name
     * @param lore
     * @return
     */
    public static ItemStack createItem(Material material,  String name, String... lore) {
        return createItem(material, (short) 0, name, lore);
    }

    /**
     * Build an ItemStack from the supplied parameters.
     * @param material
     * @param data
     * @param name
     * @param lore
     * @return
     */
    public static ItemStack createItem(Material material, short data, String name, String... lore) {
        ItemStack itemStack = new ItemStack(material, 1, data);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + name);
        List<String> loreList = new ArrayList<>();
        for (String loreLine : lore)
            loreList.add(ChatColor.GRAY + loreLine);
        meta.setLore(loreList);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
