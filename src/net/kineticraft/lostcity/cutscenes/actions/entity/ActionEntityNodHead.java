package net.kineticraft.lostcity.cutscenes.actions.entity;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

/**
 * Have an entity nod their head yes.
 * Created by Kneesnap on 7/22/2017.
 */
@ActionData(Material.ARMOR_STAND)
public class ActionEntityNodHead extends ActionEntity {
    private static final float DEGREE = 60F;
    private int nods = 3;

    @Override
    public void execute() {
        for (int i = 0; i <= nods * 2; i++)
            turn(getEntity(), i);
    }

    private void turn(Entity e, int tick) {
        float add = (tick > 0 && tick < nods * 2 ? 2 : 1) * DEGREE * (tick % 2 == 0 ? 1 : -1);
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            Location loc = e.getLocation();
            loc.setPitch(loc.getPitch() + add);
            e.teleport(loc);
        }, tick);
    }

    @Override
    public String toString() {
        return getEntityName();
    }
}
