package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.Command;
import net.kineticraft.lostcity.commands.CommandType;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.mechanics.Commands;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * CommandHelp -
 * Created by Kneesnap on 5/30/2017.
 */
public class CommandHelp extends PlayerCommand {

    public CommandHelp() {
        super("", "Display command usage.", "help", "?");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        KCPlayer player = KCPlayer.getWrapper((Player) sender);
        sender.sendMessage(ChatColor.DARK_GREEN + "------ " + ChatColor.GRAY + "Command Help" + ChatColor.DARK_GREEN + " ------");
        for (Command command : Commands.getCommands(CommandType.PLAYER)) {
            PlayerCommand playerCommand = (PlayerCommand) command;
            if (player.getRank().isAtLeast(playerCommand.getMinRank()))
                sender.sendMessage(ChatColor.GRAY + playerCommand.getCommandPrefix() + playerCommand.getName()
                        + ": " + ChatColor.WHITE + playerCommand.getHelp());
        }
    }
}
