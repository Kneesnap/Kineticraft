package net.kineticraft.lostcity.cutscenes.actions;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.cutscenes.CutsceneAction;
import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import net.kineticraft.lostcity.dungeons.Dungeons;
import org.bukkit.Bukkit;
import org.bukkit.Material;

/**
 * Complete the active dungeon, if any.
 * Created by Kneesnap on 8/8/2017.
 */
@ActionData(Material.EYE_OF_ENDER)
public class ActionCompleteDungeon extends CutsceneAction {
    @Override
    public void execute() {
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), Dungeons.getDungeon(getWorld())::complete, getEvent().getTickDelay() + 1);
    }
}
