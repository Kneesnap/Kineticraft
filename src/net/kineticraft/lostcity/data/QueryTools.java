package net.kineticraft.lostcity.data;

import lombok.Getter;
import net.kineticraft.lostcity.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * An API that allows for easily querying of all offline playerdata.
 *
 * Created by Kneesnap on 5/29/2017.
 */
public class QueryTools {

    @Getter private static int currentQueries;

    private static final int MAX_PLAYER_QUERIES = 2;

    /**
     * Asynchronously loads all playerdata then runs the callback.
     * @param callback
     */
    @SuppressWarnings("ConstantConditions")
    public static void queryData(Consumer<Stream<KCPlayer>> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(),
                () -> callback.accept(KCPlayer.getPlayerMap().values().stream()));
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
