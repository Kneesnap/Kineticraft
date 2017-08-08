package net.kineticraft.lostcity.mechanics.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.Core;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

/**
 * List of all metadata in our custom system.
 *
 * Created by Kneesnap on 6/28/2017.
 */
@AllArgsConstructor @Getter
public enum Metadata {

    // Players
    TELEPORTING(false),
    LAST_WHISPER(null),
    COMPASS_DEATH(0),
    QUIT(false),
    VANISH_TIME(0L),

    // Armor Stands
    NO_MODIFY(false),

    // Entities
    CUTSCENE_KEEP(false),
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
