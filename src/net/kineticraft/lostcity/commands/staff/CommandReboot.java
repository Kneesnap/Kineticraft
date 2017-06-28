package net.kineticraft.lostcity.commands.staff;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.utils.ServerUtils;
import net.kineticraft.lostcity.utils.Utils;
import net.kineticraft.lostcity.commands.StaffCommand;
import net.kineticraft.lostcity.mechanics.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Reboots the server.
 * Created by Kneesnap on 6/1/2017.
 */
public class CommandReboot extends StaffCommand {

    public CommandReboot() {
        super(EnumRank.ADMIN, "[delay]", "Reboot the server", "reboot", "shutdown");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        ServerUtils.reboot(args.length > 0 ? Integer.parseInt(args[0]) : 60);
    }
}
