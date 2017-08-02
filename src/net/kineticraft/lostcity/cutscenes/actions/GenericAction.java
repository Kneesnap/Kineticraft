package net.kineticraft.lostcity.cutscenes.actions;

import lombok.AllArgsConstructor;
import net.kineticraft.lostcity.cutscenes.CutsceneAction;
import net.kineticraft.lostcity.cutscenes.CutsceneEvent;

import java.util.function.Consumer;

/**
 * Allows creating custom actions, in code only.
 * Created by Kneesnap on 8/1/2017.
 */
@AllArgsConstructor
public class GenericAction extends CutsceneAction {
    private transient Consumer<CutsceneEvent> onExecute;

    @Override
    public void execute() {
        this.onExecute.accept(getEvent());
    }
}
