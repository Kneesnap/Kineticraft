package net.kineticraft.lostcity.cutscenes.actions;

import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import net.kineticraft.lostcity.cutscenes.CutsceneAction;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;

/**
 * Spawn particles in the world.
 * Created by Kneesnap on 7/22/2017.
 */
@ActionData(Material.FIREWORK)
public class ActionSpawnParticles extends CutsceneAction {

    private Particle effect = Particle.HEART;
    private Location location = null;
    private int count = 5;
    private double spreadX = 0;
    private double spreadY = 0;
    private double spreadZ = 0;

    @Override
    public void execute() {
        getWorld().spawnParticle(effect, fixLocation(location), count, spreadX, spreadY, spreadZ);
    }

    public String toString() {
        return effect + " @ " + Utils.toCleanString(location);
    }
}
