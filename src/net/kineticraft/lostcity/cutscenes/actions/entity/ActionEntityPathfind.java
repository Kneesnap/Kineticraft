package net.kineticraft.lostcity.cutscenes.actions.entity;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

/**
 * Make an entity walk from point A to point B.
 * Created by Kneesnap on 7/27/2017.
 */
@ActionData(Material.MAP)
public class ActionEntityPathfind extends ActionEntity {
    private Location target = null;

    @Override
    public void execute() {
        if (this.target == null)
            return;

        if (getLivingEntity() instanceof Monster) {
            getLivingEntity().getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(50);

            // Create the goal this entity should walk to.
            Location target = fixLocation(this.target);
            ArmorStand goal = (ArmorStand) target.getWorld().spawnEntity(target, EntityType.ARMOR_STAND);
            goal.setInvulnerable(true);
            goal.setVisible(false);

            // Navigate to the goal.
            toggleAI(e -> {
                ((Creature) e).setTarget(goal);
                boolean done = e.getLocation().distance(target) < .25D || !e.isValid();
                if (done) {
                    e.teleport(goal);
                    goal.remove();
                }
                return done;
            });
        } else {
            Entity e = getEntity();
            Location goal = this.target.clone();
            goal.setWorld(e.getWorld());
            BukkitTask[] task = new BukkitTask[1];
            final double walkSpeed = e.getLocation().distance(goal) / (double) getEvent().getTickDelay();

            task[0] = Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> stepLocation(e, goal, walkSpeed), 0L, 1L); // Move the entity closer every tick.
            Bukkit.getScheduler().runTaskLater(Core.getInstance(), task[0]::cancel, getEvent().getTickDelay() - 1);
        }
    }

    //TODO: If Armorstand, pan view.
    private void stepLocation(Entity ent, Location to, double blocksPerTick) {
        if (!ent.isValid())
            return;

        Location from = ent.getLocation();
        World world = from.getWorld();
        Vector fv = new Vector(from.getX(), from.getY(), from.getZ());
        Vector tv = new Vector(to.getX(), to.getY(), to.getZ());

        double bpt = blocksPerTick;
        Vector dv = new Vector(0d, 0d, 0d);
        dv.add(tv);
        dv.subtract(fv);
        Vector nv = dv.clone();
        nv.multiply(1 / dv.length() * bpt);

        if (nv.length() > dv.length())
            return;

        nv.add(fv);
        Location nl = new Location(world, nv.getX(), nv.getY(), nv.getZ());
        nl.setDirection(dv);
        ent.teleport(nl);
    }

    @Override
    public String toString() {
        return Utils.toCleanString(target) + super.toString();
    }
}
