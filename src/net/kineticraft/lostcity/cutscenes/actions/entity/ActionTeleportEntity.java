package net.kineticraft.lostcity.cutscenes.actions.entity;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.cutscenes.CutsceneEvent;
import org.bukkit.Location;

/**
 * Teleport an entity.
 * Created by Kneesnap on 7/22/2017.
 */
@Getter @Setter
public class ActionTeleportEntity extends ActionEntity {

    private Location location;

    @Override
    public void execute(CutsceneEvent event) {
        getEntity(event).teleport(getLocation());
    }
}
