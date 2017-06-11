package net.kineticraft.lostcity.mechanics;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Allows players to leash entities.
 *
 * Created by Kneesnap on 6/3/2017.
 */
public class Leashes extends Mechanic {

    @EventHandler(ignoreCancelled = true)
    public void onLeash(PlayerInteractAtEntityEvent evt) {
        if (!(evt.getRightClicked() instanceof LivingEntity))
            return;

        LivingEntity e = (LivingEntity) evt.getRightClicked();
        ItemStack hand = evt.getPlayer().getInventory().getItem(evt.getHand());
        if (!(e instanceof Villager) || hand == null || hand.getType() != Material.LEASH || !e.isLeashed())
            return;

        Utils.useItem(hand);
        Bukkit.getScheduler().runTask(Core.getInstance(), () -> e.setLeashHolder(evt.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent evt) {
        if (!evt.getFrom().getWorld().equals(evt.getTo().getWorld()))
            return;
        evt.getPlayer().getNearbyEntities(10, 10, 10).stream().filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e).filter(LivingEntity::isLeashed).filter(e -> e.getLeashHolder() == evt.getPlayer())
                .forEach(e -> {
                    Bukkit.getScheduler().runTask(Core.getInstance(), () -> {
                        e.teleport(evt.getPlayer().getLocation());
                        Bukkit.getScheduler().runTask(Core.getInstance(), () -> e.setLeashHolder(evt.getPlayer()));
                    });
                });
    }
}
