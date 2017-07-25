package net.kineticraft.lostcity.cutscenes.actions.entity;

import lombok.Getter;
import lombok.Setter;
import net.kineticraft.lostcity.cutscenes.CutsceneEvent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;

/**
 * Make an entity talk.
 * TODO: Maybe the talking entity could glow.
 * Created by Kneesnap on 7/22/2017.
 */
@Getter @Setter
public class ActionEntityTalk extends ActionEntity {

    private String message;
    private Sound sound;
    private float pitch = 1F;

    @Override
    public void execute(CutsceneEvent event) {
        String message = ChatColor.DARK_GREEN + getEntityName() + ": " + ChatColor.GREEN + getMessage();
        event.getStatus().getPlayers().forEach(p -> p.sendMessage(String.format(message, p.getName())));
        Entity e = getEntity(event);
        e.getWorld().playSound(e.getLocation(), getSound() != null ? getSound() : getDisplay(event).getTalkSound(), 1F, getPitch());
    }
}
