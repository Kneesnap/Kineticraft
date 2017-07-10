package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Allow Helper+ to teleport to players.
 * Created by Kneesnap on 7/9/2017.
 */
public class CommandTeleport extends StaffCommand {

    public CommandTeleport() {
        super("<player>", "Teleport to another player.", "to");
        autocompleteOnline();
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (Utils.isVisible(sender, args[0]))
            ((Player) sender).teleport(Bukkit.getPlayer(args[0]));
    }
}
