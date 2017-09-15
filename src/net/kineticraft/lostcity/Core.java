package net.kineticraft.lostcity;

import lombok.Getter;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.discord.DiscordAPI;
import net.kineticraft.lostcity.discord.DiscordChannel;
import net.kineticraft.lostcity.mechanics.system.Restrict;
import net.kineticraft.lostcity.mechanics.system.MechanicManager;
import net.kineticraft.lostcity.utils.ServerUtils;
import net.kineticraft.lostcity.utils.TextUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Core - Kineticraft Plugin Core
 * Created May 26th, 2017.
 * @author Kneesnap
 */
public class Core extends JavaPlugin {

    @Getter
    private static Core instance;

    private static final String[] FOLDERS = new String[] {"players", "messages", "audio"};
    private static final List<String> DEVS = Arrays.asList("a1adbca1-6fc5-42eb-97c7-87259634ecc3",
            "8228fe1c-c02e-4c25-b24f-a005f08f8595");

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
     * Send a warning to any place that should receive it, including discord.
     * @param message
     */
    public static void warn(String message) {
        alertStaff(ChatColor.RED + message);
        DiscordAPI.sendMessage(DiscordChannel.ORYX, message);
    }

    /**
     * Broadcast a text component to in-game and discord.
     * @param baseComponents
     */
    public static void broadcast(BaseComponent... baseComponents) {
        Bukkit.getConsoleSender().sendMessage(TextUtils.toLegacy(baseComponents));
        Bukkit.broadcast(baseComponents);
        DiscordAPI.sendGame(TextUtils.toLegacy(baseComponents));
    }

    /**
     * Broadcast a message in discord and in-game.
     * @param message
     */
    public static void broadcast(String message) {
        alert(EnumRank.values()[0], DiscordChannel.INGAME, message);
    }

    /**
     * Broadcast a serwer-wide alert.
     * @param alert
     */
    public static void announce(String alert) {
        alert(EnumRank.values()[0], DiscordChannel.INGAME,
                ChatColor.RED + "" + ChatColor.BOLD + " >> " + ChatColor.RED + alert);
    }

    /**
     * Broadcast a message to online staff, and NOT discord.
     * @param message
     */
    public static void alertStaff(String message) {
        alert(EnumRank.TRIAL, null, ChatColor.RED + message);
    }

    /**
     * Tell everyone above a certain rank a message.
     * @param minRank
     * @param message
     */
    public static void alert(EnumRank minRank, DiscordChannel channel, String message) {
        getOnlineAsync().stream().map(KCPlayer::getWrapper).filter(pw -> pw.getRank().isAtLeast(minRank))
                .map(KCPlayer::getPlayer).forEach(p -> p.sendMessage(message));
        Bukkit.getConsoleSender().sendMessage(message);

        if (channel != null)
            DiscordAPI.sendMessage(channel,  message);
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
    @SuppressWarnings("ResultOfMethodCallIgnored")
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

    /**
     * Strictly checks whether the command sender is a dev from a hardcoded list.
     * Is used for highly sensitive functions such as javascript evaluating.
     *
     * @param sender
     * @return isDev
     */
    public static boolean isDev(CommandSender sender) {
        return (sender instanceof Player && DEVS.contains(((Player) sender).getUniqueId().toString()))
                || sender instanceof ConsoleCommandSender;
    }

    /**
     * Gets a list of non-vanished online players.
     * @return players
     */
    public static List<Player> getOnlinePlayers() {
        List<Player> players = new ArrayList<>(getOnlineAsync());
        players.removeAll(getHiddenPlayers());
        return players;
    }

    public static List<Player> getHiddenPlayers() {
        return getOnlineAsync().stream().filter(p -> KCPlayer.getWrapper(p).isVanished()).collect(Collectors.toList());
    }

    /**
     * Get all online players, safe for async operations.
     * @return players
     */
    public static Set<Player> getOnlineAsync() {
        return new HashSet<>(Bukkit.getOnlinePlayers());
    }

    /**
     * Load a resource from the jar file.
     * @param fileName
     * @return resource
     */
    public static InputStream loadResource(String fileName) {
        return getInstance().getResource(fileName);
    }

    /**
     * Return whether a passed object can be registered on this build.
     * @param object - An object or class that may be disabled on a given build type.
     * @return applicable
     */
    public static boolean isApplicableBuild(Object object) {
        Class<?> cls = (object instanceof Class) ? (Class<?>) object : object.getClass();
        boolean a = !cls.isAnnotationPresent(Restrict.class)
                || !Arrays.asList(cls.getAnnotation(Restrict.class).value()).contains(ServerUtils.getType());
        if (!a)
            logInfo("Not registering " + cls.getSimpleName() + ".");
        return a;
    }
}