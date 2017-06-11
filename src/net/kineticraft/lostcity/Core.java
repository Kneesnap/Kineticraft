package net.kineticraft.lostcity;

import lombok.Getter;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
     * Broadcasts a message sent by Kinetica.
     * @param message
     */
    public static void kineticaMessage(String message) {
        Bukkit.broadcastMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "DOG" + ChatColor.GREEN + " Kinetica"
                + ChatColor.GRAY + ": " + ChatColor.WHITE + message);
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
        alertStaff(ChatColor.RED + message);
        //TODO: Broadcast discord
    }

    /**
     * Broadcast a serwer-wide alert.
     * @param alert
     */
    public static void announce(String alert) {
        alert(EnumRank.values()[0], ChatColor.RED + "" + ChatColor.BOLD + " >> " + ChatColor.RED + alert);
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

    /**
     * Creates a folder if it does not exist.
     * @param folder
     */
    public static void makeFolder(String folder) {
        getFile(folder + "/").mkdirs();
    }

    /**
     * Returns a File in the data storage folder with the given name.
     * @param name
     * @return File
     */
    public static File getFile(String name) {
        return new File(Core.getInstance().getDataFolder() + "/" + name);
    }
}