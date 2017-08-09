package net.kineticraft.lostcity.guis;

import net.kineticraft.lostcity.item.display.GUIItem;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A GUI that has support for multiple pages.
 * Created by Kneesnap on 6/9/2017.
 */
public abstract class PagedGUI extends GUI {

    private int playerPage = 1; // Page currently shown to the player.
    private int writePage; // Page currently being written to.
    private String title; // The title to display our overlay over.
    private List<Map<Integer, GUIItem>> pages = new ArrayList<>(); // Pages of items.
    private boolean overlay; // Are we writing the overlay?

    private static final int MAX_ROWS = 5; // The max number of rows per page.

    public PagedGUI(Player player, String title) {
        super(player, title, MAX_ROWS + 1);
        this.title = title;
    }

    @Override //Whenever the gui is cleared, clear our page records.
    public void clear() {
        super.clear();
        pages.clear(); // Clear our pages
        writePage = 0; // Set the page index to the first page.
    }

    @Override // Show items from the current GUI page + the overlay.
    protected void showItems() {
        getPage().forEach((k, v) -> getInventory().setItem(k, v.generateItem())); // Show page items.
        super.showItems(); // Show overlay.
    }

    /**
     * Add an item to the page list.
     * Adds to the current page if we're writing an overlay.
     *
     * @param item
     * @return item
     */
    @Override
    public GUIItem addItem(GUIItem item) {
        if (overlay)
            return super.addItem(item);

        if (pages.isEmpty())
            pages.add(new HashMap<>());

        if (getSlotIndex() >= MAX_ROWS * ROW_SIZE)
            newPage(); // We've reached the overlay, start writing items to the next page.

        pages.get(writePage).put(getSlotIndex(), item);
        nextSlot();
        return item;
    }

    @Override // Gets the item in the slot, will get either an overlay or an item in the page.
    public GUIItem getItem(int slot) {
        return getPage().containsKey(slot) ? getPage().get(slot) : super.getItem(slot);
    }

    /**
     * Get the page currently viewed by the player.
     * @return page
     */
    public Map<Integer, GUIItem> getPage() {
        return pages.get(playerPage - 1);
    }

    /**
     * Start writing items to a new page.
     */
    private void newPage() {
        writePage++;
        pages.add(new HashMap<>()); // Add a new page.
        setSlotIndex(0); // Start writing again at slot 0.
    }

    @Override
    public void addItems() {
        overlay = true; // We're creating the overlay.
        toBottom();
        if (playerPage > 1) // Add previous page button.
            addItem(Material.EMPTY_MAP, ChatColor.GRAY + "Previous Page",
                    "Click here to return to the previous page.").anyClick( e -> setPage(playerPage - 1));

        fillGlass(DyeColor.GRAY);
        if (playerPage < maxPages()) {
            // Add "Next Page" Button
            skipSlots(-1); // Go back an item.
            addItem(Material.EMPTY_MAP, ChatColor.GRAY + "Next Page", "Click here to advance to the next page.")
                    .anyClick(e -> setPage(playerPage + 1));
        }

        skipSlots(-8);
        addCustomOverlay();

        setTitle(this.title + " (" + playerPage + " / " + maxPages() + ")"); // Update the title to show the page.
        overlay = false; // We're no longer creating the overlay.
    }

    protected void addCustomOverlay() {

    }

    /**
     * Changes the page the player is looking at.
     * @param newPage
     */
    protected void setPage(int newPage) {
        playerPage = Math.max(1, Math.min(newPage, maxPages()));
        reconstruct(); // Show the new page.
        getPlayer().playSound(getPlayer().getLocation(), Sound.ENTITY_BAT_TAKEOFF, 2F, 1.4F);
    }

    /**
     * Return the largest page index.
     * @return maxPage
     */
    public int maxPages() {
        return pages.size();
    }
}
