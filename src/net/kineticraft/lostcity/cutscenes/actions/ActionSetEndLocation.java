package net.kineticraft.lostcity.cutscenes.actions;

import net.kineticraft.lostcity.cutscenes.CutsceneAction;
import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * Set the location players should be teleported to when the dungeon ends.
 * Created by Kneesnap on 8/12/2017.
 */
@ActionData(Material.END_BRICKS)
public class ActionSetEndLocation extends CutsceneAction {
    private Location location;

    @Override
    public void execute() {
        getEvent().getStatus().setStartLocation(fixLocation(location));
    }

    @Override
    public String toString() {
        return Utils.toCleanString(location);
    }
}
