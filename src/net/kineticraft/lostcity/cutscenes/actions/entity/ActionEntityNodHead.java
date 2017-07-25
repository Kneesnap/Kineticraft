package net.kineticraft.lostcity.cutscenes.actions.entity;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.cutscenes.CutsceneEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 * Have an entity nod their head yes.
 * Created by Kneesnap on 7/22/2017.
 */
@Getter @Setter
public class ActionEntityNodHead extends ActionEntity {
    private static final float DEGREE = 60F;
    private int nods = 3;

    @Override
    public void execute(CutsceneEvent event) {
        for (int i = 0; i <= getNods() * 2; i++)
            turn(getEntity(event), i);
    }

    private void turn(Entity e, int tick) {
        float add = (tick > 0 && tick < getNods() * 2 ? 2 : 1) * DEGREE * (tick % 2 == 0 ? 1 : -1);
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            Location loc = e.getLocation();
            loc.setPitch(loc.getPitch() + add);
            e.teleport(loc);
        }, tick);
    }
}
