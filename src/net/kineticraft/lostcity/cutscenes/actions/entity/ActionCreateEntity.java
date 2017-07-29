package net.kineticraft.lostcity.cutscenes.actions.entity;

import lombok.Getter;
import net.kineticraft.lostcity.cutscenes.ActionData;
import net.kineticraft.lostcity.cutscenes.CutsceneEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

/**
 * Create an entity.
 * Created by Kneesnap on 7/22/2017.
 */
@ActionData(Material.EGG)
public class ActionCreateEntity extends ActionEntity {
    private Location location;
    @Getter private EntityType entityType = EntityType.ZOMBIE;

    @Override
    public void execute(CutsceneEvent event) {
        assert getEntity(event) == null;
        Entity e = location.getWorld().spawnEntity(location, getEntityType());
        e.setCustomName(getEntityName());
        e.setSilent(true);

        if (e instanceof LivingEntity) {
            LivingEntity le = (LivingEntity) e;
            le.setAI(false);
            le.setInvulnerable(true);
        }

        event.getStatus().getEntityMap().put(getEntityName(), e);
    }
}
