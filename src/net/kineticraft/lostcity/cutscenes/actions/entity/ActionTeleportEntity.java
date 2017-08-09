package net.kineticraft.lostcity.cutscenes.actions.entity;

import lombok.AllArgsConstructor;
import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * Teleport an entity.
 * Created by Kneesnap on 7/22/2017.
 */
@AllArgsConstructor
@ActionData(Material.ENDER_PEARL)
public class ActionTeleportEntity extends ActionEntity {
    private Location location = null;

    public ActionTeleportEntity() {

    }

    @Override
    public void execute() {
        Location l = fixLocation(location);
        getEntity().teleport(l);
        if (getEntity().equals(getCamera()))
            getPlayers().forEach(p -> p.teleport(l));
    }

    @Override
    public String toString() {
        return Utils.toCleanString(location) + super.toString();
    }
}