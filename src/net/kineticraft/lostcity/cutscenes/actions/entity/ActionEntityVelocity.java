package net.kineticraft.lostcity.cutscenes.actions.entity;

import net.kineticraft.lostcity.cutscenes.CutsceneEvent;
import org.bukkit.util.Vector;

/**
 * Control the velocity of an entity.
 * Created by Kneesnap on 7/22/2017.
 */
public class ActionEntityVelocity extends ActionEntity {
    private double x;
    private double y;
    private double z;

    @Override
    public void execute(CutsceneEvent event) {
        getEntity(event).setVelocity(new Vector(x, y, z));
    }
}
