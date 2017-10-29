package net.kineticraft.lostcity.cutscenes.actions.entity;

import lombok.Getter;
import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;

/**
 * Create an entity cutscene prop.
 * Created by Kneesnap on 7/22/2017.
 */
@ActionData(Material.EGG)
public class ActionCreateEntity extends ActionEntity {
    private Location location = null;
    @Getter private EntityType entityType = EntityType.ZOMBIE;

    @Override
    public void execute() {
        assert getEntity() == null;
        Entity e = getWorld().spawnEntity(fixLocation(location), getEntityType());
        e.setCustomName(ChatColor.GREEN + getEntityName());
        e.setSilent(true);

        if (e instanceof Monster)
            ((Monster) e).setAI(false);

        if (e instanceof Ageable)
            ((Ageable) e).setAdult();
        getEvent().getStatus().setEntity(getEntityName(), e);
    }

    @Override
    public String toString() {
        return getEntityType() + super.toString() + " @ " + Utils.toCleanString(location);
    }
}
