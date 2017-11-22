package net.kineticraft.lostcity.data;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.entities.User;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.discord.DiscordSender;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.data.lists.EnumList;
import net.kineticraft.lostcity.data.lists.JsonList;
import net.kineticraft.lostcity.data.lists.StringList;
import net.kineticraft.lostcity.data.maps.JsonMap;
import net.kineticraft.lostcity.data.reflect.JsonSerializer;
import net.kineticraft.lostcity.discord.DiscordAPI;
import net.kineticraft.lostcity.discord.DiscordChannel;
import net.kineticraft.lostcity.mechanics.DataHandler;
import net.kineticraft.lostcity.mechanics.Toggles.Toggle;
import net.kineticraft.lostcity.mechanics.metadata.MetadataManager;
import net.kineticraft.lostcity.mechanics.Punishments.*;
import net.kineticraft.lostcity.mechanics.Vanish;
import net.kineticraft.lostcity.mechanics.Voting;
import net.kineticraft.lostcity.utils.Dog;
import net.kineticraft.lostcity.utils.TextUtils;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * PlayerData - Allows for loading and saving of player data.
 * Created May 26th, 2017.
 * @author Kneesnap
 */
@Getter @Setter
public class KCPlayer implements Jsonable {

    @Getter private static Map<UUID, KCPlayer> playerMap = new HashMap<>();

    private UUID uuid;
    private int accountId = generateNewId();
    private long discordId;
    private String username;
    private String lastIP;
    private EnumRank rank = EnumRank.MU;
    private String icon;

    private Particle effect;
    private boolean vanished;
    private long zenMode;
    private String nickname;
    private long secondsPlayed;
    private int lastBuild;
    private int monthlyVotes;
    private int totalVotes;
    private long lastVote;
    private int pendingVotes;
    private Location lastLocation;

    private Mute mute;
    private JsonList<Punishment> punishments = new JsonList<>();
    private EnumList<Toggle> toggles = new EnumList<>();
    private JsonMap<Location> homes = new JsonMap<>();
    private JsonList<PlayerDeath> deaths = new JsonList<>();
    private StringList notes = new StringList();
    private StringList mail = new StringList();
    private StringList ignored = new StringList();
    private JsonList<ItemStack> mailbox = new JsonList<>();

    public KCPlayer() {

    }

    public KCPlayer(UUID uuid, String username) {
        setUuid(uuid);
        setUsername(username);
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
     * Mute this player until the given expiry.
     * @param source
     * @param expiry
     * @param reason
     */
    public void mute(CommandSender source, Date expiry, String reason) {
        Dog.PUPPER_PATROL.say(ChatColor.RED + getUsername() + ChatColor.WHITE + " has been muted by " + ChatColor.AQUA + source.getName() + ChatColor.WHITE + ".");
        setMute(new Mute(expiry.getTime(), reason, source.getName()));
        sendMessage(ChatColor.RED + "You have been muted. (" + reason + ")");
        DiscordAPI.sendMessage(DiscordChannel.ORYX, source.getName() + " has muted " + getUsername() + " for " + Utils.formatDate(expiry) + ".");
    }

    /**
     * Send a message to this player if they're online.
     * @param message
     */
    public void sendMessage(String message) {
        if (isOnline())
            getPlayer().sendMessage(message);
    }

    /**
     * Is this player muted?
     * @return muted
     */
    public boolean isMuted() {
        return getMute() != null && !getMute().isExpired();
    }

    /**
     * Punish this player for ban evasion, if they aren't already banned for it.
     */
    public void punishEvasion() {
        for (Punishment p : getPunishments())
            if (p.isValid() && p.getType() == PunishmentType.ALT_ACCOUNT)
                return;
        punish(PunishmentType.ALT_ACCOUNT, Bukkit.getConsoleSender());
    }

    /**
     * Punish this user.
     * If run a-sync it will run next tick.
     *
     * @param type
     * @param punisher
     */
    public void punish(PunishmentType type, CommandSender punisher) {
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(Core.getInstance(), () -> punish(type, punisher));
            return;
        }


        Dog.PUPPER_PATROL.say(ChatColor.RED + getUsername() + ChatColor.WHITE + " has been punished by " + ChatColor.AQUA
                + punisher.getName() + ChatColor.WHITE + " for " + ChatColor.YELLOW + type.getDisplay() + ChatColor.WHITE + ".");

        getPunishments().add(new Punishment(type, punisher.getName()));
        String expiry = Utils.formatTimeFull(getPunishExpiry());
        Dog.OFFICER_BORKLEY.say("Arf Arf! Expires: " + ChatColor.YELLOW + expiry + ChatColor.WHITE + ".");

        if (isOnline()) {
            Player p = getPlayer();
            if (type == PunishmentType.XRAY)
                Utils.toSpawn(p); // Teleport the player to spawn if banned for xray.
            p.kickPlayer(ChatColor.RED + "Oh no! You've been punished for " + ChatColor.YELLOW + type.getDisplay() + ChatColor.RED + "...");
        }
        writeData();
        DiscordAPI.sendMessage(DiscordChannel.ORYX, punisher.getName() + " has punished " + getUsername()
                + " for " + type.getDisplay() + " (" + expiry + ")");
    }

