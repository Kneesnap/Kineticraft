package net.kineticraft.lostcity.party.anniversary;

import net.kineticraft.lostcity.item.ItemManager;
import net.kineticraft.lostcity.party.games.FreeplayGame;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Boost Minigame - Original concept by pmme.
 * Created by Kneesnap on 9/15/2017.
 */
public class Boost extends FreeplayGame {
    private static final List<Material> BANNED = Arrays.asList(Material.COAL_BLOCK, Material.STAINED_GLASS_PANE, Material.BARRIER);

    public Boost() {
        setArena(62, 134, 3, 83, 183, 24);
        addSpawnLocation(72.5, 180, 13.5, -40, 75);
        setExit(73, 185, 10.5, 0, 0);
    }

    @Override
    public void onJoin(Player player) {
        super.onJoin(player);
        ItemStack item = ItemManager.createItem(Material.STICK, ChatColor.YELLOW + "Boost Stick", "Official Sniping Gear");
        if (!player.getInventory().contains(item))
            player.getInventory().addItem(item);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent evt) {
        if (isPlaying(evt.getPlayer()) && evt.getTo().getY() <= getArena().getYMin() + 3) { // They've reached the bottom.
            broadcastPlayers(evt.getPlayer().getName() + " went " + ChatColor.DARK_RED + "SPLAT" + ChatColor.BLUE + ".");
            evt.getPlayer().setFallDistance(0);
            spawnPlayer(evt.getPlayer());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent evt) {
        if (evt.getAction() != Action.RIGHT_CLICK_BLOCK && evt.getAction() != Action.RIGHT_CLICK_AIR)
            return;
        Block bk = evt.getPlayer().getTargetBlock((Set<Material>) null, 25);
        Player p = evt.getPlayer();
        if (bk != null && !BANNED.contains(bk.getType())) // Boosting others.
            bk.getWorld().getNearbyEntities(bk.getLocation(), 2, 2, 2).stream().filter(e -> !e.equals(p)).forEach(e -> boost(p, e));

        if (evt.hasBlock() && p.getLocation().distance(evt.getClickedBlock().getLocation()) <= 3 && !BANNED.contains(evt.getClickedBlock().getType()))
            boost(p, p); // Boosting themselves.
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent evt) {
        boost(evt.getPlayer(), evt.getRightClicked());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onCombat(EntityDamageByEntityEvent evt) {
        evt.setCancelled(boost(evt.getDamager(), evt.getEntity()));
    }

    private boolean boost(Entity source, Entity receiver) {
        if (!(source instanceof Player) || !(receiver instanceof Player) || !isPlaying((Player) source) || !isPlaying((Player) receiver))
            return false;
        receiver.sendMessage(ChatColor.GREEN + "Whoosh!");
        Vector toSender = source.getVelocity().subtract(receiver.getVelocity());
        toSender.setY(0);
        double distance = toSender.length();
        if(distance > 0) {
            toSender.multiply(1D / distance);
        } else {
            float yaw = source.getLocation().getYaw();
            toSender.setX(-1 * Math.sin(toRad(yaw)));
            toSender.setZ(Math.cos(toRad(yaw)));
        }
        toSender.multiply(2.5D);
        toSender.setY(2);
        receiver.setVelocity(toSender);
        return true;
    }

    private double toRad(double num) {
        return num * Math.PI / 180;
    }
}
