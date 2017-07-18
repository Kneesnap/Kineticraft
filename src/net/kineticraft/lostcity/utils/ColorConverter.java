package net.kineticraft.lostcity.utils;

import org.bukkit.ChatColor;

/**
 * Convert color formats.
 * Created by Kneesnap on 6/12/2017.
 */
public class ColorConverter {

    /**
     * Convert the given color to bungee format.
     * @param color
     * @return bungeeColor
     */
    public static net.md_5.bungee.api.ChatColor toBungee(ChatColor color) {
        return net.md_5.bungee.api.ChatColor.getByChar(color.getChar());
    }
}
