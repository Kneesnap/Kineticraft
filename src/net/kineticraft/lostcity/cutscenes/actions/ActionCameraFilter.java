package net.kineticraft.lostcity.cutscenes.actions;

import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import net.kineticraft.lostcity.cutscenes.CutsceneAction;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

/**
 * Set the camera filter.
 * Created by Kneesnap on 7/22/2017.
 */
@ActionData(value = Material.STAINED_GLASS_PANE, meta = 5)
public class ActionCameraFilter extends CutsceneAction {
    private EntityType filterType = EntityType.CREEPER; // Select from Armor_Stand, Creeper, etc.

    @Override
    public void execute() {
        getEvent().getStatus().makeCamera(filterType);
    }

    @Override
    public String toString() {
        return filterType.name();
    }
}
