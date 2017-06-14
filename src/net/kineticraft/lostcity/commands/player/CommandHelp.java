package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.Command;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import net.kineticraft.lostcity.mechanics.Commands;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * CommandHelp - List help for all commands
 * TODO: Seperate pages.
 *
 * Created by Kneesnap on 5/30/2017.
 */
public class CommandHelp extends PlayerCommand {

    public CommandHelp() {
        super(EnumRank.MU, false, "[command]", "Display command usage.", "help", "?");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        String bar = ChatColor.DARK_GREEN.toString() + ChatColor.STRIKETHROUGH + "-----";

        if (args.length == 0) {
            sender.sendMessage(bar + ChatColor.GRAY + " Command Help " + bar);
            Commands.getCommands().stream().filter(c -> c.canUse(sender, false) && c.getHelp() != null)
                    .forEach(cmd ->  sender.sendMessage(ChatColor.GRAY + cmd.getCommandPrefix()
                            + cmd.getName() + ": " + ChatColor.WHITE + cmd.getHelp()));
        } else {

            Command cmd = Commands.getCommand(null, args[0]);

            // If the command isn't found or it isn't a command a player needs to see.
            if (cmd == null || !cmd.canUse(sender, false)) {
                sender.sendMessage(ChatColor.RED + "Cannot find help for '" + args[0] + "'.");
                return;
            }

            sender.sendMessage(bar + ChatColor.GRAY + " Information for " + cmd.getName() + " " + bar);
            sender.sendMessage("Description: " + cmd.getHelp());
            sender.sendMessage(cmd.getUsage());
            sender.sendMessage("Alias: " + String.join(" ", cmd.getAlias()));
            if (cmd instanceof PlayerCommand)
                sender.sendMessage("Minimum Rank: " + ((PlayerCommand) cmd).getMinRank().getName());
        }
    }
}
