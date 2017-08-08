package net.kineticraft.lostcity.cutscenes.actions.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.item.ItemManager;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Stream;

/**
 * A registry of entity types and possible information for their displays.
 * Created by Kneesnap on 7/22/2017.
 */
@AllArgsConstructor @Getter
public enum EntityDisplay {

    ZOMBIE("Zombie", Sound.ENTITY_ZOMBIE_HURT),
    CREEPER("Creeper", Sound.ENTITY_CREEPER_HURT),
    VILLAGER("Villager", Sound.ENTITY_VILLAGER_AMBIENT),
    PLAYER("Steve", Sound.ENTITY_VILLAGER_TRADING),
    UNKNOWN("Alex", null);

    private final String skullName;
    private final Sound talkSound;

    /**
     * Get the skull for this display.
     * @return skull
     */
    public ItemStack getSkull() {
        return ItemManager.makeSkull(getSkullName());
    }

    /**
     * Get the EntityType associated with this display.
     * @return type
     */
    public EntityType getType() {
        return EntityType.valueOf(name());
    }

    /**
     * Get the EntityDisplay for a given EntityType.
     * @param type
     * @return display.
     */
    public static EntityDisplay getByType(EntityType type) {
        return Stream.of(values()).filter(d -> d.getType() == type).findFirst().orElse(UNKNOWN);
    }
}
