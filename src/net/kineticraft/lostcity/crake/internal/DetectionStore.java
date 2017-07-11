package net.kineticraft.lostcity.crake.internal;

import lombok.Getter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.utils.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A container for detections.
 *
 * Created by Kneesnap on 7/10/2017.
 */
@Getter
public class DetectionStore<T extends Detection> {

    private int clearTicks;
    private int showMin;
    private String message;
    private List<T> detections = new ArrayList<>();

    public DetectionStore(String message) {
        this(20, message);
    }

    public DetectionStore(int clearTicks, String message) {
        this(clearTicks, 2, message);
    }

    /**
     * @param clearTicks - The amount of ticks until a detection is forgotten.
     * @param showMin - Minimum detections before an alert is fired.
     * @param message - The message to show on detection. Formatted with {Detection Count}
     */
    public DetectionStore(int clearTicks, int showMin, String message) {
        this.clearTicks = clearTicks;
        this.showMin = showMin;
        this.message = message;

        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), this::clearTrash, 0L, 60L * 20L);
    }

    /**
     * Clear out expired detections.
     */
    protected void clearTrash() {
        getDetections().removeAll(getDetections().stream()
                .filter(d -> ServerUtils.getCurrentTick() > d.getTick() + getClearTicks() || d.hasExpired())
                .collect(Collectors.toList()));
    }

    /**
     * Add detections based on a boolean list.
     * Makes for better code formatting of detectors.
     *
     * @param detection
     * @param res
     */
    public void detect(T detection, boolean... res) {
        for (boolean b : res)
            if (b)
                detect(detection);
    }

    /**
     * Calls when a detection is thrown.
     * @param detection
     */
    public void detect(T detection) {
        getDetections().add(detection);
        clearTrash();

        if (getDetections(detection.getPlayer()).size() < getShowMin())
            return;

        Core.alertStaff("Crake: " + ChatColor.GRAY + detection.getPlayer().getName()
                + " " + getMessage(detection.getPlayer()) + ".");
        getDetections().removeAll(getDetections(detection.getPlayer())); // Remove all detections for this player.
    }

    /**
     * Get all detections for a player.
     * @param player
     * @return dettections
     */
    public List<T> getDetections(Player player) {
        return getDetections().stream().filter(d -> d.getPlayer().equals(player)).collect(Collectors.toList());
    }

    /**
     * Get the display message for a detection.
     * @param player
     * @return message
     */
    protected String getMessage(Player player) {
        return String.format(getMessage(), getDetections(player).size());
    }
}
