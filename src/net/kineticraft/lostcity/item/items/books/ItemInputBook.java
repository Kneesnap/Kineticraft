package net.kineticraft.lostcity.item.items.books;

import net.kineticraft.lostcity.data.lists.StringList;
import net.kineticraft.lostcity.item.ItemManager;
import net.kineticraft.lostcity.item.ItemType;
import net.kineticraft.lostcity.item.ItemWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Allow prompting a user for user input.
 * Created by Kneesnap on 7/10/2017.
 */
public abstract class ItemInputBook extends ItemBook implements Listener {

    private StringList list;

    public ItemInputBook(ItemType type, StringList pages) {
        this(type);
        this.list = pages;
    }

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
    public void updateItem() {
        if (list != null)
            list.forEach(this::addPage);
        super.updateItem();
    }

    @EventHandler
    public void onBookEdit(PlayerEditBookEvent event) {
        ItemWrapper item = ItemManager.constructItem(event.getPlayer().getInventory().getItem(event.getSlot()));
        if (item == null || !(item instanceof ItemInputBook))
            return;

        ItemInputBook book = (ItemInputBook) item;
        event.setCancelled(true);
        event.getPlayer().getInventory().setItem(event.getSlot(), null); // Remove book from inventory.
        book.setMeta(event.getNewBookMeta()); // Update meta.
        book.onUpdate(event.getPlayer());
    }

    /**
     * Get the contents of the book stored in a saveable method.
     * @return lines
     */
    public StringList getLines() {
        return new StringList(getMeta().getPages());
    }

    /**
     * Executed when the book input is updated.
     */
    protected abstract void onUpdate(Player player);
}
