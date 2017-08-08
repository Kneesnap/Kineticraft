package net.kineticraft.lostcity.cutscenes.actions.entity;

import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import net.kineticraft.lostcity.mechanics.ArmorStands;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;

/**
 * Force an ArmorStand to assume a pose.
 * Created by Kneesnap on 8/1/2017.
 */
@ActionData(Material.ARMOR_STAND)
public class ActionArmorStandPose extends ActionEntity {
    private ArmorStands.ArmorPose pose;

    @Override
    public void execute() {
        ArmorStands.assumePose((ArmorStand) getEntity(), pose);
    }
}
