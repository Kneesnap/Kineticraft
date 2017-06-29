package net.kineticraft.lostcity.utils;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Contains static player utilities
 *
 * Created by Kneesnap on 6/10/2017.
 */
public class PlayerUtils {

    /**
     * Sets the player walk speed in a manner where resetSpeed will function.
     * @param player
     * @param speed
     */
    public static void setPlayerSpeed(Player player, float speed) {
        if (player.getWalkSpeed() != speed)
            player.setWalkSpeed(speed);
    }

    /**
     * Reset a player's walk speed.
     * @param player
     */
    public static void resetSpeed(Player player) {
        setPlayerSpeed(player, .1F);
    }

    /**
     * Get the CraftPlayer object for this player.
     * @param player
     * @return craftPlayer
     */
    public static CraftPlayer getCraftPlayer(Player player) {
        return (CraftPlayer) player;
    }

    /**
     * Get the NMS player object of a bukkit player.
     * @param player
     * @return nmsPlayer
     */
    public static EntityPlayer getNMSPlayer(Player player) {
        return getCraftPlayer(player).getHandle();
    }
}
