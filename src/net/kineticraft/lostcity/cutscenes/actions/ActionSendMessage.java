package net.kineticraft.lostcity.cutscenes.actions;

import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import net.kineticraft.lostcity.cutscenes.CutsceneAction;
import org.bukkit.Material;

/**
 * Send a message to the players in the cutscene.
 * Created by Kneesnap on 7/22/2017.
 */
@ActionData(Material.SIGN)
public class ActionSendMessage extends CutsceneAction {
    private String message = "";

    @Override
    public void execute() {
        getPlayers().forEach(p -> p.sendMessage(formatString(message, p)));
    }

    @Override
    public String toString() {
        return message;
    }
}
