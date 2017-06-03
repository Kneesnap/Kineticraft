package net.kineticraft.lostcity.item.guis;

import net.kineticraft.lostcity.item.ItemWrapper;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an Item that only needs editting for display purposes, likely to show up in a GUI.
 *
 * Created by Kneesnap on 6/2/2017.
 */
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
}
