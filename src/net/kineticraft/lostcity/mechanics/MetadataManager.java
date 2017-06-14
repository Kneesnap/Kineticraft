package net.kineticraft.lostcity.mechanics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

/**
 * Manages Player Metadata
 *
 * Created by Kneesnap on 6/1/2017.
 */
public class MetadataManager extends Mechanic {

    @AllArgsConstructor @Getter
    public enum Metadata {

        // Players
        TELEPORTING(false),
        LAST_WHISPER(null),
        COMPASS_DEATH(0),

        // Entities
        NORMAL_SPEED(.1F), // Normal Walkspeed.
        PLAYER_DAMAGE(0D);

        private Object fallback;

        /**
         * Does this not have a default value by a default enum class?
         * @return
         */
        public boolean isEnumClass() {
            return getFallback() instanceof Class<?>;
        }

        /**
         * Returns the key this metadata will be stored as.
         */
        public String getKey() {
            return name();
        }

        /**
         * Gets the default value as a metadata value.
         * @return
         */
        public MetadataValue getDefaultValue() {
            return new FixedMetadataValue(Core.getInstance(), getFallback());
        }
    }


    /**
     * Does this object have the given metadata?
     * @param obj
     * @param type
     * @return
     */
    public static boolean hasMetadata(Metadatable obj, Metadata type) {
        return obj.hasMetadata(type.getKey());
    }

    /**
     * Get a metadata value from a player.
     * @param metadatable
     * @return
     */
    public static MetadataValue getMetadata(Metadatable metadatable, Metadata type) {
        return hasMetadata(metadatable, type) ? metadatable.getMetadata(type.getKey()).get(0)
                : (type.isEnumClass() ? null : type.getDefaultValue());
    }

    /**
     * Remove metadata from the given object.
     * @param obj
     * @param type
     */
    public static void removeMetadata(Metadatable obj, Metadata type) {
        obj.removeMetadata(type.getKey(), Core.getInstance());
    }

    /**
     * Set a metadata value.
     * @param metadata
     * @param type
     * @param o
     */
    public static void setMetadata(Metadatable metadata,  Metadata type, Object o) {
        if (o instanceof Enum<?>)
            o = ((Enum<?>)o).name();
        metadata.setMetadata(type.getKey(), new FixedMetadataValue(Core.getInstance(), o));
    }

    /**
     * Enact a cooldown on the given type.
     *
     * IMPORTANT:
     * Cooldowns are seperate from the metadata enum because the metadata enum will get removed on logout.
     * Also, it's stupid to add them to the enum.
     *
     * @param obj
     * @param cooldown
     * @param ticks
     */
    public static void setCooldown(Metadatable obj, String cooldown, int ticks) {
        obj.setMetadata(cooldown, new FixedMetadataValue(Core.getInstance(), System.currentTimeMillis() + (ticks * 50)));
    }

    /**
     * Does this object have a cooldown?
     */
    public static boolean hasCooldown(Metadatable obj, String cooldown) {
        return obj.hasMetadata(cooldown) ? obj.getMetadata(cooldown).get(0).asLong() > System.currentTimeMillis() : false;
    }

    /**
     * Does this player have a cooldown? Alerts them if they do.
     * @param player
     * @param cooldown
     * @return hasCooldown
     */
    public static boolean alertCooldown(Player player, String cooldown) {
        boolean has = hasCooldown(player, cooldown);
        if (has)
            player.sendMessage(ChatColor.RED + "You must wait " + Utils.formatTime(player.getMetadata(cooldown).get(0)
                    .asLong() - System.currentTimeMillis()) + " before doing this.");
        return has;
    }
}
