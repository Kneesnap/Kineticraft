package net.kineticraft.lostcity.cutscenes.actions;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.cutscenes.CutsceneAction;
import net.kineticraft.lostcity.cutscenes.CutsceneEvent;
import org.bukkit.Location;
import org.bukkit.Sound;

/**
 * Play a sound at a location.
 * Created by Kneesnap on 7/22/2017.
 */
@Getter @Setter
public class ActionPlaySound extends CutsceneAction {

    private Location location;
    private Sound sound = Sound.ENTITY_PLAYER_LEVELUP;
    private float pitch = 1F;
    private int times;

    @Override
    public void execute(CutsceneEvent event) {
        for (int times = 0; times < getTimes(); times++)
            getLocation().getWorld().playSound(getLocation(), getSound(), 1, getPitch());
    }
}
