package net.kineticraft.lostcity.item.display;

import lombok.Getter;
import net.kineticraft.lostcity.item.ItemWrapper;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

/**
 * Represents an Item that only needs editting for display purposes, likely to show up in a GUI.
 *
 * Created by Kneesnap on 6/2/2017.
 */
@Getter
public class DisplayItem extends ItemWrapper {

    private ItemWrapper baseItem;

    private static final List<Material> WOOL_DATA = Arrays.asList(Material.WOOL, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE,
            Material.STAINED_CLAY);

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
     * Update the icon for this item.
     * @param icon
     * @return this
     */
    public DisplayItem setIcon(Material icon) {
        getItem().setType(icon);
        return this;
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
        setMeta(WOOL_DATA.contains(getItem().getType()) ? color.getWoolData() : color.getDyeData());
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