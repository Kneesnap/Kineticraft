package net.kineticraft.lostcity.mechanics;

import net.kineticraft.lostcity.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

/**
 * GeneralMechanics - Small general mechanics.
 *
 * Created by Kneesnap on 5/29/2017.
 */
public class GeneralMechanics extends Mechanic {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
        if (evt.getPlayer().hasPlayedBefore())
            return;
        Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1.1F));
        Bukkit.broadcastMessage(ChatColor.GRAY + "Welcome " + ChatColor.GREEN + evt.getPlayer().getName()
                + ChatColor.GRAY + " to " + ChatColor.BOLD + "Kineticraft" + ChatColor.GRAY + "!");
    }

    @EventHandler
    public void onPlayerTabComplete(PlayerChatTabCompleteEvent evt) {
        if (!"1234567890abcdefghijklmnopqrstuvwxyz".contains(evt.getChatMessage().substring(0, 1).toLowerCase()))
            Core.alertStaff(ChatColor.RED + "[" + evt.getPlayer().getName() + "]" + ChatColor.GRAY + ": " + evt.getChatMessage());
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent evt) {
        if (evt.getVehicle().getType() == EntityType.BOAT && !evt.getFrom().getBlock().isLiquid()
                && evt.getTo().getY() > evt.getFrom().getY() && evt.getVehicle().getVelocity().length() == 0)
            Core.alertStaff(ChatColor.RED + "[BoatFly] " + ChatColor.GRAY + evt.getVehicle().getPassenger().getName()
                    + " may be using BoatFly!");
    }
}
