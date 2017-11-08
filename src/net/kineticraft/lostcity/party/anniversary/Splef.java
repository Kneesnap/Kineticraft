package net.kineticraft.lostcity.party.anniversary;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.party.games.FreeplayGame;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * KC Version of Spleef
 * Created by Kneesnap on 9/16/2017.
 */
public class Splef extends FreeplayGame {
    private long lastReset;

    public Splef() {
        setArena(19, 134, -91, 79, 147, -31);
        setExit(42.25, 151, -93.25, -140, 0);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent evt) {
        if (isPlaying(evt.getPlayer()) && evt.getBlock().getType() == Material.TNT)
            evt.setCancelled(false); // Allow players to break tnt in splef.
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageEvent evt) { // Teleport a player out if they lose.
        if (evt.getCause() == EntityDamageEvent.DamageCause.LAVA && evt.getEntity() instanceof Player) {
            evt.getEntity().playEffect(EntityEffect.HURT);
            removePlayer((Player) evt.getEntity());
            Bukkit.getScheduler().runTask(Core.getInstance(), () -> evt.getEntity().setFireTicks(0)); // Must run a tick late.
        }
    }

    @Override
    public void signAction(String action, Player player, Sign sign) {
        if (!action.equals("Reset Arena"))
            return;

        if (lastReset + (45 * 1000) >= System.currentTimeMillis()) {
            player.sendMessage(ChatColor.RED + "The arena can only be reset once every 45 seconds.");
            return;
        }

        broadcastPlayers("The arena has been reset by " + player.getName() + ".");
        getArena().forEachBlock(l -> {
            if (l.getBlock().getType() == Material.AIR)
                l.getBlock().setType(Material.TNT);
        }, 141);
        lastReset = System.currentTimeMillis();
    }

    @Override
    public boolean allowDamage() {
        return true;
    }

    @Override
    protected Location randomSpawn() {
        return new Location(getWorld(), Utils.randInt(31, 70), 142, Utils.randInt(-85, -37));
    }
}