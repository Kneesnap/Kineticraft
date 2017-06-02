package net.kineticraft.lostcity.mechanics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.Core;
import org.bukkit.Bukkit;
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

        TELEPORTING(false);

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
     * Enact a cooldown on the given type.
     * @param obj
     * @param type
     * @param ticks
     */
    public static void setCooldown(Metadatable obj, Metadata type, int ticks) {
        setMetadata(obj, type, true);
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> removeMetadata(obj, type), ticks); // Remove.
    }

    /**
     * Does this object have a cooldown?
     */
    public static boolean hasCooldown(Metadatable obj, Metadata type) {
        return getMetadata(obj, type).asBoolean();
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
}
