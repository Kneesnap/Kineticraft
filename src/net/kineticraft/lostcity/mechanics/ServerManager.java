package net.kineticraft.lostcity.mechanics;

import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.data.lists.QueueList;
import net.kineticraft.lostcity.utils.ServerUtils;
import net.kineticraft.lostcity.utils.TextUtils;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Manages basic core server utilities such as backing up, rebooting, announcements, lag controller, etc.
 *
 * Created by Kneesnap on 6/28/2017.
 */
public class ServerManager extends Mechanic {

    @Getter private static int renderDistance = 10;
    @Getter private static QueueList<Double> tpsQueue = new QueueList<>();
    private static long lastPoll = System.currentTimeMillis();

    private static final int TPS_INTERVAL = 50;
    private static final int MAX_RENDER = 10;
    private static final int MIN_RENDER = 5;

    @Override
    public void onEnable() {
        // Register announcer.
        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            String s = Utils.randElement(Configs.getTextConfig(Configs.ConfigType.ANNOUNCER).getLines());
            if (s != null)
                Core.broadcast(TextUtils.fromMarkup(s).create());
        }, 0L, 5 * 20 * 60L);

        // Update render distance every minute.
        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () ->
            setRenderDistance(getRenderDistance() + (getTPS() >= 19 ? 1 : -1)), 0L, 60 * 20L);

        // Update TPS Counter
        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            getTpsQueue().trim(25); // Only keep recent records.
            final long startTime = System.currentTimeMillis();
            getTpsQueue().add(Math.abs(((startTime - lastPoll) / (TPS_INTERVAL * (TPS_INTERVAL / 20D)) - 40)));
            lastPoll = startTime;
        }, 0L, TPS_INTERVAL);

        if (!ServerUtils.isDevServer())
            // Reboot after 12 hours of uptime.
            Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
                ServerUtils.takeBackup();
                ServerUtils.reboot(3600);
            }, 23 * 60 * 60 * 20L);

        Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), () -> {
            List<Chunk> unload = new ArrayList<>();
            Bukkit.getWorlds().forEach(w -> Stream.of(w.getLoadedChunks()).filter(Chunk::isLoaded)
                    .filter(ServerManager::shouldUnload).forEach(unload::add));
            if (!unload.isEmpty())
                Core.alertStaff("Unloading " + unload.size() + " chunks.");

            Bukkit.getScheduler().runTask(Core.getInstance(), () -> unload.forEach(Chunk::unload));
        }, 0L, 60 * 20L);
    }

    @Override
    public void onJoin(Player player) {
        player.setViewDistance(getRenderDistance());
    }

    /**
     * Get if a player is within render distance of a given chunk.
     * @param c
     * @return shouldUnload
     */
    private static boolean shouldUnload(Chunk c) {
        int render = getRenderDistance();
        for (Player p : Core.getOnlineAsync())
            if (Math.abs((p.getLocation().getBlockX() / 16) - c.getX()) <= render
                    && Math.abs((p.getLocation().getBlockZ() / 16) - c.getZ()) <= render)
                return false;
        return true;
    }

    /**
     * Update the entire server render distance.
     * @param newDistance
     */
    public static void setRenderDistance(int newDistance) {
        newDistance = Math.max(MIN_RENDER, Math.min(newDistance, MAX_RENDER));
        if (newDistance == getRenderDistance())
            return; // Don't waste resources changing the render distance every pulse, only when it should change.

        renderDistance = newDistance;
        Bukkit.getOnlinePlayers().forEach(p -> p.setViewDistance(getRenderDistance()));
    }

    /**
     * Get the average server TPS.
     * @return tps
     */
    public static double getTPS() {
        return Math.min(20, getTpsQueue().stream().mapToDouble(Double::doubleValue).average().orElse(20D));
    }

    /**
     * Returns a number between 0 and 4 that represents the lag setting.
     * @return lagSetting
     */
    public static int getLagSetting() {
        return (int) (((MAX_RENDER - getRenderDistance()) / (double) (MAX_RENDER - MIN_RENDER)) * 4);
    }
}
