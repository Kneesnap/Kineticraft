package net.kineticraft.lostcity;

import jdk.nashorn.internal.runtime.linker.LinkerCallSite;
import lombok.Cleanup;
import lombok.Getter;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import net.kineticraft.lostcity.utils.Dog;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Core - Kineticraft Core Plugin
 *
 * Created May 26th, 2017.
 * @author Kneesnap
 */
public class Core extends JavaPlugin {

    @Getter
    private static Core instance;

    private static final String[] FOLDERS = new String[] {"players", "messages"};

    @Override
    public void onEnable() {
        instance = this;
        Arrays.stream(FOLDERS).forEach(Core::makeFolder); // Create all data folders.
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
        alert(EnumRank.HELPER, ChatColor.RED + message);
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
    private static void makeFolder(String folder) {
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

    /**
     * Strictly checks whether the command sender is a dev from a hardcoded list.
     * Is used for highly sensitive functions such as javascript evaluating.
     *
     * @param sender
     * @return isDev
     */
    public static boolean isDev(CommandSender sender) {
        return (sender instanceof Player && Arrays.asList("Kneesnap", "SuperAnimeBoi").contains(sender.getName()))
                || sender instanceof ConsoleCommandSender;
    }

    /**
     * Gets a list of non-vanished online players.
     * @return players
     */
    public static List<Player> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream().filter(p -> !KCPlayer.getWrapper(p).isVanished()).collect(Collectors.toList());
    }

    /**
     * Take a backup of the server.
     */
    public static void takeBackup() {
        Dog.KINETICA.say("Server is backing up, expect lag!");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-off");

        Bukkit.getScheduler().runTaskAsynchronously(getInstance(), () -> {
            try {
                final ProcessBuilder childBuilder = new ProcessBuilder("./backup.sh");
                childBuilder.redirectErrorStream(true);
                childBuilder.directory(getInstance().getDataFolder().getParentFile().getParentFile());
                final Process child = childBuilder.start();

                Bukkit.getScheduler().runTaskAsynchronously(getInstance(), () -> {
                    try {
                        @Cleanup BufferedReader reader = new BufferedReader(new InputStreamReader(child.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null)
                            Bukkit.getLogger().info(line);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                child.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Bukkit.getScheduler().runTask(getInstance(), () ->
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-on"));
                Dog.KINETICA.say("Backup complete.");
            }
        });
    }
}