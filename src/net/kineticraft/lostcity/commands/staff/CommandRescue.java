package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.mechanics.Callbacks;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;

/**
 * Remove the owner of an animal.
 * Created by Kneesnap on 6/2/2017.
 */
public class CommandRescue extends StaffCommand {

    public CommandRescue() {
        super("", "Rescue a tamed animal.", "rescue", "disown");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.GREEN + "Please click on the animal you'd like to rescue.");

        Callbacks.selectEntity((Player) sender, ent -> {
                if (!(ent instanceof Tameable)) {
                    sender.sendMessage(ChatColor.RED + "This entity is not tameable.");
                    return;
                }

                ((Tameable) ent).setOwner(null);
                sender.sendMessage(ChatColor.GREEN + "Entity has been disowned.");
        });
    }
}
