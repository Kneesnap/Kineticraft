package net.kineticraft.lostcity.mechanics;

import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.data.lists.QueueList;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
import net.kineticraft.lostcity.utils.ServerUtils;
import net.kineticraft.lostcity.utils.TextUtils;
import net.kineticraft.lostcity.utils.TimeInterval;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;

/**
 * Manages basic core server utilities such as backing up, rebooting, announcements, lag controller, etc.
 * Created by Kneesnap on 6/28/2017.
 */
public class ServerManager extends Mechanic {

    @Getter private static int renderDistance = 10;
    @Getter private static QueueList<Double> tpsQueue = new QueueList<>();
    private static long lastPoll = System.currentTimeMillis();

    private static final int TPS_INTERVAL = 50;
    private static final int MAX_RENDER = 10;
    private static final int MIN_RENDER = 5;
    private static final int REBOOT_TIME = 3600;

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
            setRenderDistance(getRenderDistance() + (getTPS() >= 19 ? 1 : -1)), 0L, 1200L);

        // Update TPS Counter
        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            getTpsQueue().trim(25); // Only keep recent records.
            final long startTime = System.currentTimeMillis();
            getTpsQueue().add(Math.abs(((startTime - lastPoll) / (TPS_INTERVAL * (TPS_INTERVAL / 20D)) - 40)));
            lastPoll = startTime;
        }, 0L, TPS_INTERVAL);

        if (!ServerUtils.isDevServer()) {
            Utils.runCalendarTaskAt(TimeInterval.HOUR, 6, ServerUtils::takeBackup); // Backup the server at 6AM daily.

            // Automatically reboot after a while.
            Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
                if (ServerUtils.getTicksToReboot() <= REBOOT_TIME * 20 && !ServerUtils.isRebootScheduled())
                    ServerUtils.reboot(REBOOT_TIME);
            }, 0L, 20L);
        }
    }

    /**
     * Get if a player is within render distance of a given chunk.
     * @param c
     * @return shouldUnload
     */
    private static boolean shouldUnload(Chunk c) {
        int render = getRenderDistance() + 1;
        return Core.getOnlineAsync().stream().map(Player::getLocation).map(Location::getChunk)
                .noneMatch(chk -> Math.abs(chk.getX() - c.getX()) <= render || Math.abs(chk.getZ() - c.getZ()) <= render);
    }

    /**
     * Update the entire server render distance.
     * @param newDistance
     */
    public static void setRenderDistance(int newDistance) {
        // MCP = setViewDistance(int i)
        renderDistance = Math.max(MIN_RENDER, Math.min(newDistance, MAX_RENDER));
        ((CraftServer) Bukkit.getServer()).getServer().getPlayerList().a(renderDistance);
        Bukkit.getWorlds().forEach(w -> ((CraftWorld) w).getHandle().spigotConfig.viewDistance = renderDistance);
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
