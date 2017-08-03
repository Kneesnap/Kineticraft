package net.kineticraft.lostcity.entity;

import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.kineticraft.lostcity.mechanics.system.BuildType;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
import net.kineticraft.lostcity.mechanics.system.Restrict;
import net.kineticraft.lostcity.utils.ReflectionUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles entities spawned with Citizens.
 * Created by Kneesnap on 8/2/2017.
 */
@Restrict(BuildType.PRODUCTION) @Getter
public class Entities extends Mechanic {
    private static List<CustomEntity> entities = new ArrayList<>();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityRemove(EntityDeathEvent evt) {
        CustomEntity ce = getCustom(evt.getEntity());
        if (ce != null) {
            ce.onDeath();
            entities.remove(ce);
        }
    }

    public static <T extends CustomEntity> T spawnEntity(Class<T> clazz, Location loc) {
        T e = ReflectionUtil.construct(clazz, loc);
        entities.add(e);
        return e;
    }

    /**
     * Spawn an NPC.
     * @param type
     * @param name
     * @return npc
     */
    public static NPC spawnNPC(EntityType type, String name) {
        return CitizensAPI.getNPCRegistry().createNPC(type, name);
    }

    /**
     * Get if this entity is a custom NPC.
     * @param e
     * @return isNpc
     */
    public static boolean isNPC(Entity e) {
        return CitizensAPI.getNPCRegistry().isNPC(e);
    }

    /**
     * Get a custom entity by its bukkit object.
     * @param e
     * @return customEntity
     */
    public static CustomEntity getCustom(Entity e) {
        NPC n = CitizensAPI.getNPCRegistry().getNPC(e);
        return entities.stream().filter(ce -> ce.getNPC().equals(n)).findAny().orElse(null);
    }
}
