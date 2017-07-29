package net.kineticraft.lostcity.cutscenes.actions.entity;

import lombok.Getter;
import net.kineticraft.lostcity.cutscenes.CutsceneAction;
import net.kineticraft.lostcity.cutscenes.CutsceneEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

/**
 * Represents a CutsceneAction that performs on an entity.
 * Created by Kneesnap on 7/22/2017.
 */
@Getter
public abstract class ActionEntity extends CutsceneAction {

    private String entityName = "Camera";

    /**
     * Get the entity related to this action.
     * @return entity
     */
    public Entity getEntity(CutsceneEvent evt) {
        return evt.getStatus().getEntityMap().get(getEntityName());
    }

    /**
     * Get the LivingEntity this event controls, if there is one.
     * @param evt
     * @return livingEntity
     */
    public LivingEntity getLivingEntity(CutsceneEvent evt) {
        return (LivingEntity) getEntity(evt);
    }

    /**
     * Get the mob display for the mob from this event.
     * @return
     */
    public EntityDisplay getDisplay(CutsceneEvent evt) {
        return EntityDisplay.getByType(evt.getStatus().getCutscene().getType(getEntityName()));
    }

    /**
     * Does an entity exist with a name that matches the input name?
     * @param entityName
     * @param evt
     * @return doesExist
     */
    public boolean doesEntityExist(String entityName, CutsceneEvent evt) {
        return evt.getStatus().getCutscene().getType(entityName) != null;
    }
}