package net.kineticraft.lostcity.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

/**
 * Send a server message with a given personality.
 * Created by Kneesnap on 6/17/2017.
 */
@AllArgsConstructor @Getter
public enum Dog {

    KINETICA(Sound.ENTITY_WOLF_WHINE),
    PUPPER_PATROL(null),
    OFFICER_BORKLEY(Sound.ENTITY_WOLF_AMBIENT);

    private final Sound sound;

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

        if (getSound() != null)
            Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), getSound(), 1, 1.7F));
    }
}
