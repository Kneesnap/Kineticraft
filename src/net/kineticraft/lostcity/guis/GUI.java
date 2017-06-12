package net.kineticraft.lostcity.guis;

import com.sun.activation.registries.MailcapParseException;
import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import net.kineticraft.lostcity.item.ItemManager;
import net.kineticraft.lostcity.item.ItemWrapper;
import net.kineticraft.lostcity.item.guis.GUIItem;
import net.kineticraft.lostcity.mechanics.GUIManager;
import net.kineticraft.lostcity.utils.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GUI - Base GUI System.
 *
 * Created by Kneesnap on 6/2/2017.
 */
@Getter
public abstract class GUI {

    private Player player;
    private Inventory inventory;
    private Map<Integer, GUIItem> itemMap = new HashMap<>();
    @Setter private boolean allowStorage;
    @Setter private int slotIndex;
    private GUI previous;
    @Setter private boolean parent;

    private static int ROW_SIZE = 9;
    private static final List<InventoryAction> IGNORE = Arrays.asList(InventoryAction.COLLECT_TO_CURSOR,
            InventoryAction.DROP_ONE_SLOT, InventoryAction.DROP_ALL_SLOT, InventoryAction.NOTHING, InventoryAction.UNKNOWN);

    public GUI(Player player, String title, int rows) {
        this.player = player;
        this.inventory = Bukkit.createInventory(null, rows * ROW_SIZE, title);
        this.previous = GUIManager.getGUI(player);

        // Don't allow async openings, and lets the subclass' constructor to finish setup.
        Bukkit.getScheduler().runTask(Core.getInstance(), this::open);
    }

    /**
     * Get the item in the given slot.
     * @param slotIndex
     * @return item
     */
    protected GUIItem getItem(int slotIndex) {
        return itemMap.get(slotIndex);
    }

    /**
     * Add an item to this GUI.
     * @param iw
     * @return item
     */
    protected GUIItem addItem(ItemWrapper iw) {
        return addItem(iw.generateItem());
    }

    /**
     * Add an item to this GUI.
     * @param itemStack
     * @return this
     */
    protected GUIItem addItem(ItemStack itemStack) {
        return addItem(new GUIItem(itemStack));
    }

    /**
     * Add an item to this GUI.
     * @param material
     * @param name
     * @param lore
     * @return this
     */
    protected GUIItem addItem(Material material,  String name, String... lore) {
        return addItem(material, (short) 0, name, lore);
    }

    /**
     * Add an item to this GUI.
     * @param mat
     * @param meta
     * @param name
     * @param lore
     * @return this
     */
    protected GUIItem addItem(Material mat, short meta, String name, String... lore) {
        return addItem(ItemManager.createItem(mat, meta, name, lore));
    }

    /**
     * Add an item to this GUI.
     * @param item
     * @return this
     */
    protected GUIItem addItem(GUIItem item) {
        assert getSlotIndex() < getInventory().getSize();
        itemMap.put(getSlotIndex(), item);
        nextSlot();
        return item;
    }

    /**
     * Moves the slot counter over to the next slot.
     */
    protected void nextSlot() {
        skipSlots(1);
    }

    /**
     * Moves the slot counter right a specified number of slots.
     * @param slots
     */
    protected void skipSlots(int slots) {
        setSlotIndex(getSlotIndex() + slots);
    }

    /**
     * Skips to the next row in the GUI.
     */
    protected void nextRow() {
        setSlotIndex((((getSlotIndex() - 1) / 9) + 1) * 9);
    }

    /**
     * Skips to the bottom row of the GUI.
     */
    protected void toBottom() {
        setSlotIndex(getInventory().getSize() - 9);
    }

    /**
     * Skips to the right edge of the current GUI row.
     * @param items
     */
    protected void toRight(int items) {
        nextRow();
        setSlotIndex(getSlotIndex() - items);
    }

    /**
     * Center the cursor in the current gui row.
     */
    protected void center() {
        center(1);
    }

    /**
     * Center the slot counter on the current row for a given number of items.
     * @param items
     */
    protected void center(int items) {
        skipSlots(((ROW_SIZE / 2) - getCounterX() - (items / 2)));
    }

    /**
     * Fill the rest of the row with colored glass.
     * @param color
     */
    @SuppressWarnings("deprecation")
    protected void fillGlass(DyeColor color) {
        fillRow(ItemManager.createItem(Material.STAINED_GLASS_PANE, color.getWoolData(), " "));
    }

    /**
     * Fill the rest of the row with a given item.
     * @param itemStack
     */
    protected void fillRow(ItemStack itemStack) {
        int startSlot = getSlotIndex();
        for (int i = 0; i < (((startSlot / 9) + 1) * 9) - startSlot; i++)
            addItem(itemStack);
    }

    /**
     * Get the current X coordinate of the slot counter.
     * @return xCoord
     */
    protected int getCounterX() {
        return getSlotIndex() % ROW_SIZE;
    }

    /**
     * Add a button that takes the user back to the previous menu, if it exists.
     */
    protected void addBackButton() {
        if (getPrevious() == null)
            return;
        toRight(1);
        addItem(Material.BARRIER, ChatColor.RED + "Back", "Click here to return to the previous menu.")
                .anyClick(e -> openPrevious());
    }

