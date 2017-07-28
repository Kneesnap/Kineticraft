package net.kineticraft.lostcity.cutscenes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kineticraft.lostcity.cutscenes.gui.CutsceneActionEditor;
import net.kineticraft.lostcity.data.Jsonable;
import org.bukkit.Material;

/**
 * Represents an action taken during a cutscene stage.
 * Created by Kneesnap on 7/22/2017.
 */
@AllArgsConstructor @Getter
public abstract class CutsceneAction implements Jsonable {

    private final Material icon;

    public CutsceneAction() {
        this(Material.DIRT);
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
