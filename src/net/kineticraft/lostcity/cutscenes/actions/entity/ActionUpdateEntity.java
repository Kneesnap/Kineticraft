package net.kineticraft.lostcity.cutscenes.actions.entity;

import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import net.kineticraft.lostcity.cutscenes.annotations.AllowNull;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

/**
 * Update an entity.
 * Created by Kneesnap on 7/22/2017.
 */
@ActionData(Material.GOLDEN_APPLE)
public class ActionUpdateEntity extends ActionEntity {
    @AllowNull private String customName;
    private boolean showName;

    @Override
    public void execute() {
        Entity e = getEntity();
        if (customName != null)
            e.setCustomName(ChatColor.DARK_GREEN + customName);
        e.setCustomNameVisible(showName);
    }

    @Override
    public String toString() {
        return toJsonString();
    }
}
