package net.kineticraft.lostcity.mechanics;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.data.QueryTools;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Handler - Manages loading and unloading of player data.
 *
 * Created by Kneesnap on 5/29/2017.
 */
public class DataHandler extends Mechanic {

    @Override
    public void onEnable() {
        // Every 5 minutes, save all playerdata
        Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), DataHandler::saveAllPlayers, 0, 5 * 60 * 20);
    }

    /**
     * Save all loaded player data.
     * ASync-Safe.
     */
    public static void saveAllPlayers() {
        new ArrayList<>(KCPlayer.getPlayerMap().values()).forEach(KCPlayer::writeData);
    }

    @EventHandler(priority = EventPriority.LOWEST) // Run first, so other things like ban checker have data.
    public void onAttemptJoin(AsyncPlayerPreLoginEvent evt) {
        try {
            KCPlayer.getPlayerMap().put(evt.getUniqueId(), KCPlayer.getWrapper(evt.getUniqueId())); // Load player.
        } catch (Exception e) {
            e.printStackTrace();
            Core.warn("Failed to load " + evt.getName() + "'s player data!");
            evt.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    ChatColor.RED + "There was an error loading your playerdata. Staff have been notified.");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST) // Run first, so other things like ban checker have data.
    public void onJoinFail(AsyncPlayerPreLoginEvent evt) {
        if (evt.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED)
            KCPlayer.getPlayerMap().remove(evt.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST) // Run last.
    public void onJoinResult(PlayerLoginEvent evt) {
        String ip = evt.getAddress().toString().split("/")[1].split(":")[0];

        if (evt.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            KCPlayer.getPlayerMap().remove(evt.getPlayer().getUniqueId()); // Remove their data if they weren't let on.
            Core.alertStaff(ChatColor.RED + evt.getPlayer().getName() + " (" + ChatColor.YELLOW + evt.getAddress().toString()
                    + ChatColor.RED + ") attempted login.");
            return;
        }

        KCPlayer.getWrapper(evt.getPlayer()).writeData(); // Save data.

        QueryTools.queryData(d -> {
            List<KCPlayer> maybe = d.filter(k -> ip.equals(k.getLastIP()))
                    .filter(k -> !k.getUsername().equals(evt.getPlayer().getName())).collect(Collectors.toList());
            if (maybe.isEmpty())
                return; // Nobody found.

            long banned = maybe.stream().filter(KCPlayer::isBanned).count();
            Core.alertStaff(evt.getPlayer().getName() + " shares the same IP as " + maybe.stream()
                    .map(KCPlayer::getUsername).collect(Collectors.joining(", ")));

            if (banned > 0) {
                Core.warn(evt.getPlayer().getName() + " shares the same IP as " + banned + " banned players.");
                if (!evt.getPlayer().hasPlayedBefore())
                    KCPlayer.getWrapper(evt.getPlayer()).punish(Punishments.PunishmentType.ALT_ACCOUNT, Bukkit.getConsoleSender());
            }
        });
    }

    @Override
    public void onJoin(Player player) {
        KCPlayer.getWrapper(player).updatePlayer();
    }

    @Override
    public void onQuit(Player p) {
        // Don't use KCPlayer#getWrapper, since we don't want to load the data if it's not there.
        KCPlayer player = KCPlayer.getPlayerMap().get(p.getUniqueId());
        if (player != null && player.getLastIP() != null) // Only save if data exists and the player has been online at least once.
            player.writeData(); // Save the player's data to disk.
        KCPlayer.getPlayerMap().remove(p.getUniqueId()); // Unload their data from memory.
    }

    @Override
    public void onDisable() {
        Core.logInfo("Saving all player data...");
        saveAllPlayers();
    }
}
