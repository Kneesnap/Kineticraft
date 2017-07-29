package net.kineticraft.lostcity.cutscenes.actions;

import net.kineticraft.lostcity.cutscenes.ActionData;
import net.kineticraft.lostcity.cutscenes.CutsceneAction;
import net.kineticraft.lostcity.cutscenes.CutsceneEvent;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * A cutscene action that changes blocks.
 * Created by Kneesnap on 7/22/2017.
 */
@ActionData(Material.GRASS)
public class ActionBlockUpdate extends CutsceneAction {

    private Location location;
    private Material type = Material.AIR;

    @Override
    public void execute(CutsceneEvent event) {
        location.getBlock().setType(type);
    }
}
