package net.kineticraft.lostcity.cutscenes.actions.entity;


import net.kineticraft.lostcity.cutscenes.CutsceneEvent;

/**
 * Remove an entity from the world.
 * Created by Kneesnap on 7/22/2017.
 */
public class ActionRemoveEntity extends ActionEntity {
    @Override
    public void execute(CutsceneEvent event) {
        getEntity(event).remove();
    }
}
