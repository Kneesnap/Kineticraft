package net.kineticraft.lostcity.item.event;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.item.ItemManager;
import net.kineticraft.lostcity.item.ItemWrapper;
import net.kineticraft.lostcity.item.display.GenericItem;
import net.kineticraft.lostcity.utils.ReflectionUtil;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Represents a custom ItemEvent. This is not a bukkit event as we'd like to have it fire in the context of the item class,
 * which isn't possible with the bukkit event system.
 *
 * Created by Kneesnap on 6/11/2017.
 */
@Getter
public abstract class ItemEvent {

    private Player player; // Who triggered the event?
    private ItemWrapper wrapper; // Cached so if setVanilla is called we don't change the object we're referencing in other places.
    private ItemUsage usage;
    private Event event;
    @Setter private ItemStack vanilla; // The ItemStack which caused the firing of this event.
    @Setter private boolean removed; // Should this item be removed from their inventory on completion?
    @Setter private boolean cancelled; // Do we cancel the original bukkit event?

    public ItemEvent(Player player, ItemStack item, ItemUsage usage) {
        this(player, item, usage, null);
    }

    public ItemEvent(Player player, ItemStack item, ItemUsage usage, Event event) {
        this.player = player;
        this.wrapper = ItemManager.constructItem(item);
        this.vanilla = item;
        this.usage = usage;
        this.event = event;
    }

    /**
     * Fire this event.
     */
    public void fire() {
        if (getVanilla() == null || getVanilla().getType() == Material.AIR || getWrapper() instanceof GenericItem)
            return;

        if (getEvent() instanceof Cancellable) // Cancel events by default.
            ((Cancellable) getEvent()).setCancelled(true);

        try {
            for (Method m : ReflectionUtil.getMethods(getWrapper().getClass(), getClass())) {
                ItemListener l = m.getAnnotation(ItemListener.class);
                if (l != null && (l.value() == null || Arrays.asList(l.value().getUsages()).contains(getUsage())))
                    m.invoke(getWrapper(), this);
            }
            handle();
        } catch (Exception e) {
            e.printStackTrace();
            Core.warn("Error using " + player.getName() + "'s " + getWrapper().getClass().getSimpleName() + "!");
            getPlayer().sendMessage(ChatColor.RED + "There was an error while using this item.");
        }
    }

    /**
     * Update the result item.
     * @param itemStack
     */
    public void setResult(ItemStack itemStack) {
        setVanilla(itemStack);
    }

    /**
     * Get the resulting item from this event.
     * @return item
     */
    protected ItemStack getResult() {
        return isRemoved() ? Utils.useItem(getVanilla().clone()) : getVanilla();
    }

    /**
     * Handles event-specific code.
     */
    protected abstract void handle();
}
