package net.kineticraft.lostcity.cutscenes.actions;

import net.kineticraft.lostcity.cutscenes.CutsceneAction;
import net.kineticraft.lostcity.cutscenes.CutsceneEvent;
import org.bukkit.entity.EntityType;

/**
 * Set the camera filter.
 * Created by Kneesnap on 7/22/2017.
 */
public class ActionCameraFilter extends CutsceneAction {

    private EntityType filterType; // Select from Armor_Stand, Creeper, etc.

    @Override
    public void execute(CutsceneEvent event) {
        event.getStatus().makeCamera(filterType);
    }
}
