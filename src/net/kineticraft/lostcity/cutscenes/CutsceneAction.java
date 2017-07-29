package net.kineticraft.lostcity.cutscenes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.cutscenes.gui.CutsceneActionEditor;
import net.kineticraft.lostcity.data.Jsonable;

/**
 * Represents an action taken during a cutscene stage.
 * Created by Kneesnap on 7/22/2017.
 */
@AllArgsConstructor @Getter
public abstract class CutsceneAction implements Jsonable {

    /**
     * Get annotated action data of this action.
     * @return data
     */
    public ActionData getData() {
        return getClass().getAnnotation(ActionData.class);
    }

    /**
     * Execute this action.
     * @param event
     */
    public abstract void execute(CutsceneEvent event);

    /**
     * Add any special buttons to the editor gui.
     * @param gui
     */
    public void setupGUI(CutsceneActionEditor gui) {

    }
}
