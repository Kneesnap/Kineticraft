package net.kineticraft.lostcity.cutscenes.gui;

import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import net.kineticraft.lostcity.cutscenes.CutsceneAction;
import net.kineticraft.lostcity.cutscenes.CutsceneStage;
import net.kineticraft.lostcity.guis.GUI;
import net.kineticraft.lostcity.item.ItemWrapper;
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
        super(player, "Stage Editor");
        this.stage = stage;
    }

    @Override
    public void addItems() {
        for (CutsceneAction action : stage.getActions()) {
            ActionData data = action.getData();
            boolean valid = action.isValid();
            ItemWrapper iw = addItem(valid ? data.value() : Material.BARRIER, (byte) data.meta(), action.getName(),
                    "Value: " + ChatColor.YELLOW + action.toString(), "")
                    .leftClick(ce -> new CutsceneActionEditor(ce.getPlayer(), action))
                    .rightClick(ce -> {
                        stage.getActions().remove(action);
                        reconstruct();
                    });
            if (!valid)
                iw.addLore(ChatColor.RED + "Invalid.");
            iw.addLoreAction("Left", "Edit Action").addLoreAction("Right", "Remove Action");
        }

        nextRow();
        addItem(Material.SPONGE, ChatColor.YELLOW + "Ticks", "Ticks: " + ChatColor.YELLOW + stage.getTicks(),
                "Click here to set the tick duration of this stage").anyClick(ce -> {
                    ce.getPlayer().sendMessage(ChatColor.YELLOW + "Please enter the tick duration for this stage.");
                    Callbacks.listenForNumber(ce.getPlayer(), 0, 100, stage::setTicks);
                });

        center();
        addItem(Material.DIAMOND_BLOCK, ChatColor.YELLOW + "Add Action", "Click here to add an action.")
            .leftClick(ce -> new GUIActionPicker(ce.getPlayer(), a -> {
                stage.getActions().add(a);
                new CutsceneActionEditor(ce.getPlayer(), a);
            }));
        addBackButton();
    }
}
