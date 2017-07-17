package net.kineticraft.lostcity.crake.detectors;

import net.kineticraft.lostcity.crake.internal.Detection;
import net.kineticraft.lostcity.crake.internal.DetectionStore;
import org.bukkit.event.Listener;


/**
 * A template for a detector.
 * Created by Kneesnap on 7/10/2017.
 */
public class Detector implements Listener {


    /**
     * Throw a detection that will not persist / be stored.
     * @param detection
     * @param message
     * @param <T>
     */
    protected <T extends Detection> void detect(T detection, String message) {
        new DetectionStore<T>(0, message).detect(detection);
    }
}
