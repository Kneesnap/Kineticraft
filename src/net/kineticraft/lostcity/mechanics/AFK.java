package net.kineticraft.lostcity.mechanics;

import com.destroystokyo.paper.Title;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitTask;

/**
 * Prevents players from AFKing.
 * Created by Kneesnap on 6/11/2017.
 */
public class AFK extends Mechanic {

    public void onEnable() {
        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () ->
            Bukkit.getOnlinePlayers().stream().filter(p -> !Utils.getRank(p).isAtLeast(EnumRank.MEDIA)).filter(AFK::isAFK).forEach(p -> {
                BukkitTask kickTask = Bukkit.getScheduler().runTaskLater(Core.getInstance(), () ->
                    Callbacks.cancel(p, Callbacks.ListenerType.CHAT), 35 * 20L);

                int numA = Utils.nextInt(20);
                int numB = Utils.nextInt(20);
                String op  = Utils.randChance(2) ? "+" : "-";
                int answer = op.equals("+") ? numA + numB : numA - numB;
                String problem = ChatColor.YELLOW.toString() + numA + " " + op + " " + numB + " = ?";

                p.sendTitle(new Title(ChatColor.RED + "AFK CHECK", problem, 20, 100, 20));
                p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "AFK CHECK: " + problem);

                Runnable fail = () -> {
                    if (!p.isOnline())
                        return;

                    p.kickPlayer(ChatColor.RED + "You were kicked for idling more than "
                            + Configs.getMainConfig().getAfkLimit() + " minutes.");
                    Core.alertStaff(p.getName() + " was kicked for AFKing.");
                    kickTask.cancel();
                };

                Callbacks.listenForNumber(p, n -> {
                    kickTask.cancel();
                    if (n == answer) {
                        p.sendMessage(ChatColor.GREEN + "Correct.");
                        markActive(p);
                    } else {
                        p.kickPlayer(ChatColor.RED + "Incorrect answer.");
                    }
                }, fail);
            }), 0L, 30 * 20L);
    }

    @Override
    public void onJoin(Player player) {
        markActive(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent evt) {
        markActive(evt.getPlayer());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent evt) {
        markActive(evt.getPlayer());
    }

    /**
     * Is this player AFK?
     * @param player
     * @return isAfk
     */
    public static boolean isAFK(Player player) {
        return !MetadataManager.hasCooldown(player, "active");
    }

    /**
     * Mark this player as active.
     *
     * @param player
     */
    public static void markActive(Player player) {
        int base = Configs.getMainConfig().getAfkLimit();
        MetadataManager.setCooldown(player, "active", Utils.randInt(base - 3, base + 3) * 60 * 20);
    }
}
