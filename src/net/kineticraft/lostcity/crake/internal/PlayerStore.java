package net.kineticraft.lostcity.crake.internal;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Store data indexing it by a player.
 * Created by Kneesnap on 7/16/2017.
 */
@Getter
public class PlayerStore<T> extends DetectionStore<GeneralDetection<T>> {

    public PlayerStore(String message) {
        super(message);
    }

    public PlayerStore(int clearSeconds, String message) {
        super(clearSeconds, message);
    }

    /**
     * Add a detection without triggering it.
     * @param detection
     */
    public void add(Player player, T detection) {
        add(new GeneralDetection<>(player, detection));
    }

    /**
     * Get all detections for a player.
     * @param player
     * @return detections
     */
    public List<T> getData(Player player) {
        return getDetections(player).stream().map(GeneralDetection::getObject).collect(Collectors.toList());
    }
}
