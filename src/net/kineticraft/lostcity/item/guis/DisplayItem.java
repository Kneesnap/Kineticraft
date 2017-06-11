package net.kineticraft.lostcity.item.guis;

import lombok.Getter;
import net.kineticraft.lostcity.item.ItemWrapper;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Represents an Item that only needs editting for display purposes, likely to show up in a GUI.
 *
 * Created by Kneesnap on 6/2/2017.
 */
@Getter
public class DisplayItem extends ItemWrapper {

    private ItemWrapper baseItem;

    public DisplayItem(ItemStack item) {
        this(new GenericItem(item), item);
    }

    public DisplayItem(ItemWrapper itemWrapper) {
        this(itemWrapper, itemWrapper.generateItem());
    }

    public DisplayItem(ItemWrapper itemWrapper, ItemStack display) {
        super(display.clone());
        this.baseItem = itemWrapper;
    }

    @Override
    public ItemStack getRawStack() {
        return getItem();
    }

    @Override
    public void updateItem() {

    }

    /**
     * Set the amount of this item.
     * @param amount
     * @return this
     */
    public DisplayItem setAmount(int amount) {
        getItem().setAmount(amount);
        return this;
    }

    /**
     * Set the durability value of this item.
     * @param meta
     * @return this
     */
    public DisplayItem setMeta(short meta) {
        getItem().setDurability(meta);
        return this;
    }

    /**
     * Set the DyeColor of this item.
     * @param color
     * @return this
     */
    @SuppressWarnings("deprecation")
    public DisplayItem setColor(DyeColor color) {
        setMeta(color.getDyeData());
        return this;
    }

    /**
     * Set the DyeColor of this item.
     * @param color
     * @return this
     */
    @SuppressWarnings("deprecation")
    public DisplayItem setWoolColor(DyeColor color) {
        setMeta(color.getWoolData());
        return this;
    }

    /**
     * Set the skull owner of this item.
     * @param username
     * @return this
     */
    public DisplayItem setSkullOwner(String username) {
        ((SkullMeta) getMeta()).setOwner(username);
        setMeta((short) 3);
        return this;
    }

    /**
     * Update the display name of this item.
     * @param displayName
     * @return this
     */
    public DisplayItem setDisplayName(String displayName) {
        getMeta().setDisplayName(ChatColor.WHITE + displayName);
        return this;
    }
}
