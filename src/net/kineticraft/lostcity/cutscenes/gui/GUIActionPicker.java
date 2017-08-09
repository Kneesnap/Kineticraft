package net.kineticraft.lostcity.cutscenes.gui;

import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import net.kineticraft.lostcity.cutscenes.CutsceneAction;
import net.kineticraft.lostcity.cutscenes.Cutscenes;
import net.kineticraft.lostcity.guis.GUI;
import net.kineticraft.lostcity.utils.ReflectionUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * Pick a cutscene action.
 * Created by Kneesnap on 7/28/2017.
 */
public class GUIActionPicker extends GUI {

    private Consumer<CutsceneAction> onPick;

    public GUIActionPicker(Player player, Consumer<CutsceneAction> onPick) {
        super(player, "Action Picker");
        this.onPick = onPick;
    }

    @Override
    public void addItems() {
        for (Class<? extends CutsceneAction> action : Cutscenes.getActions()) {
            CutsceneAction a = ReflectionUtil.construct(action);
            ActionData data = a.getData();
            addItem(data.value(), (byte) data.meta(), ChatColor.YELLOW + a.getClass().getSimpleName(), "Click here to select this action.")
                    .leftClick(ce -> {
                        onPick.accept(a);
                        openPrevious();
                    });
        }

        addBackButton();
    }
}
