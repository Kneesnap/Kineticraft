package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Tell players how to verify.
 * Created by Kneesnap on 6/29/2017.
 */
public class CommandVerify extends PlayerCommand {

    public CommandVerify() {
        super("", "Link your discord and in-game accounts.", "verify");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.BLUE + "Type " + ChatColor.RED + "/verify"
                + ChatColor.BLUE + " in discord to link your accounts for extra perks.");
    }
}
