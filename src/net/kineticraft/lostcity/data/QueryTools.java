package net.kineticraft.lostcity.data;

import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An API that allows for easily querying of all offline playerdata.
 *
 * Created by Kneesnap on 5/29/2017.
 */
public class QueryTools {

    @Getter
    private static int currentQueries;

    private static int FILES_PER_TICK = 75;
    private static int MAX_PLAYER_QUERIES = 2;

    /**
     * Asynchronously loads all playerdata then runs the callback.
     * @param callback
     */
    public static void queryData(Consumer<Stream<KCPlayer>> callback) {

        final BukkitTask[] task = new BukkitTask[1]; // We use int[] because it can be final while allowing us to change the value.
        List<UUID> check = Arrays.stream(Core.getFile("players/").listFiles())
                .filter(file -> file.getName().endsWith(".json")).map(f -> f.getName().split("\\.")[0])
                .map(UUID::fromString).collect(Collectors.toList()); // Get a list of all UUIDs to check.
        List<KCPlayer> loaded = new ArrayList<>();

        currentQueries++;
        task[0] = Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), () -> {
            for (int i = 0; i < FILES_PER_TICK; i++)
                if (!check.isEmpty())
                    loaded.add(KCPlayer.getWrapper(check.remove(0)));

            if (check.isEmpty()) {
                // Done searching, time to perform operations.
                task[0].cancel(); // Cancel this load task.
                currentQueries--;
                if (!loaded.isEmpty())
                    Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), () -> callback.accept(loaded.stream()));
            }
        }, 0, 1);
    }

    /**
     * Loads the playerdata for the given username from disk, if found.
     * @param username
     * @param callback
     */
    public static void getData(String username, Consumer<KCPlayer> callback) {
        getData(username, callback, null);
    }

    /**
     * Gets the playerdata for the given username, if found.
     * @param username
     * @param callback
     * @param fail
     */
    public static void getData(String username, Consumer<KCPlayer> callback, Runnable fail) {
        Player player = Bukkit.getPlayer(username);
        if (player != null) { // They're online.
            callback.accept(KCPlayer.getWrapper(player));
            return;
        }

        // They're offline, load it then made changes.
        queryData(str -> Bukkit.getScheduler().runTask(Core.getInstance(), () -> {
                KCPlayer p = str.filter(kc -> username.equalsIgnoreCase(kc.getUsername())).findAny().orElse(null);
                if (p == null) {
                    if (fail != null)
                        fail.run(); // Oh no, we couldn't find anyone matching this.
                    return;
                }

                callback.accept(p);
                p.writeData(); // Save back to disk.
            }));
    }

    /**
     * Returns if a non-staff member is clear to initiate a query.
     * Prevents a player from spamming a bunch of queries.
     *
     * @param sender
     * @return dontAcceptQuery
     */
    public static boolean isBusy(CommandSender sender) {
        boolean busy = getCurrentQueries() >= MAX_PLAYER_QUERIES;
        if (busy)
            sender.sendMessage(ChatColor.GRAY + "Please wait before using this command");
        return busy;
    }
}
