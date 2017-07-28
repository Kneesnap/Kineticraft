package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.utils.ServerUtils;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Reboots the server, or show how much time there is until a reboot.
 * Created by Kneesnap on 6/1/2017.
 */
public class CommandReboot extends PlayerCommand {

    public CommandReboot() {
        super("[delay]", "See how much time is left until the next reboot.", "reboot", "shutdown");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (Utils.getRank(sender).isAtLeast(EnumRank.MOD) && args.length > 0) {
            ServerUtils.reboot(Integer.parseInt(args[0]));
            return;
        }
        sender.sendMessage(ChatColor.GRAY + "Next Reboot: " + ChatColor.GREEN + Utils.formatTimeFull(ServerUtils.getTicksToReboot() * 50));
    }
}
