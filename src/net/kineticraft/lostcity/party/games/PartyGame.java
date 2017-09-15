package net.kineticraft.lostcity.party.games;

import lombok.Getter;
import net.kineticraft.lostcity.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        //TODO: Teleport player to exit.
        broadcast(player.getName() + " has left", false);
    }

    /**
     * Start this game.
     */
    public void start() {
        if (isGoing())
            return;
        //TODO: Start stuff.
        onStart();
        going = true;
    }

    /**
     * Stop this game.
     */
    public void stop() {
        if (!isGoing())
            return;
        //TODO: Stop Stuff
        onStop();
        going = false;
    }

    /**
     * Add a player to this game.
     * @param player
     */
    public void addPlayer(Player player) {
        if (isPlaying(player)) {
            player.sendMessage(ChatColor.RED + "You are already playing this game?");
            return;
        }

        getPlayers().add(player);
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
        onLeave(player);
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
     * Broadcast a party message.
     * @param message
     */
    protected void broadcast(String message) {
        broadcast(message, true);
    }

    /**
     * Broadcast a party message.
     * @param message - The message to send.
     * @param serverWide - Should this message get sent to everyone, or just people playing this game?
     */
    protected void broadcast(String message, boolean serverWide) {
        String fullMsg = ChatColor.YELLOW + "[" + getName() + "] " + ChatColor.BLUE + message;
        if (serverWide) {
            Bukkit.broadcastMessage(fullMsg);
        } else {
            getPlayers().forEach(p -> p.sendMessage(fullMsg));
        }
    }
}
