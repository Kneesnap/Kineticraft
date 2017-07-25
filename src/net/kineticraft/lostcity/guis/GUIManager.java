package net.kineticraft.lostcity.guis;

import net.kineticraft.lostcity.mechanics.system.Mechanic;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages GUIs.
 * Created by Kneesnap on 6/8/2017.
 */
public class GUIManager extends Mechanic {

    private static Map<HumanEntity, GUI> guiMap = new HashMap<>();

    /**
     * Handles a potential GUI click.
     * @param evt
     */
    @EventHandler
    public void onClick(InventoryClickEvent evt) {
        if (guiMap.containsKey(evt.getWhoClicked()))
            guiMap.get(evt.getWhoClicked()).onClick(evt);
    }

    /**
     * Handle a potential GUI drag
     * @param evt
     */
    @EventHandler
    public void onDrag(InventoryDragEvent evt) {
        if (guiMap.containsKey(evt.getWhoClicked()))
            guiMap.get(evt.getWhoClicked()).onDrag(evt);
    }

    /**
     * Handle a potential GUI close.
     * @param evt
     */
    @EventHandler
    public void onClose(InventoryCloseEvent evt) {
        GUI gui = guiMap.remove(evt.getPlayer());
        if (gui != null && !gui.isParent())
            gui.onClose(); // Only fire onClose if it isn't a parent GUI.
    }

    /**
     * Open a GUI for the given player.
     * @param player
     * @param guiType
     */
    public static void openGUI(Player player, GUIType guiType) {
        guiType.construct(player);
    }

    /**
     * Set the GUI the player is currently viewing.
     * @param player
     * @param gui
     */
    public static void setGUI(Player player, GUI gui) {
        guiMap.put(player, gui);
    }

    /**
     * Get the GUI the player is currently viewing.
     * @param player
     * @return gui
     */
    public static GUI getGUI(Player player) {
        return guiMap.get(player);
    }

    @Override // Closes all GUIs.
    public void onDisable() {
        new ArrayList<>(guiMap.keySet()).forEach(HumanEntity::closeInventory);
    }
}
