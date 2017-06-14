package net.kineticraft.lostcity.data.wrappers;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.data.JsonData;
import net.kineticraft.lostcity.data.lists.JsonList;
import net.kineticraft.lostcity.data.lists.StringList;
import net.kineticraft.lostcity.data.maps.JsonMap;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.mechanics.MetadataManager;
import net.kineticraft.lostcity.mechanics.MetadataManager.Metadata;
import net.kineticraft.lostcity.mechanics.Vanish;
import net.kineticraft.lostcity.mechanics.Voting;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
    private JsonMap<JsonLocation> homes = new JsonMap<>();
    private JsonList<JsonLocation> deaths = new JsonList<>();
    private StringList notes = new StringList();
    private StringList mail = new StringList();
    private StringList ignored = new StringList();
    private EnumRank rank;
    private String icon;
    private JsonData loadedData;
    private int monthlyVotes;
    private int totalVotes;
    private long lastVote;
    private int pendingVotes;
    private int secondsPlayed;
    private Particle effect;
    private boolean vanished;
    private int accountId;

    public KCPlayer(UUID uuid, JsonData data) {
        this.setUuid(uuid);
        load(data);
    }

    /**
     * Is this player invisible (for commands) to this CommandSender?
     * @param sender
     * @return vanished.
     */
    public boolean isVanished(CommandSender sender) {
        return isVanished() && !Utils.getRank(sender).isAtLeast(EnumRank.MEDIA);
    }

    /**
     * Is this player invisible to other players?
     * Either by being vanished or in gm3.
     *
     * @return hidden
     */
    public boolean isHidden() {
        return isVanished() || getPlayer().getGameMode() == GameMode.SPECTATOR;
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
     * Gets the death the player has selected.
     * @return death
     */
    public JsonLocation getSelectedDeath() {
        return getDeaths().getValueSafe(MetadataManager.getMetadata(getPlayer(), Metadata.COMPASS_DEATH).asInt());
    }

    /**
     * Is this player the top voter?
     * @return topVoter
     */
    public boolean isTopVoter() {
        return getUuid().equals(Configs.getVoteData().getTopVoter());
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
        save().toFile(getPath(getUuid()));
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
            updatePlayer();
        }

        this.rank = newRank;
    }

    /**
     * Performs updates on this player such as attempting to give vote rewards, updating player tablist name,
     * and sending the "you have mail" message
     */
    public void updatePlayer() {
        Player player = getPlayer();
        if (player == null)
            return; // This only applies to online players.

        player.setPlayerListName(getDisplayName()); // Update tab name.
        Voting.giveRewards(player); // Give vote rewards, if any.

        if (!getMail().isEmpty())
            player.sendMessage(ChatColor.GOLD + "You have " + ChatColor.RED + getMail().size() + ChatColor.GOLD
                    + " unread messages. Use /mail to read them.");
    }

    /**
     * Vanish or unvanish this player.
     * @param vanishState
     */
    public void vanish(boolean vanishState) {
        setVanished(vanishState);
        Vanish.hidePlayers(getPlayer());
    }

    /**
     * Gets the player's temporary rank, if none exist than it returns the normal rank.
     * @return tempRank
     */
    public EnumRank getTemporaryRank() {
        return isTopVoter() ? EnumRank.VOTER : getRank();
    }

    /**
     * Gets the user's display prefix.
     * @return displayPrefix
     */
    public String getDisplayPrefix() {
        return getIcon() != null ? getTemporaryRank().getColor() + getIcon() + getTemporaryRank().getNameColor()
                : getTemporaryRank().getChatPrefix();
    }

    /**
     * Get the full display name of this player.
     * @return displayName
     */
    public String getDisplayName() {
        return getDisplayPrefix() + " " + getUsername();
    }

    /**
     * Gets this player's last seen username.
     * @return username
     */
    public String getUsername() {
        return isOnline() ? getPlayer().getName() : getLoadedData().getString("username");
    }

    /**
     * Returns this player's name with the extra color tag.
     * @return coloredName
     */
    public String getColoredName() {
        return getTemporaryRank().getNameColor() + getUsername();
    }

    /**
     * Get a player's data. Loads it if it is not present.
     * @param player
     * @return playerData
     */
    public static KCPlayer getWrapper(OfflinePlayer player) {
        return player != null ? getWrapper(player.getUniqueId()) : null;
    }

    /**
     * Get a player's data. Loads it if it is not present.
     * @param uuid
     * @return playerWrapper
     */
    public static KCPlayer getWrapper(UUID uuid) {
        return playerMap.containsKey(uuid) ? playerMap.get(uuid) : loadWrapper(uuid);
    }

    /**
     * Does this UUID match a saved wrapper on disk?
     * @param uuid
     */
    public static boolean isWrapper(UUID uuid) {
        return JsonData.isJson(getPath(uuid));
    }

    /**
     * Loads a KCPlayer from disk. Creates a new one if they doesn't exist.
     * @param uuid
     */
    public static KCPlayer loadWrapper(UUID uuid) {
        return new KCPlayer(uuid, isWrapper(uuid) ? JsonData.fromFile(getPath(uuid)) : new JsonData());
    }

    /**
     * Get an online user's data by its account id.
     * @param aId
     * @return player
     */
    public static KCPlayer getById(int aId) {
        return Bukkit.getOnlinePlayers().stream().map(KCPlayer::getWrapper).filter(kc -> kc.getAccountId() == aId).findAny().orElse(null);
    }

    /**
     * Generates a new account id.
     * @return aId
     */
    private static int generateNewId() {
        return Core.getFile("players/").listFiles().length + 1;
    }

    private static String getPath(UUID uuid) {
        return "players/" + uuid.toString();
    }

    @Override
    public void load(JsonData data) {
        setLoadedData(data);
        setHomes(data.getMap("homes", JsonLocation.class));
        setDeaths(data.getJsonList("deaths", JsonLocation.class));
        this.rank = data.getEnum("rank", EnumRank.MU); // Don't use setRank() because it runs extra code.
        setIcon(data.getString("icon"));
        setMonthlyVotes(data.getInt("monthlyVotes"));
        setTotalVotes(data.getInt("totalVotes"));
        setPendingVotes(data.getInt("pendingVotes"));
        setNotes(data.getList("notes", StringList.class));
        setMail(data.getList("mail", StringList.class));
        setIgnored(data.getList("ignored", StringList.class));
        setSecondsPlayed(data.getInt("secondsPlayed"));
        setEffect(data.getEnum("effect", Particle.class));
        setVanished(data.getBoolean("vanish"));
        setLastVote(data.getLong("lastVote"));
        setAccountId(data.getInt("accountId", generateNewId()));

        if (getAccountId() == generateNewId())
            writeData(); // Save immediately if we're a new player, as generateNewId goes off filecount.
    }

    @Override
    public JsonData save() {
        JsonData data = new JsonData();
        data.setUUID("uuid", getUuid());
        data.setString("lastIp", getLastIP());
        data.setString("username", getUsername());
        data.setElement("homes", getHomes());
        data.setList("deaths", getDeaths());
        data.setEnum("rank", getRank());
        data.setString("icon", getIcon());
        data.setNum("monthlyVotes", getMonthlyVotes());
        data.setNum("totalVotes", getTotalVotes());
        data.setNum("pendingVotes", getPendingVotes());
        data.setList("notes", getNotes());
        data.setList("mail", getMail());
        data.setList("ignored", getIgnored());
        data.setNum("secondsPlayed", getSecondsPlayed());
        data.setEnum("effect", getEffect());
        data.setBoolean("vanish", isVanished());
        data.setNum("accountId", getAccountId());
        data.setNum("lastVote", getLastVote());
        return data;
    }
}