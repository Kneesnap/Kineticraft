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
 * Manages Player Metadata
 *
 * Created by Kneesnap on 6/1/2017.
 */
public class MetadataManager extends Mechanic {

    @Getter private static CustomStoreBase storeBase = new CustomStoreBase();

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
        return hasMetadata(metadatable, type) ? metadatable.getMetadata(type.getKey()).get(0) : type.getDefaultValue();
    }

    /**
     * Remove metadata from the given object.
     * @param obj
     * @param type
     */
    @SuppressWarnings("unchecked")
    public static <T> T removeMetadata(Metadatable obj, Metadata type) {
        T value = (T) getMetadata(obj, type).value();
        obj.removeMetadata(type.getKey(), Core.getInstance());
        return value;
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
