package net.kineticraft.lostcity.mechanics;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Arrays;
import java.util.List;

/**
 * Prevents farms from being too economically overpowered.
 *
 * Provides Restrictions:
 *  - Limits overpopulating chicken farms.
 *  - Limits the effectiveness of iron / gold / etc farms by requiring players to perform most of the damage to get a drop.
 *
 * Created by Kneesnap on 6/3/2017.
 */
public class FarmLimiter extends Mechanic {


    private static final int MAX_CRAMMING = 32;
    private static final int RADIUS = 8;
    private static List<EntityType> IGNORE = Arrays.asList(EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN);

    @EventHandler
    public void onChickenSpawn(CreatureSpawnEvent evt) {
        if ((evt.getSpawnReason() == SpawnReason.DISPENSE_EGG || evt.getSpawnReason() == SpawnReason.EGG)
                && getEntityCount(evt.getLocation(), EntityType.CHICKEN) >= MAX_CRAMMING)
            evt.setCancelled(true); // There are more chickens here than we allow, don't spawn another one.
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent evt) {
        if (!(evt.getEntity() instanceof Player) && evt.getEntity() instanceof LivingEntity // Not an applicable entity.
                && getPlayerDamage(evt.getEntity()) < getDamageNeeded(evt.getEntity()) // Not enough player damage.
                && !IGNORE.contains(evt.getEntityType())) // Not a type we ignore
            evt.getDrops().clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent evt) {
        Entity e = evt.getEntity();
        if (e instanceof Player || (e instanceof Projectile && ((Projectile) e).getShooter() instanceof Player))
            addPlayerDamage(e, evt.getDamage()); // Count all damage delt by players.
    }


    /**
     * Get the amount of damage inflicted by a player on this entity.
     * @param entity
     * @return
     */
    private static double getPlayerDamage(Entity entity) {
        return MetadataManager.getMetadata(entity, MetadataManager.Metadata.PLAYER_DAMAGE).asDouble();
    }

    /**
     * Add to the amount of player damage inflicted on an entity.
     * @param entity
     * @param damage
     */
    private static void addPlayerDamage(Entity entity, double damage) {
        MetadataManager.setMetadata(entity, MetadataManager.Metadata.PLAYER_DAMAGE, getPlayerDamage(entity) + damage);
    }


    /**
     * Get the amount of required player-inflicted damage to kill this entity.
     * Takes in factors such as the number of entities nearby and max health.
     *
     * @param entity
     * @return
     */
    private static double getDamageNeeded(LivingEntity entity) {
        int entityCount = getEntityCount(entity.getLocation()) - 2; // Don't include the combatants.
        double percentNeeded = Math.max(1, entityCount - 3) / 10D; //10% needed for each mob nearby, if > 3 mobs are nearby.
        percentNeeded = Math.max(Math.min(percentNeeded, .75D), .25D); // Restrict the damage amounts between 25% and 75%.
        return percentNeeded * entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
    }

    /**
     * Get the number of entities within the constant radius of a given location.
     * @param loc
     * @return
     */
    private static int getEntityCount(Location loc) {
        return getEntityCount(loc, null);
    }

    /**
     * Get the number of entities of a certain type within the constant radius of a given location.
     * @param loc
     * @param check - Null = Any Type.
     * @return
     */
    private static int getEntityCount(Location loc, EntityType check) {
        return (int) loc.getWorld().getNearbyEntities(loc, RADIUS, RADIUS, RADIUS).stream()
                .filter(entity ->  (check == null || check == entity.getType())).count();
    }
}
