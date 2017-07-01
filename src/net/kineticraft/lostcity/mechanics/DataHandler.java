package net.kineticraft.lostcity.mechanics;

import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.QueryTools;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Data Handler - Manages loading and unloading of player data.
 *
 * Created by Kneesnap on 5/29/2017.
 */
public class DataHandler extends Mechanic {

    @Getter private static Map<Long, UUID> discordMap = new HashMap<>();

    @Override
    public void onEnable() {
        // Every 5 minutes, save all playerdata
        Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), DataHandler::saveAllPlayers, 0, 5 * 60 * 20);
        loadCache();
    }

    private static void loadCache() {
        List<UUID> check = Arrays.stream(Core.getFile("players/").listFiles())
                .filter(file -> file.getName().endsWith(".json")).map(f -> f.getName().split("\\.")[0])
                .map(UUID::fromString).collect(Collectors.toList());

        check.forEach(u -> KCPlayer.getPlayerMap().put(u, KCPlayer.loadWrapper(u)));
        Bukkit.getLogger().info("Loaded " + KCPlayer.getPlayerMap().size() + " wrappers.");

        QueryTools.queryData(players ->
                players.filter(KCPlayer::isVerified).forEach(p -> discordMap.put(p.getDiscordId(), p.getUuid())));
    }

    /**W
     * Save all online player data.
     * ASync-Safe.
     */
    public static void saveAllPlayers() {
        Bukkit.getOnlinePlayers().stream().map(KCPlayer::getWrapper).forEach(KCPlayer::writeData);
    }

    @EventHandler(priority = EventPriority.LOWEST) // Run first, so other things like ban checker have data.
    public void onAttemptJoin(AsyncPlayerPreLoginEvent evt) {
        if (!KCPlayer.getPlayerMap().containsKey(evt.getUniqueId()))
            KCPlayer.getPlayerMap().put(evt.getUniqueId(), new KCPlayer(evt.getUniqueId(), new JsonData()));
    }

    @EventHandler(priority = EventPriority.HIGHEST) // Run last.
    public void onJoinResult(PlayerLoginEvent evt) {
        String ip = evt.getAddress().toString().split("/")[1].split(":")[0];

        if (evt.getResult() != PlayerLoginEvent.Result.ALLOWED) {
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
    }

    @Override
    public void onDisable() {
        Core.logInfo("Saving all player data...");
        saveAllPlayers();
    }
}
