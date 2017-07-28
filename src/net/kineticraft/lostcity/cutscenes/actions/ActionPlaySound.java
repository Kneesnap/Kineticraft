package net.kineticraft.lostcity.cutscenes.actions;

import net.kineticraft.lostcity.cutscenes.CutsceneAction;
import net.kineticraft.lostcity.cutscenes.CutsceneEvent;
import org.bukkit.Location;
import org.bukkit.Sound;

/**
 * Play a sound at a location.
 * Created by Kneesnap on 7/22/2017.
 */
public class ActionPlaySound extends CutsceneAction {

    private Location location;
    private Sound sound = Sound.ENTITY_PLAYER_LEVELUP;
    private float pitch = 1F;
    private int times = 1;

    @Override
    public void execute(CutsceneEvent event) {
        for (int times = 0; times < this.times; times++)
            location.getWorld().playSound(location, sound, 1, pitch);
    }
}
