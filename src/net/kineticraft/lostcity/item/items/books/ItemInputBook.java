package net.kineticraft.lostcity.item.items.books;

import net.kineticraft.lostcity.item.ItemType;
import org.bukkit.inventory.ItemStack;

/**
 * Allow prompting a user for user input.
 *
 * Created by Kneesnap on 7/10/2017.
 */
public class ItemInputBook extends ItemBook {

    public ItemInputBook(ItemType type) {
        super(type, "Input Book", false);
    }

    public ItemInputBook(ItemStack item) {
        super(item);
    }

    @Override
    public boolean isWriteLines() {
        return true;
    }
}
