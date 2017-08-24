package net.kineticraft.lostcity.item;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.item.event.events.ItemEntityInteractEvent;
import net.kineticraft.lostcity.item.event.events.ItemInteractEvent;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
import net.kineticraft.lostcity.utils.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom Item Event System
 * Created by Kneesnap on 6/11/2017.
 */
public class Items extends Mechanic {

    @Override
    public void onEnable() {
        // Register all items that are listeners as listeners.
        for (ItemType type : ItemType.values())
            if (Listener.class.isAssignableFrom(type.getItemClass()))
                Bukkit.getPluginManager().registerEvents((Listener) ReflectionUtil.construct(type.getItemClass(),
                        new Class[] {ItemStack.class}, new ItemStack(Material.AIR)), Core.getInstance());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent evt) {
        if (evt.getAction() != Action.PHYSICAL)
            new ItemInteractEvent(evt).fire();
    }

    @EventHandler
    public void onEntityClick(PlayerInteractEntityEvent evt) {
        new ItemEntityInteractEvent(evt).fire();
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent evt) {
        if (evt.getDamager() instanceof Player)
            new ItemEntityInteractEvent(evt).fire();
    }

    @EventHandler // Prevents players from selling custom items and crafting with them.
    public void onClick(InventoryClickEvent evt) {
        if (!(evt.getInventory() instanceof MerchantInventory) || evt.getRawSlot() != 2)
            return;

        MerchantInventory mi = (MerchantInventory) evt.getInventory();
        List<ItemStack> ig = new ArrayList<>();
        try {
            ig = mi.getSelectedRecipe().getIngredients();
        } catch (Exception e) {
            for (int i = 0; i < 3; i++) // Interal CraftBukkit throws an error, this avoids it.
                ig.add(new ItemStack(Material.DIRT));
        }

        for (int i = 0; i < evt.getInventory().getSize() - 1; i++)
            if (ItemWrapper.isCustom(evt.getInventory().getItem(i)) && !ItemWrapper.isCustom(ig.get(i)))
                evt.setCancelled(true); // Don't allow the trade to go through if it's using a custom item where none is needed.
    }
}