package net.kineticraft.lostcity.cutscenes.actions.entity;

import net.kineticraft.lostcity.cutscenes.annotations.ActionData;
import net.kineticraft.lostcity.cutscenes.annotations.AllowNull;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;

/**
 * Make an entity talk.
 * TODO: Maybe the talking entity could glow.
 * Created by Kneesnap on 7/22/2017.
 */
@ActionData(Material.PUMPKIN)
public class ActionEntityTalk extends ActionEntity {
    private String message = "";
    @AllowNull private Sound sound = null;
    private float pitch = 1F;

    @Override
    public void execute() {
        String message = ChatColor.DARK_GREEN + getEntityName() + ": " + ChatColor.GREEN + this.message;
        getPlayers().forEach(p -> p.sendMessage(formatString(message, p)));
        getWorld().playSound(getCamera().getLocation(), sound != null ? sound : getDisplay().getTalkSound(), 1F, pitch);
    }

    @Override
    public String toString() {
        return message + super.toString();
    }
}
