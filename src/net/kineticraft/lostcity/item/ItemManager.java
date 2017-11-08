package net.kineticraft.lostcity.item;

import net.kineticraft.lostcity.item.display.GenericItem;
import net.kineticraft.lostcity.utils.ReflectionUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manages Item Functions.
 *
 * Created by Kneesnap on 6/2/2017.
 */
public class ItemManager {

    /**
     * Load an item object from an ItemStack.
     * @param itemStack
     * @return item
     */
    public static ItemWrapper constructItem(ItemStack itemStack) {
        ItemType itemType = ItemWrapper.getType(itemStack);
        return itemType != null ? ReflectionUtil.construct(itemType.getItemClass(),
                new Class[] {ItemStack.class}, itemStack) : new GenericItem(itemStack);
    }

    /**
     * Build an ItemStack from the supplied parameters.
     * @param material
     * @param name
     * @param lore
     * @return item
     */
    public static ItemStack createItem(Material material, String name, String... lore) {
        return createItem(material, (short) 0, name, lore);
    }

    /**
     * Build an ItemStack from the supplied parameters.
     * @param material
     * @param data
     * @param name
     * @param lore
     * @return item
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

    /**
     * Create a skull with cached skin data.
     * @param skinValue
     * @param itemName
     * @param itemLore
     * @return skullItem
     */
    public static ItemStack createSkull(String skinValue, String itemName, String... itemLore) {
        GenericItem di = new GenericItem(createItem(Material.SKULL_ITEM, (byte) 3, itemName, itemLore));
        di.setTag("SkullOwner", "{Id:\"%s\",Properties:{textures:[{Value:\"%s\"}]}}", UUID.randomUUID(), skinValue);
        return di.generateItem();
    }

    /**
     * Create a claim shovel.
     * @return shovel
     */
    public static ItemStack makeClaimShovel() {
        return createItem(Material.GOLD_SPADE, ChatColor.YELLOW + "Claim Shovel", "Use this to claim land as your own.");
    }

    /**
     * Creates a skull with a given owner.
     * @param username
     * @return skull
     */
    public static ItemStack makeSkull(String username) {
        ItemStack skull = createItem(Material.SKULL_ITEM, (short) 3, username + "'s skull.");
        ItemMeta meta = skull.getItemMeta();
        ((SkullMeta) meta).setOwner(username);
        skull.setItemMeta(meta);
        return skull;
    }
}
