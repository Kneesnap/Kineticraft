package net.kineticraft.lostcity.crake;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.crake.detectors.Detector;
import net.kineticraft.lostcity.crake.detectors.misc.Xray;
import net.kineticraft.lostcity.crake.detectors.movement.Flight;
import net.kineticraft.lostcity.mechanics.system.Mechanic;
import org.bukkit.Bukkit;

/**
 * Crake - Custom cheat detection system.
 * Created by Kneesnap on 6/17/2017.
 */
public class Crake extends Mechanic {

    @Override
    public void onEnable() {
        addDetector(new Flight());
        addDetector(new Xray());
    }

    public static void addDetector(Detector d) {
        if (Core.isApplicableBuild(d))
            Bukkit.getPluginManager().registerEvents(d, Core.getInstance());
    }
}
