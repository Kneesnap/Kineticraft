package net.kineticraft.lostcity.cutscenes.actions;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.cutscenes.CutsceneAction;
import net.kineticraft.lostcity.cutscenes.CutsceneEvent;
import org.bukkit.Location;
import org.bukkit.Particle;

/**
 * Spawn particles in the world.
 * Created by Kneesnap on 7/22/2017.
 */
@Getter @Setter
public class ActionSpawnParticles extends CutsceneAction {

    private Particle effect;
    private Location location;
    private int count = 5;
    private double spreadX;
    private double spreadY;
    private double spreadZ;

    @Override
    public void execute(CutsceneEvent event) {
        getLocation().getWorld().spawnParticle(getEffect(), getLocation(), getCount(), getSpreadX(), getSpreadY(), getSpreadZ());
    }
}
