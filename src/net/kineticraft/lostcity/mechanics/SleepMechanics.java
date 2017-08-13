package net.kineticraft.lostcity.mechanics;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

/**
 * SleepMechanics - Handles when players go to bed.
 * Created by Kneesnap on 6/2/2017.
 */
public class SleepMechanics extends Mechanic {

    private static final double PERCENT_NEEDED = .25F; // The percentage of players needed to skip a night.

    public static void skipNight() {
        if (getSleepCount() < getNeededPlayers())
            return; // We don't have enough players sleeping.

        World world = Core.getMainWorld();
        world.setTime(0);
        world.setStorm(false);
        Bukkit.broadcastMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + " * The night has been vanquished by the wonders of sleep. *");
    }

    /**
     * Get the number of sleeping players.
     * @return sleepingPlayers
     */
    private static int getSleepCount() {
        return (int) Bukkit.getOnlinePlayers().stream().filter(Player::isSleeping).count();
    }

    /**
     * Get the number of players needed to sleep.
     * @return needed
     */
    private static int getNeededPlayers() {
        return (int) (Bukkit.getOnlinePlayers().size() * PERCENT_NEEDED);
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent evt) {
        if (!evt.getBed().getWorld().equals(Core.getMainWorld()))
            return;

        updateBeds();

        if (!MetadataManager.updateCooldownSilently(evt.getPlayer(), "bedSpam", 20 * 60 * 5))
            Bukkit.broadcastMessage(ChatColor.GREEN.toString() + getLeftPlayers() + ChatColor.GRAY
                    + " players need to enter a bed to skip the night.");
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onBedExit(PlayerBedLeaveEvent evt) {
        updateBeds();
    }

    /**
     * Get the amount of players needed to skip the night, considering the people who are already in a bed.
     * @return needed
     */
    private static int getLeftPlayers() {
        return getNeededPlayers() - getSleepCount();
    }

    private static void updateBeds() {
        if (Core.getMainWorld().getTime() > 10L || getLeftPlayers() <= 0)
            skipNight();
    }
}
