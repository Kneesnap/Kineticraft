package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.StaffCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Broadcast a message cross-server.
 * Created by Kneesnap on 6/27/2017.
 */
public class CommandBroadcast extends StaffCommand {

    public CommandBroadcast() {
        super(EnumRank.MEDIA, "<message>", "Broadcast a message accross the entire server.", "broadcast");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        Core.announce(ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));
    }
}
