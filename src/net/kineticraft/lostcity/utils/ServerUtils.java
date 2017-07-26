package net.kineticraft.lostcity.utils;

import lombok.Getter;
import net.kineticraft.lostcity.mechanics.system.BuildType;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.mechanics.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Contains utilities for the general management of the server.
 *
 * Created by Kneesnap on 6/27/2017.
 */
public class ServerUtils {

    @Getter private static boolean backingUp;

    private static final List<BukkitTask> rebootTasks = new ArrayList<>();
    private static final List<Integer> REBOOT_ALERTS = Arrays.asList(10, 30, 60, 300, 600, 1800, 3600);
    private static long rebootTime = System.currentTimeMillis() + (1000 * 60 * 60 * 24);

    /**
     * Take a backup of the server.
     */
    public static void takeBackup() {
        assert !isBackingUp();

        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(Core.getInstance(), ServerUtils::takeBackup);
            return;
        }

        Dog.KINETICA.say("Server is backing up, expect lag!");
        backingUp = true;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-off");

        Utils.runShell("./backup.sh", () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-on");
            Dog.KINETICA.say("Backup complete.");
            backingUp = false;
        });
    }

    /**
     * Cancel the current reboot, if any.
     */
    public static void cancelReboot() {
        if (!isRebootScheduled())
            return;

        rebootTasks.forEach(BukkitTask::cancel); // Cancel all tasks.
        Core.announce(ChatColor.AQUA + "Reboot cancelled.");
    }

    /**
     * Get if a reboot is scheduled.
     * @return scheduled.
     */
    public static boolean isRebootScheduled() {
        return !rebootTasks.isEmpty();
    }

    /**
     * Cancel the current reboot timer and schedule a reboot, in seconds.
     * @param seconds
     */
    public static void reboot(int seconds) {
        cancelReboot(); // Cancel existing reboot, if any.

        ServerUtils.announceReboot(seconds, 0); // Broadcast the message at this second.
        REBOOT_ALERTS.stream().filter(n -> n < seconds).forEach(n -> ServerUtils.announceReboot(n, seconds));

        rebootTasks.add(Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            if (isBackingUp()) {
                reboot(60);
                return;
            }

            Core.announce(ChatColor.AQUA + "Server rebooting...");
            DataHandler.saveAllPlayers();
            Core.getMainWorld().setAutoSave(true);
            Core.getMainWorld().save();
            Bukkit.getServer().shutdown();
        }, 20 * seconds));

        rebootTime = System.currentTimeMillis() + (seconds * 1000);
    }

    private static void announceReboot(int seconds, int total) {
        // >> Content Patch deploying in {TIME}.
        rebootTasks.add(Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> Core.announce(ChatColor.AQUA
                + "Server rebooting in " + Utils.formatTimeFull(seconds * 1000).toLowerCase() + "."), (total - seconds) * 20));
    }

    /**
     * Get the BuildType of this release.
     * @return type
     */
    public static BuildType getType() {
        return Configs.getMainConfig() != null ? Configs.getMainConfig().getBuildType() : null;
    }

    /**
     * Returns whether or not this is a development server.
     * @return isDev
     */
    public static boolean isDevServer() {
        return getType() == BuildType.DEV;
    }

    /**
     * Returns whether or not this is a beta server.
     * @return isBeta
     */
    public static boolean isBetaServer() {
        return getType() == BuildType.BETA;
    }

    /**
     * Get the current tick of the server.
     * @return tick
     */
    public static int getCurrentTick() {
        return (int) ReflectionUtil.getField(ReflectionUtil.getNMS("MinecraftServer"), "currentTick");
    }

    /**
     * Get the amount of ticks until a reboot should occur.
     * @return ticks
     */
    public static long getTicksToReboot() {
        return (rebootTime - System.currentTimeMillis()) / 50;
    }
}
