package net.kineticraft.lostcity.cutscenes;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Information sent to a cutscene action when its ready to execute.
 * Created by Kneesnap on 7/22/2017.
 */
@AllArgsConstructor @Getter
public class CutsceneEvent {
    private CutsceneStatus status;
    private int tickDelay;
}
