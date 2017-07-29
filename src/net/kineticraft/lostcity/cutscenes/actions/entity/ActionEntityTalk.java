package net.kineticraft.lostcity.cutscenes.actions.entity;

import net.kineticraft.lostcity.cutscenes.ActionData;
import net.kineticraft.lostcity.cutscenes.CutsceneEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;

/**
 * Make an entity talk.
 * TODO: Maybe the talking entity could glow.
 * Created by Kneesnap on 7/22/2017.
 */
@ActionData(Material.PUMPKIN)
public class ActionEntityTalk extends ActionEntity {
    private String message;
    private Sound sound;
    private float pitch = 1F;

    @Override
    public void execute(CutsceneEvent event) {
        String message = ChatColor.DARK_GREEN + getEntityName() + ": " + ChatColor.GREEN + this.message;
        event.getStatus().getPlayers().forEach(p -> p.sendMessage(String.format(message, p.getName())));
        Entity e = getEntity(event);
        e.getWorld().playSound(e.getLocation(), sound != null ? sound : getDisplay(event).getTalkSound(), 1F, pitch);
    }
}
