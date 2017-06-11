package net.kineticraft.lostcity.utils;

import net.kineticraft.lostcity.mechanics.MetadataManager;
import net.kineticraft.lostcity.mechanics.MetadataManager.Metadata;
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
        if (player.getWalkSpeed() == speed)
            return;
        MetadataManager.setMetadata(player, Metadata.NORMAL_SPEED, speed);
        player.setWalkSpeed(speed);
    }

    /**
     * Reset a player's walk speed.
     * @param player
     */
    public static void resetSpeed(Player player) {
        if (MetadataManager.hasMetadata(player, Metadata.NORMAL_SPEED))
            setPlayerSpeed(player, MetadataManager.getMetadata(player, Metadata.NORMAL_SPEED).asFloat());
    }
}
