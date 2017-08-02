package net.kineticraft.lostcity.cutscenes.actions;

import lombok.AllArgsConstructor;
import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import net.kineticraft.lostcity.cutscenes.CutsceneAction;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

/**
 * Play a sound at a location.
 * Created by Kneesnap on 7/22/2017.
 */
@ActionData(Material.NOTE_BLOCK) @AllArgsConstructor
public class ActionPlaySound extends CutsceneAction {
    private Location location = null;
    private Sound sound = Sound.ENTITY_PLAYER_LEVELUP;
    private float pitch = 1F;
    private int times = 1;

    public ActionPlaySound() {

    }

    @Override
    public void execute() {
        for (int times = 0; times < this.times; times++)
            getWorld().playSound(fixLocation(location), sound, 1, pitch);
    }

    @Override
    public String toString() {
        return sound + " @ " + Utils.toCleanString(location);
    }
}
