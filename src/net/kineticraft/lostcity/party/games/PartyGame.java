package net.kineticraft.lostcity.party.games;

import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.party.Parties;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Set;

/**
 * A party game base.
 * Game types: single player, free join (boost), minimum count.
 * Created by Kneesnap on 9/14/2017.
 */
@Getter
public class PartyGame implements Listener {
    private Set<Player> players;
    private boolean going;
    private Set<Location> spawnLocations;
    private Location exitLocation;

    public PartyGame() {
        Bukkit.getPluginManager().registerEvents(this, Core.getInstance()); // Register this.
    }

    /**
     * Called when this game ends.
     */
    protected void onStop() {

    }

    /**
     * Called when this game is started.
     */
    protected void onStart() {

    }

    /**
     * Called when a player joins this game.
     * @param player
     */
    protected void onJoin(Player player) {

    }

    /**
     * Called when a player leaves this game.
     * @param player
     */
    protected void onLeave(Player player) {

    }

    /**
     * Can a player be added to this game?
     * @param player
     * @return canAdd
     */
    protected boolean canAdd(Player player) {
        return true;
    }

    /**
     * Start this game.
     */
    public void start() {
        if (isGoing())
            return;
        getPlayers().forEach(this::spawnPlayer);
        onStart();
        going = true;
    }

    /**
     * Stop this game.
     */
    public void stop() {
        if (!isGoing())
            return;
        getPlayers().forEach(p -> p.teleport(getExitLocation()));
        onStop();
        getPlayers().clear();
        going = false;
    }

    /**
     * Add a player to this game.
     * @param player
     */
    public void addPlayer(Player player) {
        if (isPlaying(player)) {
            player.sendMessage(ChatColor.RED + "You are already playing this game.");
            return;
        }

        if (!canAdd(player))
            return;

        getPlayers().add(player);
        player.setGameMode(GameMode.SURVIVAL);
        onJoin(player);
    }

    /**
     * Remove a player from this game.
     * @param player
     */
    public void removePlayer(Player player) {
        if (!isPlaying(player))
            return;
        getPlayers().remove(player);
        if (getExitLocation() != null)
            player.teleport(getExitLocation());
        broadcastPlayers(player.getName() + " has left");
        onLeave(player);
    }

    /**
     * Get the party-world.
     * @return partyWorld
     */
    protected World getWorld() {
        return Parties.getPartyWorld();
    }

    /**
     * Is a given player currently taking part of this minigame?
     * @param player
     * @return isPlaying
     */
    public boolean isPlaying(Player player) {
        return getPlayers().contains(player);
    }

    /**
     * Get this game's name.
     * @return gameName
     */
    public String getName() {
        return getClass().getSimpleName();
    }

    /**
     * Get the prefix that shows for this game for messaging.
     * @return prefix
     */
    protected String getPrefix() {
        return "[" + getName() + "]";
    }

    /**
     * Format a game-message for chat.
     * @param message
     * @return formatted
     */
    protected String format(String message) {
        return ChatColor.YELLOW + getPrefix() + " " + ChatColor.BLUE + message;
    }

    /**
     * Broadcast a party message.
     * @param message
     */
    protected void broadcast(String message) {
        broadcast(message, true);
    }

    /**
     * Broadcast a party message to all players in the game.
     * @param message
     */
    protected void broadcastPlayers(String message) {
        broadcast(message, false);
    }

    /**
     * Broadcast a party message.
     * @param message - The message to send.
     * @param serverWide - Should this message get sent to everyone, or just people playing this game?
     */
    private void broadcast(String message, boolean serverWide) {
        String fullMsg = format(message);
        if (serverWide) {
            Bukkit.broadcastMessage(fullMsg);
        } else {
            getPlayers().forEach(p -> p.sendMessage(fullMsg));
        }
    }

    /**
     * Spawns a player into the game at a random location.
     * @param player
     */
    protected void spawnPlayer(Player player) {
        Location loc = Utils.randElement(getSpawnLocations());
        if (loc == null)
            return;
        loc.setWorld(Parties.getPartyWorld());
        player.teleport(loc);
    }

    /**
     * Add a spawn location.
     * @param x
     * @param y
     * @param z
     * @param yaw
     * @param pitch
     */
    protected void addSpawnLocation(double x, double y, double z, float yaw, float pitch) {
        getSpawnLocations().add(new Location(null, x, y, z, yaw, pitch));
    }

    /**
     * Set the exit location for this game.
     */
    protected void setExit(double x, double y, double z, float yaw, float pitch) {
        exitLocation = new Location(Parties.getPartyWorld(), x, y, z, yaw, pitch);
    }
}
