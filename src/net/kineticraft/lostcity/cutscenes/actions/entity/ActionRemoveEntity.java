package net.kineticraft.lostcity.cutscenes.actions.entity;

import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import org.bukkit.Material;

/**
 * Remove an entity from the world.
 * Created by Kneesnap on 7/22/2017.
 */
@ActionData(Material.LAVA_BUCKET)
public class ActionRemoveEntity extends ActionEntity {
    @Override
    public void execute() {
        getEvent().getStatus().removeEntity(getEntityName());
    }

    @Override
    public String toString() {
        return getEntityName();
    }
}
