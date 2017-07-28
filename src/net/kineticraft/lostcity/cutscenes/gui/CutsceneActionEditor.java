package net.kineticraft.lostcity.cutscenes.gui;

import net.kineticraft.lostcity.cutscenes.CutsceneAction;
import net.kineticraft.lostcity.guis.data.GUIJsonEditor;
import org.bukkit.entity.Player;

/**
 * Edit a cutscene action.
 * Created by Kneesnap on 7/27/2017.
 */
public class CutsceneActionEditor extends GUIJsonEditor {

    private CutsceneAction action;

    public CutsceneActionEditor(Player player, CutsceneAction action) {
        super(player, action);
        this.action = action;
    }

    @Override
    protected void addElements() {
        super.addElements();
        this.action.setupGUI(this);
    }
}