    /**
     * Does this user have a pending punishment?
     * @return banned
     */
    public boolean isBanned() {
        long punishTime = getPunishExpiry();
        return punishTime == -1 || punishTime > 0;
    }

    /**
     * Gets the time in milliseconds when this player's punishments will expire.
     * 0 = not punished
     * -1 = Never
     *
     * @return expiry
     */
    public long getPunishExpiry() {
        long hours;
        List<Punishment> p = getPunishments().stream().filter(Punishment::isValid).collect(Collectors.toList());
        if (p.isEmpty())
            return 0; // If there are no punishments, they're clean.

        Punishment punishment = p.get(p.size() - 1);
        PunishmentType type = punishment.getType();
        switch (p.size()) {
            case 1:
                hours = type.getInitialTime();
                break;
            case 2:
                hours = type.getPunishLength() * 24;
                break;
            case 3:
                hours = ((type.getPunishLength() * 2) + 1) * 24;
                break;
            default:
                return -1;
        }
        return hours > -1 ? punishment.getTimestamp() + (hours * 60 * 60 * 1000) - System.currentTimeMillis() : -1;
    }

    /**
     * Is this player at least the given rank? If they're not, it alerts them they don't have permission.
     * @param rank
     * @return is the user at least the specified rank.
     */
    public boolean isRank(EnumRank rank) {
        boolean hasPerms = getRank().isAtLeast(rank);
        if (!hasPerms)
            sendMessage(ChatColor.RED + "You must be at least rank " + rank.getName() + " to do this.");
        return hasPerms;
    }

    /**
     * Is this player currently online?
     * @return isOnline
     */
    public boolean isOnline() {
        return getPlayer() != null;
    }

    /**
     * Gets the death the player has selected.
     * @return death
     */
    public Location getSelectedDeath() {
        int index = getDeaths().size() - MetadataManager.getValue(getPlayer(), "compassDeath", 1) - 1;
        PlayerDeath death = getDeaths().getValueSafe(index);
        return death != null ? death.getLocation() : null;
    }

    /**
     * Is this player a top voter?
     * @return topVoter
     */
    public boolean isTopVoter() {
        return getUuid().equals(Configs.getVoteData().getTopVoter());
    }

    /**
     * Get the player object associated with this data, if online.
     * @return player
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(getUuid());
    }

    /**
     * Save our playerdata to disk.
     */
    public void writeData() {
        new JsonData(save().getAsJsonObject()).toFile(getPath(getUuid()));
    }

    /**
     * Set a player's rank.
     * @param newRank
     */
    public void setRank(EnumRank newRank) {
        if (!getRank().isAtLeast(newRank)) // Broadcast the new rank if it's a promotion.
            Core.broadcast(ChatColor.GREEN + " * " + ChatColor.YELLOW + getUsername() + ChatColor.GREEN
                    + " has ranked up to " + newRank.getColor() + newRank.getName() + ChatColor.GREEN + ". * ");

        this.rank = newRank;

        if (isOnline()) {
            // Tell the player they've been promoted.
            Player player = getPlayer();
            player.sendMessage(ChatColor.YELLOW + "Your rank is now: " + newRank.getColor() + newRank.getName());
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
            updatePlayer();
        }

        updateDiscord();
    }

    /**
     * Set the new nickname of this player.
     * @param newNick
     */
    public void setNickname(String newNick) {
        this.nickname = newNick;
        sendMessage(ChatColor.GOLD + "Nickname " + (newNick != null ? "updated" : "removed") + ".");
        updatePlayer();
    }

    /**
     * Update this player's account on discord.
     */
    public void updateDiscord() {
        if (!isVerified())
            return; // There's no linked discord account to update.

        DiscordAPI.setNick(getDiscord(), getUsername());
        DiscordAPI.setRoles(getDiscord(), getRank().isStaff() ? "Staff" : "Verified", getRank().getName());
    }

    /**
     * Get the display name of this player.
     * @return displayName
     */
    public String getName() {
        return getNickname() != null ? getNickname() : getUsername();
    }

