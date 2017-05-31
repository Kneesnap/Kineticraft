package net.kineticraft.lostcity.data;

import net.kineticraft.lostcity.Core;
import org.bukkit.Bukkit;
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

    private static int FILES_PER_TICK = 20;

    /**
     * Asynchronously loads all playerdata then runs the callback.
     * @param callback
     */
    public static void queryData(Consumer<Stream<KCPlayer>> callback) {

        final BukkitTask[] task = new BukkitTask[1]; // We use int[] because it can be final while allowing us to change the value.
        List<UUID> check = Arrays.stream(new File(Core.getPlayerStoragePath()).listFiles())
                .filter(file -> file.getName().endsWith(".json")).map(f -> f.getName().split("\\.")[0])
                .map(UUID::fromString).collect(Collectors.toList()); // Get a list of all UUIDs to check.
        List<KCPlayer> loaded = new ArrayList<>();

        task[0] = Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), () -> {
            for (int i = 0; i < FILES_PER_TICK; i++) {
                if (check.isEmpty())
                    break; // We've finished loading :)
                loaded.add(KCPlayer.loadWrapper(check.get(0)));
                check.remove(0);
            }

            if (check.isEmpty()) {
                // Done searching, time to perform operations.
                task[0].cancel(); // Cancel this load task.
                Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), () -> callback.accept(loaded.stream()));
            }
        }, 0, 1);
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
        queryData(str -> {
            KCPlayer p = str.filter(kc -> kc.getUsername().equalsIgnoreCase(username)).findAny().orElse(null);
            if (p == null) {
                if (fail != null)
                    fail.run(); // Oh no, we couldn't find anyone matching this.
                return;
            }

            callback.accept(p);
            p.writeData(); // Save back to disk.
        });
    }
}
