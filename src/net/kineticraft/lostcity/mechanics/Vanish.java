package net.kineticraft.lostcity.mechanics;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.cutscenes.Cutscenes;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.mechanics.metadata.Metadata;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
import net.kineticraft.lostcity.utils.PacketUtil;
import net.kineticraft.lostcity.utils.ReflectionUtil;
import net.kineticraft.lostcity.utils.Utils;
import net.minecraft.server.v1_12_R1.EnumGamemode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.potion.PotionEffectType;
import org.inventivetalent.packetlistener.PacketListenerAPI;
import org.inventivetalent.packetlistener.handler.PacketHandler;
import org.inventivetalent.packetlistener.handler.ReceivedPacket;
import org.inventivetalent.packetlistener.handler.SentPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles vanished players.
 * Created by Kneesnap on 6/11/2017.
 */
public class Vanish extends Mechanic {

    @Override
    public void onEnable() {
        // Tell vanished players they're vanished.
        Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), () ->
            Bukkit.getOnlinePlayers().stream().filter(p -> KCPlayer.getWrapper(p).isVanished())
                    .forEach(p -> p.sendActionBar(ChatColor.GRAY + "You are vanished.")), 0L, 40L);

        // Hides GM3 from non-staff. Does not hide them from any players in GM3 to stop interferance.
        PacketListenerAPI.addPacketHandler(new PacketHandler(Core.getInstance()) {
            @Override
            public void onSend(SentPacket packet) {
                if (!packet.hasPlayer())
                    return;

                Player p = packet.getPlayer();
                String packetName = packet.getPacketName();

                if (packetName.equals("PacketPlayOutPlayerInfo") && !canSeeSpectator(p)) {
                    ((List<?>) packet.getPacketValueSilent("b")).stream()
                            .filter(d -> ReflectionUtil.getField(d, "c") == EnumGamemode.SPECTATOR)
                            .forEach(d -> ReflectionUtil.setField(d, "c", EnumGamemode.CREATIVE));
                }
            }

            @Override
            public void onReceive(ReceivedPacket receivedPacket) {}
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSendTabCompletes(TabCompleteEvent evt) {
        if (Utils.getRank(evt.getSender()).isStaff()) // Remove vanished players from non-staff view.
            return;

        List<String> completes = new ArrayList<>(evt.getCompletions());
        Core.getHiddenPlayers().stream().map(Player::getName).forEach(completes::remove);
        evt.setCompletions(completes);
    }

    @EventHandler(priority = EventPriority.HIGH) // Run after command logic.
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
        Core.alert(EnumRank.MEDIA, null, ChatColor.GRAY + evt.getPlayer().getName() + " joined silently.");
        MetadataManager.setMetadata(evt.getPlayer(), Metadata.VANISH_TIME, System.currentTimeMillis());
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent evt) {
        showSpectator(evt.getPlayer());
    }

    @Override
    public void onJoin(Player player) {
        showSpectator(player); // Show spectator.
        updateVanish(); // Correct vanish stuff for new players.
    }

    /**
     * Hide all vanished players from this player.
     * @param player
     */
    public static void hidePlayers(Player player) {
        KCPlayer p = KCPlayer.getWrapper(player);
        boolean vanished = p.isVanished();
        Utils.setPotion(player, PotionEffectType.INVISIBILITY, vanished);

        Bukkit.getOnlinePlayers().stream().filter(pl -> pl != player).forEach(pl -> {
            if (!vanished) {
                pl.showPlayer(player);
            } else {
                pl.hidePlayer(player);
            }
        });
    }

    /**
     * Update the vanish state of everyone on the server.
     */
    public static void updateVanish() {
        Bukkit.getOnlinePlayers().forEach(Vanish::hidePlayers);
    }

    /**
     * Can a player see other players in gamemode 3?
     * @param player
     * @return canSee
     */
    private static boolean canSeeSpectator(Player player) {
        return Utils.getRank(player).isAtLeast(EnumRank.MEDIA) || Cutscenes.isWatching(player);
    }

    /**
     * Show spectator mode to the right players.
     * @param player
     */
    public static void showSpectator(Player player) {
        Bukkit.getScheduler().runTask(Core.getInstance(), () -> {
            if (player.getGameMode() == GameMode.SPECTATOR)
                Bukkit.getOnlinePlayers().stream().filter(Vanish::canSeeSpectator).forEach(p -> PacketUtil.updateGameMode(p, player));
        });
    }
}
