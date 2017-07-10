package net.kineticraft.lostcity.item;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.item.event.events.ItemEntityInteractEvent;
import net.kineticraft.lostcity.item.event.events.ItemInteractEvent;
import net.kineticraft.lostcity.mechanics.Mechanic;
import net.kineticraft.lostcity.utils.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Custom Item Event System
 *
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

    /*@EventHandler // Disabled for now.
    public void onVillagerOpen(InventoryOpenEvent evt) {
        if (!(evt.getInventory() instanceof MerchantInventory))
            return;
        Merchant merchant = (Villager) evt.getInventory().getHolder();

        // Remove specified villager trades:
        removeTrades(merchant, Material.BOOK, Material.WRITTEN_BOOK);
        removeTrades(merchant, Material.PAPER);
    }*/

    @EventHandler // Prevents players from selling custom items and crafting with them.
    public void onClick(InventoryClickEvent evt) {
        if (!(evt.getInventory() instanceof MerchantInventory))
            return;

        ItemStack item = evt.getClick() == ClickType.NUMBER_KEY ? evt.getWhoClicked().getInventory().getItem(evt.getRawSlot())
                : (evt.isShiftClick() ? evt.getCurrentItem() : evt.getCursor());

        if (ItemWrapper.getType(item) != null)
            evt.setCancelled(true); // Cancel for all items that are custom.
    }

    /**
     * Remove trades with the given item types as required ingredients.
     * @param merchant
     * @param remove
     */
    private static void removeTrades(Merchant merchant, Material... remove) {
        List<Material> checkFor = Arrays.asList(remove);
        List<MerchantRecipe> newRecipes = new ArrayList<>(merchant.getRecipes());
        merchant.getRecipes().stream().filter(mr -> mr.getIngredients().stream().anyMatch(i ->
                checkFor.contains(i.getType()))).forEach(newRecipes::remove);
        merchant.setRecipes(newRecipes);
    }
}
