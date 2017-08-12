package net.kineticraft.lostcity.cutscenes;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.data.Jsonable;
import net.kineticraft.lostcity.data.lists.JsonList;
import org.bukkit.Bukkit;

/**
 * A bundle of cutscene actions to all be performed at once.
 * Created by Kneesnap on 7/22/2017.
 */
@Getter
public class CutsceneStage implements Jsonable {
    private JsonList<CutsceneAction> actions = new JsonList<>();
    @Setter private int ticks = 20; // The amount of ticks until the next stage.

    /**
     * Perform all actions in this stage.
     * @param status
     */
    public void action(CutsceneStatus status) {
        CutsceneEvent event = new CutsceneEvent(status, getTicks());
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), status::nextStage, getTicks());
        getActions().stream().filter(CutsceneAction::isValid).forEach(a -> {
            a.setEvent(event);
            status.bindCamera(); // Teleport each player to the camera. Fixes weird audio-loss bug.
            try {
                a.execute();
            } catch (Exception e) {
                e.printStackTrace();
                Core.warn("Failed to run action '" + a.getClass().getSimpleName() + "' on " + status);
            }
            a.setEvent(null);
        });
    }

    /**
     * Add a cutscene action.
     * @param a
     */
    public void addAction(CutsceneAction a) {
        getActions().add(a);
    }
}
