package net.kineticraft.lostcity.cutscenes.gui;

import net.kineticraft.lostcity.cutscenes.ActionData;
import net.kineticraft.lostcity.cutscenes.CutsceneAction;
import net.kineticraft.lostcity.cutscenes.CutsceneStage;
import net.kineticraft.lostcity.guis.GUI;
import net.kineticraft.lostcity.mechanics.Callbacks;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Stage editor.
 * Created by Kneesnap on 7/28/2017.
 */
public class GUIStageEditor extends GUI {
    private CutsceneStage stage;

    public GUIStageEditor(Player player, CutsceneStage stage) {
        super(player, "Stage Editor", fitSize(stage.getActions(), 2));
        this.stage = stage;
    }

    @Override
    public void addItems() {
        for (CutsceneAction action : stage.getActions()) {
            ActionData data = action.getData();
            addItem(data.value(), (byte) data.meta(), ChatColor.YELLOW + action.getClass().getSimpleName(), "")
                    .leftClick(ce -> new CutsceneActionEditor(ce.getPlayer(), action))
                    .rightClick(ce -> {
                        stage.getActions().remove(action);
                        reconstruct();
                    }).addLoreAction("Left", "Edit Action").addLoreAction("Right", "Remove Action");
        }

        toRight(3);
        addItem(Material.SPONGE, ChatColor.YELLOW + "Ticks", "Ticks: " + ChatColor.YELLOW + stage.getTicks(),
                "Click here to set the tick duration of this stage").anyClick(ce -> {
                    ce.getPlayer().sendMessage(ChatColor.YELLOW + "Please enter the tick duration for this stage.");
                    Callbacks.listenForNumber(ce.getPlayer(), 0, 100, stage::setTicks);
                });
        addItem(Material.DIAMOND_BLOCK, ChatColor.YELLOW + "Add Action", "Click here to add an action.")
            .leftClick(ce -> new GUIActionPicker(ce.getPlayer(), stage.getActions()::add));
        addBackButton();
    }
}
