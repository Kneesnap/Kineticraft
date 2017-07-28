package net.kineticraft.lostcity.item.display;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.guis.GUI;
import net.kineticraft.lostcity.guis.GUIType;
import net.kineticraft.lostcity.guis.GUIManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents an item that displays in a GUI and has actions when clicked.
 *
 * Created by Kneesnap on 6/2/2017.
 */
@Getter
public class GUIItem extends DisplayItem {

    private Map<IClickType, List<Consumer<ClickEvent>>> listeners = new HashMap<>();

    public GUIItem(ItemStack item) {
        super(item);
    }

    /**
     * Listen for a left-click.
     * @param evt
     * @return this
     */
    public GUIItem leftClick(Consumer<ClickEvent> evt) {
        return onClick(IClickType.LEFT, evt);
    }

    /**
     * Listen for a right-click.
     * @param evt
     * return this
     */
    public GUIItem rightClick(Consumer<ClickEvent> evt) {
        return onClick(IClickType.RIGHT, evt);
    }

    /**
     * Listen for a middle-click.
     * @param evt
     * @return this
     */
    public GUIItem middleClick(Consumer<ClickEvent> evt) {
        return onClick(IClickType.MIDDLE, evt);
    }

    /**
     * Listen for a shift-click.
     * @param evt
     * @return this
     */
    public GUIItem shiftClick(Consumer<ClickEvent> evt) {
        return onClick(IClickType.SHIFT, evt);
    }

    /**
     * Listen for any click.
     * @param evt
     * @return this
     */
    public GUIItem anyClick(Consumer<ClickEvent> evt) {
        return onClick(IClickType.ANY, evt);
    }

    /**
     * Remove all listeners of a given clicktype.
     * @param type
     * @return this
     */
    public GUIItem clear(IClickType type) {
        getListeners().getOrDefault(type, new ArrayList<>()).clear();
        return this;
    }
    /**
     * Listen for a specified click-type.
     * @param type
     * @param evt
     * @return this
     */
    private GUIItem onClick(IClickType type,  Consumer<ClickEvent> evt) {
        listeners.putIfAbsent(type, new ArrayList<>());
        listeners.get(type).add(evt);
        return this;
    }

    /**
     * Opens a specified gui when clicked.
     * @param type
     * @return this
     */
    public GUIItem opens(GUIType type) {
        return opens(type, IClickType.ANY);
    }

    /**
     * Opens a specified GUI when a certain click occurs
     * @param type
     * @param clickType
     * @return this
     */
    public GUIItem opens(GUIType type, IClickType clickType) {
        onClick(clickType, e -> GUIManager.openGUI(e.getPlayer(), type));
        return this;
    }

    /**
     * Calls when this item is clicked.
     * @param evt
     */
    public void onClick(InventoryClickEvent evt) {
        ClickEvent clickEvent = new ClickEvent((Player) evt.getWhoClicked(), evt.getCurrentItem(), evt);
        listeners.keySet().stream().filter(c -> c.isType(evt.getClick()))
                .map(listeners::get).forEach(list -> list.forEach(l -> l.accept(clickEvent)));
    }

    @AllArgsConstructor @Getter
    public class ClickEvent {
        private Player player;
        private ItemStack clicked;
        private InventoryClickEvent event;

        /**
         * Get the gui this event happened in.
         * @return
         */
        public GUI getGUI() {
            return GUIManager.getGUI(getPlayer());
        }
    }

    @AllArgsConstructor
    public enum IClickType {
        SHIFT(ClickType::isShiftClick),
        LEFT(ClickType::isLeftClick),
        MIDDLE(t -> t == ClickType.MIDDLE),
        RIGHT(ClickType::isRightClick),
        ANY(t -> true);

        private final Function<ClickType, Boolean> check;

        /**
         * Does this apply to the given click?
         * @param type
         * @return isType
         */
        public boolean isType(ClickType type) {
            return check.apply(type);
        }
    }
}