    /**
     * Open this GUI.
     */
    public void open() {
        setParent(false); // We're no longer looking at a sub GUI.
        Bukkit.getScheduler().runTask(Core.getInstance(), () -> {
            reconstruct();
            getPlayer().openInventory(getInventory());
            GUIManager.setGUI(getPlayer(), this);
        });
    }

    /**
     * Open the previous GUI, if possible.
     * Closes this GUI if not.
     */
    public void openPrevious() {
        if (getPrevious() != null) {
            getPrevious().open();
        } else {
            close();
        }
    }

    /**
     * Close this GUI.
     */
    public void close() {
        clear(); // Remove all items from the gui.
        Bukkit.getScheduler().runTask(Core.getInstance(), getPlayer()::closeInventory);
    }

    /**
     * Clear this GUI.
     */
    public void clear() {
        getInventory().clear(); // Clear items
        getItemMap().clear(); // Clear click callbacks.
        setSlotIndex(0); // Clear write-index.
    }

    /**
     * Recreate this GUI from scratch.
     */
    public void reconstruct() {
        clear(); // Remove existing items.
        addItems(); // Add all items
        showItems(); // Show them to the player. (Seperate method so we can allow editting the item after it's added.)
        getPlayer().updateInventory(); // Show the items to the player.
    }

    /**
     * Create GUI-instance specific items.
     */
    public abstract void addItems();

    /**
     * Display the items in the GUI.
     */
    protected void showItems() {
        itemMap.entrySet().stream().forEach(m -> getInventory().setItem(m.getKey(), m.getValue().generateItem()));
    }

    /**
     * Handle a player click.
     * @param evt
     */
    public void onClick(InventoryClickEvent evt) {
        InventoryAction action = evt.getAction();
        if (IGNORE.contains(action)) {
            evt.setCancelled(true);
            return; // Don't allow these clicks.
        }

        Inventory top = evt.getView().getTopInventory();
        boolean isTop = top.equals(evt.getClickedInventory());
        boolean isBottom = evt.getView().getBottomInventory().equals(evt.getClickedInventory());
        ItemStack add = null;
        int slot = evt.getRawSlot();

        if (slot >= 0 && isTop) {
            GUIItem item = getItem(slot);
            if (item != null) {
                evt.setCancelled(true);
                item.onClick(evt);
            } else {
                if (!isAllowStorage()) {
                    evt.setCancelled(true); // Don't allow depositing / withdrawing items.
                    return;
                }

                if (action == InventoryAction.HOTBAR_MOVE_AND_READD || action == InventoryAction.HOTBAR_SWAP) {
                    // Hotbar swap.
                    if (item != null || !isAllowStorage()) // Either they're not allowed or they're swapping with a DisplayItem.
                        add = evt.getWhoClicked().getInventory().getItem(evt.getHotbarButton());
                } else if (action == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_ONE) { //PLACE_SOME adds to an existing stack, we only want to fire when a new item is added.
                    add = evt.getCursor().clone();
                    if (action == InventoryAction.PLACE_ONE)
                        add.setAmount(1); // They're right clicking an item in.
                }
            }
        } else if (isBottom && evt.isShiftClick()) { // They're trying to shift click an item in.
            if (isAllowStorage() && top.firstEmpty() > -1) {
                add = evt.getCurrentItem();
            } else {
                evt.setCancelled(true);
            }
        }

        if (add != null) {
            // We're depositing an item.
            if (canDeposit(slot, add)) {
                deposit(add);
            } else {
                evt.setCancelled(true);
            }
        }
    }

    /**
     * Update the title of this GUI.
     * @param newTitle
     */
    public void setTitle(String newTitle) {

        // Runs a tick later so when this is called before the menu is open it still works.
        Bukkit.getScheduler().runTask(Core.getInstance(), () -> PacketUtil.updateWindowTitle(getPlayer(), newTitle,
                getInventory().getSize()));
    }

    /**
     * Handle a player drag.
     * @param evt
     */
    public void onDrag(InventoryDragEvent evt) {
        evt.setCancelled(true); //TODO: Implement drag checks.
    }

    /**
     * Called when an item is deposited into this GUI.
     * @param add
     */
    protected void deposit(ItemStack add) {

    }

    /**
     * Can this item be desposited into the given slot?
     * @param slot
     * @param add
     * @return depositable
     */
    protected boolean canDeposit(int slot, ItemStack add) {
        return true;
    }

    /**
     * Calls when the player closes this GUI.
     */
    public void onClose() {

    }

    /**
     * Marks this is a sub gui. The parent gui won't call onClose when this replaces it.
     */
    protected void markSub() {
        if (getPrevious() != null)
            getPrevious().setParent(true);
    }

    /**
     * Gets the viewer's wrapper.
     * @return wrapper
     */
    protected KCPlayer getWrapper() {
        return KCPlayer.getWrapper(getPlayer());
    }

    protected static int fitSize(Object[] arr) {
        return fitSize(arr.length);
    }

    protected static int fitSize(List<?> list) {
        return fitSize(list.size());
    }

    protected static int fitSize(int items) {
        return Math.min(6, (8 + items) / 9);
    }
}
