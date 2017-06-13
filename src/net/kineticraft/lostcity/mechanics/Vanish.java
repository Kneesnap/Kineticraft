package net.kineticraft.lostcity.mechanics;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Handles vanished players.
 *
 * Created by Kneesnap on 6/11/2017.
 */
public class Vanish extends Mechanic {

    @Override
    public void onEnable() {

        // Tell vanished players they're vanished.
        Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), () ->
            Bukkit.getOnlinePlayers().stream().filter(p -> KCPlayer.getWrapper(p).isVanished())
                    .forEach(p -> p.sendActionBar(ChatColor.GRAY + "You are vanished.")), 0L, 40L);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // Run after command logic.
    public void onChat(AsyncPlayerChatEvent evt) {
        if (!KCPlayer.getWrapper(evt.getPlayer()).isVanished())
            return;

        evt.setCancelled(true);
        evt.getPlayer().sendMessage(ChatColor.RED + "You are vanished, message was not sent to chat.");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
        if (!KCPlayer.getWrapper(evt.getPlayer()).isVanished())
            return;

        evt.setJoinMessage(null);
        Core.alert(EnumRank.MEDIA, ChatColor.GRAY + evt.getPlayer().getName() + " joined silently.");
    }

    @Override
    public void onJoin(Player player) {
        updateVanish(); // Correct vanish stuff for new players.
    }

    /**
     * Hide all vanished players from this player.
     * @param player
     */
    public static void hidePlayers(Player player) {
        KCPlayer p = KCPlayer.getWrapper(player);
        boolean vanished = p.isVanished();

        Bukkit.getOnlinePlayers().stream().filter(pl -> pl != player).forEach(pl -> {
            if (KCPlayer.getWrapper(player).getRank().isAtLeast(EnumRank.MEDIA))
                return;
            if (vanished) {
                pl.hidePlayer(player);
            } else {
                pl.showPlayer(player);
            }
        });
    }

    /**
     * Update the vanish state of everyone on the server.
     */
    public static void updateVanish() {
        Bukkit.getOnlinePlayers().forEach(Vanish::hidePlayers);
    }
}
