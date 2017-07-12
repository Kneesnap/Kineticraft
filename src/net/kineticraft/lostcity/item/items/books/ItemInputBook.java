package net.kineticraft.lostcity.item.items.books;

import net.kineticraft.lostcity.item.ItemType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Allow prompting a user for user input.
 *
 * Created by Kneesnap on 7/10/2017.
 */
public class ItemInputBook extends ItemBook {

    private static Map<Player, ItemInputBook> inputBooks = new HashMap<>();

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

    @Override
    public void open(Player player) {
        inputBooks.put(player, this);
        super.open(player);
    }
}
