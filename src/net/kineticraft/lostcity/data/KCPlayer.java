package net.kineticraft.lostcity.data;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.Home;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

/**
 * PlayerData - Allows for loading and saving of player data.
 *
 * Created May 26th, 2017.
 * @author Kneesnap
 */
@Getter @Setter
public class KCPlayer implements Jsonable {

    @Getter
    private static Map<UUID, KCPlayer> playerMap = new HashMap<>();

    private UUID uuid;
    private JsonList<Home> homes = new JsonList<>();
    private EnumRank rank;
    private String icon;
    private JsonData loadedData;

    public KCPlayer(UUID uuid, JsonData data) {
        this.setUuid(uuid);
        load(data);
    }

    /**
     * Is this player at least the given rank? If they're not, it alerts them they don't have permission.
     * @param rank
     * @return
     */
    public boolean isRank(EnumRank rank) {
        boolean hasPerms = getRank().isAtLeast(rank);
        if (!hasPerms && isOnline())
            getPlayer().sendMessage(ChatColor.RED + "You must be at least rank " + rank.getName() + " to do this.");
        return hasPerms;
    }

    /**
     * Is this player currently online?
     */
    public boolean isOnline() {
        Player p = getPlayer();
        return p != null && p.isOnline();
    }

    /**
     * Get the player object associated with this data, if online.
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(getUuid());
    }

    /**
     * Save our playerdata to disk.
     */
    public void writeData() {
        save().toFile(getFile(getUuid()));
    }

    /**
     * Returns this player's last known IP.
     * @return
     */
    public String getLastIP() {
        return isOnline() ? getPlayer().getAddress().toString().split("/")[1].split(":")[0]
                : getLoadedData().getString("lastIp");
    }

    /**
     * Set a player's rank.
     * @param newRank
     */
    public void setRank(EnumRank newRank) {
        if (!getRank().isAtLeast(newRank)) // Broadcast the new rank if it's a promotion.
            Bukkit.broadcastMessage(ChatColor.GREEN + " * " + ChatColor.YELLOW + getUsername() + ChatColor.GREEN
                    + " has ranked up to " + newRank.getColor() + newRank.getName() + ChatColor.GREEN + ". * ");

        if (isOnline()) {
            // Tell the player they've been promoted.
            Player player = getPlayer();
            player.sendMessage(ChatColor.YELLOW + "Your rank is now: " + newRank.getColor() + newRank.getName());
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
        }

        this.rank = newRank;
    }

    /**
     * Gets this player's last seen username.
     * @return
     */
    public String getUsername() {
        return isOnline() ? getPlayer().getName() : getLoadedData().getString("username");
    }

    /**
     * Get a player's data. Loads it if it is not present.
     * @param player
     * @return
     */
    public static KCPlayer getWrapper(OfflinePlayer player) {
        return getWrapper(player.getUniqueId());
    }

    /**
     * Get a player's data. Loads it if it is not present.
     * @param uuid
     * @return
     */
    public static KCPlayer getWrapper(UUID uuid) {
        if (!playerMap.containsKey(uuid))
            playerMap.put(uuid, loadWrapper(uuid));
        return playerMap.get(uuid);
    }

    /**
     * Does this UUID match a saved wrapper on disk?
     * @param uuid
     */
    public static boolean isWrapper(UUID uuid) {
        return getFile(uuid).exists();
    }

    /**
     * Loads a KCPlayer from disk. Creates a new one if they doesn't exist.
     * @param uuid
     */
    public static KCPlayer loadWrapper(UUID uuid) {
        return new KCPlayer(uuid, isWrapper(uuid) ? JsonData.fromFile(getFile(uuid)) : new JsonData());
    }

    private static File getFile(UUID uuid) {
        return new File(Core.getPlayerStoragePath() + uuid.toString() + ".json");
    }

    @Override
    public void load(JsonData data) {
        setLoadedData(data);
        setHomes(data.getList("homes", Home.class));
        this.rank = data.getEnum("rank", EnumRank.MU);
        setIcon(data.getString("icon"));
    }

    @Override
    public JsonData save() {
        JsonData data = new JsonData();
        data.setString("uuid", getUuid().toString());
        data.setString("lastIp", getLastIP());
        data.setString("username", getUsername());
        data.setElement("homes", getHomes().toJson());
        data.setEnum("rank", getRank());
        data.setString("icon", getIcon());
        return data;
    }
}