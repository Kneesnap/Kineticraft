package net.kineticraft.lostcity.cutscenes.gui;

import net.kineticraft.lostcity.cutscenes.Cutscene;
import net.kineticraft.lostcity.cutscenes.CutsceneStage;
import net.kineticraft.lostcity.guis.GUI;
import net.kineticraft.lostcity.item.ItemWrapper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Edit a cutscene.
 * Created by Kneesnap on 7/27/2017.
 */
public class GUICutsceneEditor extends GUI {

    private Cutscene cutscene;

    public GUICutsceneEditor(Player player, Cutscene c) {
        super(player, "Cutscene Editor");
        this.cutscene = c;
    }

    @Override
    public void addItems() {
        for (int i = 0; i < cutscene.getStages().size(); i++) {
            CutsceneStage stage = cutscene.getStages().get(i);
            ItemWrapper iw = addItem(Material.PAPER, ChatColor.YELLOW + "Stage " + (i + 1),
                    "Actions: " + ChatColor.YELLOW + stage.getActions().size(), "")
                    .leftClick(ce -> new GUIStageEditor(ce.getPlayer(), stage)).rightClick(ce -> {
                        cutscene.getStages().remove(stage);
                        reconstruct();
                    });
            stage.getActions().stream().map(a -> a.getName() + " (" + a + a.getColor() + ")").forEach(iw::addLore);
            iw.addLoreAction("Left", "Edit Stage").addLoreAction("Right", "Remove Stage");
        }

        toRight(1);
        addItem(Material.WOOL, ChatColor.GREEN + "Add Stage", "Click here to add a stage.").leftClick(ce -> {
            cutscene.getStages().add(new CutsceneStage());
            reconstruct();
        });
    }
}
