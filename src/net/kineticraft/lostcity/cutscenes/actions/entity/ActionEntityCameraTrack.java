package net.kineticraft.lostcity.cutscenes.actions.entity;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import net.kineticraft.lostcity.data.lists.JsonList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Follow a camera track.
 * Created by Kneesnap on 7/27/2017.
 */
@ActionData(Material.RAILS)
public class ActionEntityCameraTrack extends ActionEntity {
    private JsonList<Location> track = new JsonList<>(Location.class);
    private int perSecond = 5;
    private transient BukkitTask task;
    private transient List<Location> tempTrack;

    @Override
    public void execute() {
        Entity e = getEntity();
        World w = getWorld();
        tempTrack = new ArrayList<>(track.getValues());
        task = Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            if (tempTrack.isEmpty()) {
                task.cancel(); // We've finished all of the positions.
                return;
            }

            e.teleport(fixLocation(tempTrack.remove(0), w));
        }, 0L, 20 / perSecond);
    }

    @Override
    public String toString() {
        return track.size() + " markers" + super.toString();
    }
}