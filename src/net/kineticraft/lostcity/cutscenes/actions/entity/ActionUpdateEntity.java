package net.kineticraft.lostcity.cutscenes.actions.entity;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.cutscenes.CutsceneEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;

/**
 * Update an entity.
 * Created by Kneesnap on 7/22/2017.
 */
@Getter @Setter
public class ActionUpdateEntity extends ActionEntity {

    private String customName;
    private boolean showName;

    @Override
    public void execute(CutsceneEvent event) {
        Entity e = getEntity(event);
        e.setCustomName(ChatColor.DARK_GREEN + getCustomName());
        e.setCustomNameVisible(isShowName());
    }
}
