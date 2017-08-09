package net.kineticraft.lostcity.utils;

import org.bukkit.Location;
import org.bukkit.Particle;

/**
 * Utilities for making particles do things.
 * Created by Kneesnap on 8/6/2017.
 */
public class ParticleUtils {

    /**
     * Create a horizonal circle at the given location.
     * @param particle
     * @param center
     * @param radius
     */
    public static void makeCircle(Particle particle, Location center, double radius) {
        int amount = 100;
        for(int i = 0; i < amount; i++) {
            double angle = i * ((2 * Math.PI) / amount);
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            center.getWorld().spawnParticle(particle, new Location(center.getWorld(), x, center.getY(), z), 1);
        }
    }
}
