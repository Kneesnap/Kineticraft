package net.kineticraft.lostcity.guis;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.events.CommandRegisterEvent;
import net.kineticraft.lostcity.item.ItemManager;
import net.kineticraft.lostcity.item.ItemWrapper;
import net.kineticraft.lostcity.item.display.GUIItem;
import net.kineticraft.lostcity.utils.PacketUtil;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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

    protected static int ROW_SIZE = 9;
    private static final List<InventoryAction> IGNORE = Arrays.asList(InventoryAction.COLLECT_TO_CURSOR,
            InventoryAction.DROP_ONE_SLOT, InventoryAction.DROP_ALL_SLOT, InventoryAction.NOTHING, InventoryAction.UNKNOWN);

    public GUI(Player player, String title) {
        this(player, title, 1);
    }

    public GUI(Player player, String title, int rows) {
        this.player = player;
        this.inventory = Bukkit.createInventory(null, rows * ROW_SIZE, title);
        this.previous = GUIManager.getGUI(player);

        if (Core.isApplicableBuild(this)) {
            // Don't allow async openings, and lets the subclass' constructor to finish setup.
            markSub();
            Bukkit.getScheduler().runTask(Core.getInstance(), this::open);
        } else {
            player.sendMessage(ChatColor.RED + "This GUI is disabled on this build-type.");
        }
    }

    @EventHandler
    public void onCommandRegister(CommandRegisterEvent evt) {
        evt.register(new CommandGUIs());
    }

    /**
     * Get the amount of rows in this gui.
     * @return rows
     */
    public int getRows() {
        return getInventory().getSize() / ROW_SIZE;
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
     * Go to the slot index of the given row.
     * @param slot
     */
    protected void rowSlot(int slot) {
        setSlotIndex((getSlotIndex() / ROW_SIZE) * ROW_SIZE + (slot - 2));
    }

    /**
     * Moves the slot counter right a specified number of slots.
     * @param slots
     */
    protected void skipSlots(int slots) {
        this.slotIndex += slots;
    }

    /**
     * Skips to the next row in the GUI.
     */
    protected void nextRow() {
        if (getSlotIndex() % 9 != 0)
            setSlotIndex((((getSlotIndex() - 1) / ROW_SIZE) + 1) * ROW_SIZE);
    }

    /**
     * Skips to the bottom row of the GUI.
     */
    protected void toBottom() {
        setSlotIndex(getInventory().getSize() - ROW_SIZE);
    }

    /**
     * Skips to the right edge of the current GUI row.
     * @param items
     */
    protected void toRight(int items) {
        int oldIndex = getSlotIndex();
        nextRow();
        setSlotIndex(getSlotIndex() - items);
        if (getSlotIndex() < oldIndex) {
            skipSlots(9);
            toRight(items);
        }
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
        for (int i = 0; i < (((startSlot / ROW_SIZE) + 1) * ROW_SIZE) - startSlot; i++)
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
        Bukkit.getScheduler().runTask(Core.getInstance(), () -> {
            reconstruct();
            getPlayer().openInventory(getInventory());
            GUIManager.setGUI(getPlayer(), this);
            setParent(false); // We're no longer looking at a sub GUI.
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
        // Apply the items and click code.
        clear(); // Remove existing items.
        addItems(); // Add all items

        // Reconstruct the window if needed.
        Inventory old = getInventory();
        if (getMaxSlot() >= getInventory().getSize())
            remakeInventory();

        // Apply our items to the inventory.
        showItems(); // Show them to the player. (Seperate method so we can allow editting the item after it's added.)
        getPlayer().updateInventory(); // Show the items to the player.
        if (!old.equals(getInventory())) // The inventory has updated, open the new one for the player.
            getPlayer().openInventory(getInventory());
    }

    /**
     * Remake the Bukkit inventory object with the correct parameters.
     */
    protected void remakeInventory() {
        makeInventory(getInventory().getTitle(), (int) Math.ceil((getMaxSlot() + 1) / 9D));
    }

    /**
     * Make the inventory for this GUI with the given parameters.
     * @param title
     * @param rows
     */
    protected void makeInventory(String title, int rows) {
        this.inventory = Bukkit.createInventory(null, rows * ROW_SIZE, title);
    }

    /**
     * Get the maximum inventory slot index used in this item Map.
     * @return maxSlot
     */
    protected int getMaxSlot() {
        return getItemMap().keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    /**
     * Create GUI-instance specific items.
     */
    public abstract void addItems();

    /**
     * Display the items in the GUI.
     * Does not display any items that overflow out of the GUI.
     */
    protected void showItems() {
        itemMap.forEach((k, v) -> getInventory().setItem(k, v.generateItem()));
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
        Bukkit.getScheduler().runTask(Core.getInstance(), () -> {
            if (GUIManager.getGUI(getPlayer()) == this)
                PacketUtil.updateWindowTitle(getPlayer(), newTitle, getInventory().getSize());
        });
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
        throw new UnsupportedOperationException("Not implemented yet"); //TODO
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

    /**
     * Get the number of rows needed to fit each object of an array.
     * @param arr
     * @return rows
     */
    protected static int fitSize(Object[] arr) {
        return fitSize(arr, 0);
    }

    /**
     * Get the number of rows needed to fix each object of an array, plus an extra item count.
     * @param arr
     * @param extra
     * @return rows
     */
    protected static int fitSize(Object[] arr, int extra) {
        return fitSize(arr.length + extra);
    }

    /**
     * Get the number of rows needed to fit each object of a list.
     * @param list
     * @return rows
     */
    protected static int fitSize(Iterable<?> list) {
        return fitSize(list, 0);
    }

    /**
     * Get the number of rows needed to fix each object of a list, plus an extra item count.
     * @param list
     * @param extra
     * @return rows
     */
    protected static int fitSize(Iterable<?> list, int extra) {
        return fitSize(Utils.toList(list).size() + extra);
    }

    /**
     * Get the number of rows needed, from a given number of items.
     * @param items
     * @return rows
     */
    protected static int fitSize(int items) {
        return Math.min(6, (8 + items) / 9);
    }
}
