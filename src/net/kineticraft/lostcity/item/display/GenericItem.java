package net.kineticraft.lostcity.item.display;

import net.kineticraft.lostcity.item.ItemWrapper;
import org.bukkit.inventory.ItemStack;

/**
 * Allows interacting with vanilla and custom items without having custom behaviour apply.
 *
 * Created by Kneesnap on 6/2/2017.
 */
public class GenericItem extends ItemWrapper {

    public GenericItem(ItemStack item) {
        super(item);
    }

    @Override
    public ItemStack getRawStack() {
        return getItem();
    }

    @Override
    public void updateItem() {

    }
}
