package net.kineticraft.lostcity.cutscenes.actions.entity;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.cutscenes.CutsceneEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitTask;

/**
 * Make an entity walk from point A to point B.
 * Created by Kneesnap on 7/27/2017.
 */
public class ActionEntityPathfind extends ActionEntity {

    private Location target;
    private transient BukkitTask check;

    @Override
    public void execute(CutsceneEvent event) {
        LivingEntity e = getLivingEntity(event);
        e.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(50);

        // Create the goal this entity should walk to.
        ArmorStand goal = (ArmorStand) target.getWorld().spawnEntity(target, EntityType.ARMOR_STAND);
        goal.setInvulnerable(true);
        goal.setVisible(false);

        // Set the target of this entity
        e.setAI(true);
        ((Creature) e).setTarget(goal);

        // Disable AI once we reach our destination.
        check = Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            if (e.getLocation().distance(target) > .25D && e.isValid())
                return;
            e.setAI(false);
            goal.remove();
            check.cancel();
        }, 0L, 20);
    }
}
