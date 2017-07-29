package net.kineticraft.lostcity.cutscenes.actions.entity;

import net.kineticraft.lostcity.cutscenes.ActionData;
import net.kineticraft.lostcity.cutscenes.CutsceneEvent;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * Teleport an entity.
 * Created by Kneesnap on 7/22/2017.
 */
@ActionData(Material.ENDER_PEARL)
public class ActionTeleportEntity extends ActionEntity {
    private Location location;

    @Override
    public void execute(CutsceneEvent event) {
        getEntity(event).teleport(location);
    }
}