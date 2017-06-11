package net.kineticraft.lostcity.mechanics;

import lombok.AllArgsConstructor;
import net.kineticraft.lostcity.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Handles Callbacks of all types.
 *
 * Created by Kneesnap on 6/2/2017.
 */
public class Callbacks extends Mechanic {

    private static Map<Player, Map<ListenerType, Listener<?>>> listeners = new HashMap<>();

    public static String CANCEL_MESSAGE = ChatColor.RED.toString() + ChatColor.BOLD + "CANCELLED";

    /**
     * Listen for a number.
     * @param player
     * @param listener
     */
    public static void listenForNumber(Player player, Consumer<Integer> listener) {
        listenForNumber(player, listener, () -> player.sendMessage(CANCEL_MESSAGE));
    }

    /**
     * Listen for a number.
     * @param player
     * @param listener
     * @param fail
     */
    public static void listenForNumber(Player player, Consumer<Integer> listener, Runnable fail) {
        listenForNumber(player, Integer.MIN_VALUE, Integer.MAX_VALUE, listener, fail);
    }

    /**
     * Listen for a number.
     * @param player
     * @param min
     * @param max
     * @param listen
     */
    public static void listenForNumber(Player player, int min, int max, Consumer<Integer> listen) {
        listenForNumber(player, min, max, listen, () -> player.sendMessage(CANCEL_MESSAGE));
    }

    /**
     * Listen for a number.
     * @param player
     * @param min
     * @param max
     * @param listen
     * @param fail
     */
    public static void listenForNumber(Player player, int min, int max, Consumer<Integer> listen, Runnable fail) {
        listenForChat(player, m -> {
            try {
                int num = Integer.parseInt(m);
                if (num > max || num < min) {
                    player.sendMessage(ChatColor.RED.toString() + num + " is not in range [" + min + "," + max + "].");
                    fail.run();
                    return;
                }

                listen.accept(num);
            } catch (NumberFormatException nfe) {
                player.sendMessage(ChatColor.RED + "'" + m + "' is not a valid number.");
                fail.run();
            }
        }, fail::run);
    }

    /**
     * Listens for the player's next chat message.
     * @param player
     * @param message
     */
    public static void listenForChat(Player player, Consumer<String> message) {
        listenForChat(player, message, null);
    }

    /**
     * Listens for the next chat message the player sends. Handles Async scheduling.
     * @param player
     * @param cb
     * @param fail
     */
    public static void listenForChat(Player player, Consumer<String> cb, Runnable fail) {
        if (GUIManager.getGUI(player) != null) // Don't allow the previous GUI to open, since we're giving input.
            GUIManager.getGUI(player).setParent(true);
        player.closeInventory(); // Close the player's open inventory, if any.
        Consumer<String> deAsync = chat -> Bukkit.getScheduler().runTask(Core.getInstance(), () -> cb.accept(chat));
        listen(player, ListenerType.CHAT, deAsync, fail);
    }

    /**
     * Gets the next entity the player clicks.
     * @param player
     * @param click
     */
    public static void selectEntity(Player player, Consumer<Entity> click) {
        selectEntity(player, click, () -> player.sendMessage(CANCEL_MESSAGE));
    }

    /**
     * Get the next mob the player clicks.
     * @param player
     * @param click
     * @param fail
     */
    public static void selectEntity(Player player, Consumer<Entity> click, Runnable fail) {
        listen(player, ListenerType.ENTITY, click, fail);
    }

    /**
     * Apply a listener to the player. If one already exists of the same type, it will be cancelled.
     * @param player
     * @param type
     * @param callback
     * @param failCallback
     */
    private static <T> void listen(Player player, ListenerType type,  Consumer<T> callback, Runnable failCallback) {
        if (hasListener(player, type)) // Fail any existing listeners of this type.
            getListener(player, type).fail();

        listeners.putIfAbsent(player, new HashMap<>());
        listeners.get(player).put(type, new Listener(callback, failCallback));
    }

    /**
     * Fire a listener callback, and unregister the listener.
     * Returns whether or not a listener was accepted.
     *
     * @param player
     * @param type
     * @param obj
     */
    private static boolean accept(Player player, ListenerType type, Object obj) {
        if (!hasListener(player, type))
            return false;

        getListener(player, type).accept(obj);
        listeners.get(player).remove(type);
        return true;
    }

    /**
     * Cancel any listener of the given type.
     *
     * @param player
     * @param type
     */
    public static void cancel(Player player, ListenerType type) {
        if (!hasListener(player, type))
            return;

        getListener(player, type).fail();
        listeners.get(player).remove(type);
    }

    /**
     * Get any listener that's currently listening on the player, if any.
     * @param player
     * @param type
     * @return
     */
    private static Listener getListener(Player player, ListenerType type) {
        return listeners.get(player) != null ? listeners.get(player).get(type) : null;
    }

    /**
     * Is this player currently being listened on?
     * @param player
     * @param type
     * @return
     */
    public static boolean hasListener(Player player, ListenerType type) {
        return getListener(player, type) != null;
    }

    public enum ListenerType {
        CHAT,
        ENTITY;
    }

    @AllArgsConstructor
    private static class Listener<T> {
        private Consumer<T> success;
        private Runnable fail;

        /**
         * Calls on callback success
         */
        public void accept(T value) {
            if (success != null)
                success.accept(value);
        }

        /**
         * Calls on callback failure.
         */
        public void fail() {
            if (fail != null)
                fail.run();
        }
    }

    @Override
    public void onQuit(Player player) {
        Arrays.stream(ListenerType.values()).forEach(type -> cancel(player, type)); // Cancel all listeners.
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent evt) {
        if (accept(evt.getPlayer(), ListenerType.CHAT, evt.getMessage()))
            evt.setCancelled(true); // Handles chat callbacks.
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEntityEvent evt) {
        if (accept(evt.getPlayer(), ListenerType.ENTITY, evt.getRightClicked()))
            evt.setCancelled(true); // Handles entity click callbacks.
    }
}
