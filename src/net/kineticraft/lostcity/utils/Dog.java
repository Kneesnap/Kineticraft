package net.kineticraft.lostcity.utils;

import net.kineticraft.lostcity.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * Send a server message with a given personality.
 *
 * Created by Kneesnap on 6/17/2017.
 */
public enum Dog {

    KINETICA,
    PUPPER_PATROL,
    OFFICER_BORKLEY;

    /**
     * Get the chat name of this doggo.
     * @return chatName
     */
    public String getName() {
        return Utils.capitalize(name());
    }

    /**
     * Send a message as this doggo.
     * @param message
     */
    public void say(String message) {
        Core.broadcast(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "DOG" + ChatColor.GREEN + " "
                + getName() + ChatColor.GRAY + ": " + ChatColor.WHITE + message);
    }
}