    /**
     * Performs updates on this player such as attempting to give vote rewards, updating player tablist name,
     * and sending the "you have mail" message
     */
    public void updatePlayer() {
        if (!isOnline())
            return; // This only applies to online players.

        Player player = getPlayer();
        setUsername(player.getName());
        player.setDisplayName(getName());
        getTemporaryRank().getTeam().addEntry(player.getName());
        Voting.giveRewards(player); // Give vote rewards, if any.
        player.setOp(getRank().isAtLeast(EnumRank.BUILDER)); // Grant or remove OP status if the player is of high enough level.

        // Updates data.
        setLastIP(player.getAddress().toString().split("/")[1].split(":")[0]);
        player.addAttachment(Core.getInstance(), "OpenInv.*", getRank().isStaff());

        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            if (!getMail().isEmpty())
                player.sendMessage(ChatColor.GOLD + "You have " + ChatColor.RED + getMail().size() + ChatColor.GOLD
                        + " unread messages. Use /mail to read them.");
            if (!getMailbox().isEmpty()) {
                String l = ChatColor.YELLOW + "✉" + ChatColor.GOLD;
                player.sendMessage(TextUtils.centerChat(l + " You have new mail! Claim it with /mailbox. " + l));
            }
        }, 20L);
    }

    /**
     * Update this player, and save this data to disk.
     */
    public void updateSave() {
        updatePlayer();
        writeData();
    }

    /**
     * Does this player have a given toggle enabled?
     * @param toggle
     * @return state
     */
    public boolean getState(Toggle toggle) {
        return getToggles().contains(toggle) && getRank().isAtLeast(toggle.getMinRank());
    }

    /**
     * Toggle a given toggle.
     * @param toggle
     */
    public void toggle(Toggle toggle) {
        if (!getToggles().remove(toggle))
            getToggles().add(toggle); // Add the toggle if we did not remove it.
        sendMessage(Utils.formatToggle(Utils.capitalize(toggle.name()), getState(toggle)));
        updateToggles();
    }

    /**
     * Update toggles.
     */
    public void updateToggles() {

    }

    /**
     * Are we ignoring the given player?
     * @param sender
     * @return ignoring
     */
    public boolean isIgnoring(CommandSender sender) {
        return getIgnored().containsIgnoreCase(sender.getName());
    }

    /**
     * Vanish or unvanish this player.
     * @param vanishState
     */
    public void vanish(boolean vanishState) {
        setVanished(vanishState);
        Vanish.hidePlayers(getPlayer());
        MetadataManager.setMetadata(getPlayer(), "vanishTime", System.currentTimeMillis());
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
     * Get this player's name with their rank color applied.
     * @return displayName
     */
    public String getColoredUsername() {
        return getTemporaryRank().getChatPrefix() + getUsername();
    }

    /**
     * Returns this player's name with the extra color tag.
     * @return coloredName
     */
    public String getColoredName() {
        return getTemporaryRank().getNameColor() + (getNickname() != null ? getNickname() : getUsername());
    }

    /**
     * Is this user verified on discord?
     * @return verified
     */
    public boolean isVerified() {
        return getDiscordId() != 0L && DiscordAPI.isAlive();
    }

    /**
     * Get this player's discord user.
     * Returns null if not verified.
     *
     * @return user
     */
    public User getDiscord() {
        return isVerified() ? DiscordAPI.getBot().getBot().getUserById(getDiscordId()) : null;
    }

    /**
     * Get a CommandSender's wrapper. Accepts Player, DiscordSender.
     * Will throw a ClassCastException if sender is not one of these types.
     *
     * @param sender
     * @return wrapper
     */
    public static KCPlayer getWrapper(CommandSender sender) {
        return sender instanceof DiscordSender ? getDiscord(((DiscordSender) sender).getUser())
                : getPlayer((Player) sender);
    }

    /**
     * Get a player's wrapper by their name.
     * Will be deprecated at a future date.
     * @param name
     * @return wrapper
     */
    public static KCPlayer getWrapper(String name) {
        return getPlayerMap().values().stream().filter(kc -> name.equalsIgnoreCase(kc.getUsername())).findAny().orElse(null);
    }

    /**
     * Get a player's data. Loads it if it is not present.
     * @param player
     * @return playerData
     */
    public static KCPlayer getPlayer(OfflinePlayer player) {
        return player != null ? getWrapper(player.getUniqueId()) : null;
    }

    /**
     * Get a player's data from discord user.
     * @param user
     * @return player
     */
    public static KCPlayer getDiscord(User user) {
        return user != null ? getWrapper(DataHandler.getDiscordMap().get(user.getIdLong())) : null;
    }

    /**
     * Get a player's data. Loads it if it is not present.
     * @param uuid
     * @return playerWrapper
     */
    public static KCPlayer getWrapper(UUID uuid) {
        return playerMap.get(uuid);
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
        return JsonSerializer.fromJson(KCPlayer.class, JsonData.fromFile(getPath(uuid)).getJsonObject());
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
        return getPlayerMap().size() + 1;
    }

    private static String getPath(UUID uuid) {
        return "players/" + uuid.toString();
    }
}