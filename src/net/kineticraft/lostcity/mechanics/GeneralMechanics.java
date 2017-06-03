package net.kineticraft.lostcity.mechanics;

import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

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

    @EventHandler(ignoreCancelled = true)
    public void onEggPunch(PlayerInteractEvent evt) {
        Block block = evt.getClickedBlock();
        if ((evt.getAction() != Action.LEFT_CLICK_BLOCK && evt.getAction() != Action.RIGHT_CLICK_BLOCK)
                || block == null || block.getType() != Material.DRAGON_EGG)
            return;

        evt.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "You have picked up the egg.");
        block.setType(Material.AIR);
        Utils.giveItem(evt.getPlayer(), new ItemStack(Material.DRAGON_EGG));
        evt.setCancelled(true);
    }
}
