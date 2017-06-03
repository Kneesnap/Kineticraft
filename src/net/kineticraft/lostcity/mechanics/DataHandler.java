package net.kineticraft.lostcity.mechanics;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.utils.Utils;
import net.kineticraft.lostcity.data.JsonLocation;
import net.kineticraft.lostcity.data.KCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

/**
 * Data Handler - Manages loading and unloading of player data.
 *
 * Created by Kneesnap on 5/29/2017.
 */
public class DataHandler extends Mechanic {


    @Override
    public void onEnable() {

        // Every 5 minutes, save all playerdata
        Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), () -> saveAllPlayers(), 0, 5 * 60 * 20);
    }

    /**
     * Save all loaded player data.
     */
    public static void saveAllPlayers() {
        KCPlayer.getPlayerMap().values().forEach(KCPlayer::writeData);
    }

    @EventHandler(priority = EventPriority.LOWEST) // Run first, so other things like ban checker have data.
    public void onAttemptJoin(AsyncPlayerPreLoginEvent evt) {
        try {
            if (evt.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED)
                KCPlayer.getWrapper(evt.getUniqueId()); // This will load their data from disk.
        } catch (Exception e) {
            e.printStackTrace();
            Core.warn("Failed to load " + evt.getName() + "'s player data!");
            evt.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    ChatColor.RED + "There was an error loading your playerdata. Staff have been notified.");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent evt) {
        Location location = evt.getEntity().getLocation();
        Bukkit.getLogger().info(evt.getEntity().getName() + " died at " + Utils.toString(location));
        KCPlayer.getWrapper(evt.getEntity()).getDeaths().add(new JsonLocation(location));
        //TODO: PowerNBT save data.
    }

    @EventHandler(priority = EventPriority.HIGHEST) // Run last.
    public void onJoinResult(AsyncPlayerPreLoginEvent evt) {
        if (evt.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED)
            KCPlayer.getPlayerMap().remove(evt.getUniqueId()); // Remove their data if they weren't let on.
    }

    @Override
    public void onQuit(Player p) {
        // Don't use KCPlayer#getWrapper, since we don't want to load the data if it's not there.
        KCPlayer player = KCPlayer.getPlayerMap().get(p.getUniqueId());
        if (player != null)
            player.writeData(); // Save the player's data to disk.
        KCPlayer.getPlayerMap().remove(p.getUniqueId()); // Unload their data from memory.
    }

    @Override
    public void onDisable() {
        Core.logInfo("Saving all player data...");
        saveAllPlayers();
    }
}
