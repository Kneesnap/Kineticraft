package net.kineticraft.lostcity.mechanics;

import net.kineticraft.lostcity.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

/**
 * SleepMechanics - Handles when players go to bed.
 *
 * Created by Kneesnap on 6/2/2017.
 */
public class SleepMechanics extends Mechanic {

    private static double PERCENT_NEEDED = .4F; // The percentage of players needed to skip a night.

    private static void skipNight() {
        if (getSleepCount() < getNeededPlayers())
            return; // We don't have enough players sleeping.

        World world = Core.getMainWorld();
        world.setTime(0);
        world.setStorm(false);
        Bukkit.broadcastMessage(ChatColor.GRAY + "" + ChatColor.ITALIC+ " * The night has been vanquished by the wonders of sleep. *");
    }

    /**
     * Get the number of sleeping players.
     * @return
     */
    private static int getSleepCount() {
        return (int) Bukkit.getOnlinePlayers().stream().filter(Player::isSleeping).count();
    }

    /**
     * Get the number of players needed to sleep.
     * @return
     */
    private static int getNeededPlayers() {
        return (int) (Bukkit.getOnlinePlayers().size() * PERCENT_NEEDED);
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent evt) {
        updateBeds();
    }

    @EventHandler
    public void onBedExit(PlayerBedLeaveEvent evt) {
        updateBeds();
    }

    private static void updateBeds() {
        if (Core.getMainWorld().getTime() <= 10L)
            return; // Don't handle if it just became dawn.

        int needed = getNeededPlayers() - getSleepCount();
        if (needed > 0) {
            Bukkit.broadcastMessage(ChatColor.GRAY + "[Night Skipper] " + ChatColor.GREEN + needed + ChatColor.GRAY
                    + " players need to enter a bed to skip the night.");
        } else {
            skipNight();
        }
    }
}
