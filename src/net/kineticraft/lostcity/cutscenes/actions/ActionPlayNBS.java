package net.kineticraft.lostcity.cutscenes.actions;

import lombok.AllArgsConstructor;
import net.kineticraft.lostcity.cutscenes.CutsceneAction;
import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Material;

/**
 * Play noteblock sound files.
 * TODO: Allow repeat
 * Created by Kneesnap on 8/1/2017.
 */
@ActionData(Material.JUKEBOX) @AllArgsConstructor
public class ActionPlayNBS extends CutsceneAction {
    private String soundFile;

    public ActionPlayNBS() {

    }

    @Override
    public void execute() {
        Utils.playSound(getEvent().getStatus().getPlayers(), soundFile);
    }

    @Override
    public String toString() {
        return soundFile + ".nbs";
    }
}
