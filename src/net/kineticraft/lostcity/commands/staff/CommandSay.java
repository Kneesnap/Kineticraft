package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Broadcast a message to the entire server.
 * Created by Kneesnap on 7/24/2017.
 */
public class CommandSay extends StaffCommand {
    public CommandSay() {
        super("<message>", "Broadcast a message as yourself.", "say");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Core.broadcast("[" + Utils.getRank(sender).getNameColor() + sender.getName()
                + ChatColor.RESET + "] " + String.join(" ", args));
    }
}
