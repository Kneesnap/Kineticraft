package net.kineticraft.lostcity.mechanics.metadata;

import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

/**
 * Manages Player Metadata.
 * Imported from KataCraft on October 26th, 2017 by Kneesnap.
 */
public class MetadataManager extends Mechanic {
    @Getter private static CustomStoreBase storeBase = new CustomStoreBase();

    /**
     * Does this object have the given metadata?
     * @param obj - Object with metadata
     * @param type - Metadata key
     * @return hasKey
     */
    public static boolean hasMetadata(Metadatable obj, String type) {
        return obj.hasMetadata(type);
    }

    /**
     * Get a metadata value from a player.
     * @param obj - The object with the metadata.
     * @parma key - The key to get the metadata from.
     * @return getMetadata
     */
    public static MetadataValue getMetadata(Metadatable obj, String key) {
        return hasMetadata(obj, key) ? obj.getMetadata(key).get(0) : null;
    }

    /**
     * Get a metadata value as an enum.
     * @param obj - The object to get the value from
     * @param key - The key the value is indexed by.
     * @param clazz - The enum class we want to load.
     * @param <E>
     * @return enumValue
     */
    public static <E extends Enum<E>> E getEnum(Metadatable obj, String key, Class<E> clazz) {
        return Utils.getEnum(getValue(obj, key), clazz);
    }

    /**
     * Get a metadata value from a metadatable object.
     * @param obj - The object with the metadata.
     * @param key - The key the metadata is indexed by.
     * @param <T> - The type to return the value as.
     * @return value
     */
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static <T> T getValue(Metadatable obj, String key) {
        return (T) (hasMetadata(obj, key) ? getMetadata(obj, key).value() : null);
    }

    /**
     * Get a metadata value from a given object, if the metadata key is not present it will return the fallback value.
     * If the fallback value is returned, it is stored as the actual value.
     * @param obj - The object with the metadata.
     * @param key - The key the metadata is indexed by.
     * @param fallback - The fallback value.
     * @param <T>
     * @return value
     */
    public static <T> T getValue(Metadatable obj, String key, T fallback) {
        if (!hasMetadata(obj, key))
            setMetadata(obj, key, fallback);
        return getValue(obj, key);
    }

    /**
     * Remove metadata from the given object.
     * @param obj - Object with the metadata.
     * @param key - The key to remove.
     */
    public static <T> T removeMetadata(Metadatable obj, String key) {
        T value = getValue(obj ,key);
        obj.removeMetadata(key, Core.getInstance());
        return value;
    }

    /**
     * Set a metadata value.
     * @param metadata - The object to set the metadata of.
     * @param key - The key to index the metadata by.
     * @param o - The object value to set.
     */
    public static void setMetadata(Metadatable metadata, String key, Object o) {
        if (o instanceof MetadataValue)
            o = ((MetadataValue) o).value();
        if (o instanceof Enum<?>)
            o = ((Enum<?>) o).name();
        metadata.setMetadata(key, new FixedMetadataValue(Core.getInstance(), o));
    }

    /**
     * Enact a cooldown on the given type.
     *
     * IMPORTANT:
     * Cooldowns are seperate from the metadata enum because the metadata enum will get removed on logout.
     * Also, it's inconvenient to add them to the enum.
     *
     * @param obj
     * @param cooldown
     * @param ticks
     */
    public static void setCooldown(Metadatable obj, String cooldown, int ticks) {
        obj.setMetadata(cooldown, new FixedMetadataValue(Core.getInstance(), System.currentTimeMillis() + (ticks * 50)));
    }

    /**
     * Does this object have an active cooldown?
     * @param obj
     * @param cooldown
     * @return hasCooldown - Only will be true if the cooldown has not expired as well.
     */
    public static boolean hasCooldown(Metadatable obj, String cooldown) {
        return obj.hasMetadata(cooldown) && obj.getMetadata(cooldown).get(0).asLong() > System.currentTimeMillis();
    }

    /**
     * Does this player have a cooldown? Alerts them if they do.
     * @param meta
     * @param cooldown
     * @return hasCooldown
     */
    public static boolean alertCooldown(Metadatable meta, String cooldown) {
        boolean has = hasCooldown(meta, cooldown);
        if (has && meta instanceof CommandSender)
            ((CommandSender) meta).sendMessage(ChatColor.RED + "You must wait "
                    + Utils.formatTime(meta.getMetadata(cooldown).get(0).asLong() - System.currentTimeMillis())
                    + " before doing this.");
        return has;
    }

    /**
     * Checks if this player has the given cooldown, and if they don't, give it to them after telling them they have it.
     * Returns whether or not the player had the cooldown before this was called.
     *
     * @param meta
     * @param cooldown
     * @param ticks
     * @return hasCooldown
     */
    public static boolean updateCooldown(Metadatable meta, String cooldown, int ticks) {
        boolean has = alertCooldown(meta, cooldown);
        if (!has)
            setCooldown(meta, cooldown, ticks);
        return has;
    }

    /**
     * Checks if this player has the given cooldown, and if they don't, give it to them silently.
     * Returns whether or not the player had the cooldown before this was called.
     *
     * @param meta
     * @param cooldown
     * @param ticks
     * @return hasCooldown
     */
    public static boolean updateCooldownSilently(Metadatable meta, String cooldown, int ticks) {
        boolean has = hasCooldown(meta, cooldown);
        if (!has)
            setCooldown(meta, cooldown, ticks);
        return has;
    }
}
