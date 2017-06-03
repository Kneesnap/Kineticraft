package net.kineticraft.lostcity.mechanics;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.meta.BookMeta;

/**
 * Handles server books.
 *
 * Created by Kneesnap on 6/2/2017.
 */
public class Books extends Mechanic {

    private static final String SERVER_AUTHOR = "Kineticraft Staff";

    @EventHandler // Prevents players from giving server books to villagers.
    public void onClick(InventoryClickEvent evt) {
        if (!(evt.getInventory() instanceof MerchantInventory || evt.getInventory() instanceof CraftingInventory))
            return;

        ItemStack item = evt.getClick() == ClickType.NUMBER_KEY ? evt.getWhoClicked().getInventory().getItem(evt.getRawSlot())
                : (evt.isShiftClick() ? evt.getCurrentItem() : evt.getCursor());

        if (item == null || item.getType() != Material.WRITTEN_BOOK)
            return; // Not a book, we don't care.

        BookMeta bookMeta = (BookMeta) item.getItemMeta();
        if (SERVER_AUTHOR.equals(bookMeta.getAuthor()))
            evt.setCancelled(true); // Don't allow crafting or trading server books to merchants.
    }
}
