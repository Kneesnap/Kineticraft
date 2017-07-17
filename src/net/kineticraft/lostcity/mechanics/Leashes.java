package net.kineticraft.lostcity.mechanics;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * Allows players to leash entities.
 *
 * Created by Kneesnap on 6/3/2017.
 */
public class Leashes extends Mechanic {

    private static final List<EntityType> LEASHABLE = Arrays.asList(EntityType.VILLAGER, EntityType.SKELETON_HORSE);

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onLeash(PlayerInteractAtEntityEvent evt) {
        if (!LEASHABLE.contains(evt.getRightClicked().getType()))
            return;

        LivingEntity e = (LivingEntity) evt.getRightClicked();
        ItemStack hand = evt.getPlayer().getInventory().getItem(evt.getHand());
        if (hand == null || hand.getType() != Material.LEASH || e.isLeashed() || Utils.isProtected(evt.getPlayer(), e.getLocation()))
            return;

        evt.setCancelled(true); // Don't open merchant GUI.
        Utils.useItem(hand);
        Bukkit.getScheduler().runTask(Core.getInstance(), () -> e.setLeashHolder(evt.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent evt) {
        if (evt.getFrom().getWorld().equals(evt.getTo().getWorld()))
            evt.getPlayer().getNearbyEntities(10, 10, 10).stream().filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e).filter(LivingEntity::isLeashed).filter(e -> e.getLeashHolder() == evt.getPlayer())
                .forEach(e -> Bukkit.getScheduler().runTask(Core.getInstance(), () -> {
                    e.teleport(evt.getPlayer().getLocation());
                    Bukkit.getScheduler().runTask(Core.getInstance(), () -> e.setLeashHolder(evt.getPlayer()));
                }));
    }
}
