package net.kineticraft.lostcity;

import lombok.Getter;
import net.kineticraft.lostcity.data.KCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Core - Kineticraft Core Plugin
 *
 * Created May 26th, 2017.
 * @author Kneesnap
 */
public class Core extends JavaPlugin {

    @Getter
    private static Core instance;

    @Override
    public void onEnable() {
        instance = this;
        MechanicManager.registerMechanics(); // Initialize all plugin code.
    }

    /**
     * Gets the main Kineticraft World.
     */
    public static World getMainWorld() {
        return Bukkit.getWorlds().get(0);
    }

    /**
     * Send a warning to any place that should receive it.
     * @param message
     */
    public static void warn(String message) {
        Bukkit.getLogger().warning(message);
        //TODO: Broadcast to staff.
        //TODO: Broadcast discord
    }

    /**
     * Broadcast a message to online staff
     * @param message
     */
    public static void alertStaff(String message) {
        alert(EnumRank.HELPER, message);
    }

    /**
     * Tell everyone above a certain rank a message.
     * @param minRank
     * @param message
     */
    public static void alert(EnumRank minRank, String message) {
        Bukkit.getOnlinePlayers().stream().map(KCPlayer::getWrapper).filter(pw -> pw.getRank().isAtLeast(minRank))
                .map(KCPlayer::getPlayer).forEach(p -> p.sendMessage(message));
        Bukkit.getConsoleSender().sendMessage(message);
    }

    /**
     * Log information to the console.
     * @param s
     */
    public static void logInfo(String s) {
        Bukkit.getLogger().info("[KCv4] - " + s);
    }

    public static String getPlayerStoragePath() {
        String ret = getInstance().getDataFolder() + "/players/";
        new File(ret).mkdirs();
        return ret;
    }
}