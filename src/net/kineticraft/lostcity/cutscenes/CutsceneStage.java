package net.kineticraft.lostcity.cutscenes;

import lombok.Getter;
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
    private int ticks = 20; // The amount of ticks until the next stage.

    /**
     * Perform all actions in this stage.
     * @param status
     */
    public void action(CutsceneStatus status) {
        CutsceneEvent event = new CutsceneEvent(status, getTicks());
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), status::nextStage, getTicks());
        getActions().forEach(a -> a.execute(event));
    }
}
