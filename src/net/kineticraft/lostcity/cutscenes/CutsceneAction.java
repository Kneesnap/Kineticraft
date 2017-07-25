package net.kineticraft.lostcity.cutscenes;

import net.kineticraft.lostcity.data.Jsonable;

/**
 * Represents an action taken during a cutscene stage.
 * Created by Kneesnap on 7/22/2017.
 */
public abstract class CutsceneAction implements Jsonable {
    /**
     * Execute this action.
     * @param event
     */
    public abstract void execute(CutsceneEvent event);
}
