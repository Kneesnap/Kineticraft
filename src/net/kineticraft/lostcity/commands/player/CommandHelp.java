package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.Command;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.config.Configs;
import net.kineticraft.lostcity.commands.Commands;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CommandHelp - List help for all commands
 * Created by Kneesnap on 5/30/2017.
 */
public class CommandHelp extends PlayerCommand {

    private static final int PER_PAGE = 10;

    public CommandHelp() {
        super("[command|page]", "Display command usage.", "help", "?");
        autocomplete(() -> Commands.getCommands().stream().map(Command::getName).collect(Collectors.toList()));
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        String bar = ChatColor.DARK_GREEN.toString() + ChatColor.STRIKETHROUGH + "------";

        if (args.length == 0 || Utils.isInteger(args[0])) {
            List<String> help = getHelp(sender);
            int page = Math.max(1, args.length > 0 ? Integer.parseInt(args[0]) : 1);
            int totalPages = help.size() / PER_PAGE + Math.min(1, help.size() % PER_PAGE);

            sender.sendMessage(bar + ChatColor.GRAY + " Command Help (" + page + "/" + totalPages + ") " + bar);
            for (int i = (page - 1) * PER_PAGE; i < Math.min(help.size(), page * PER_PAGE); i++)
                sender.sendMessage(help.get(i)); // Show help.
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

    /**
     * Return all command help.
     * @return help
     */
    private static List<String> getHelp(CommandSender sender) {
        List<String> list = Commands.getCommands().stream().filter(c -> c.canUse(sender, false) && c.getHelp() != null)
                .map(cmd ->  ChatColor.GRAY + cmd.getCommandPrefix() + cmd.getName() + ": " + ChatColor.WHITE + cmd.getHelp())
                .collect(Collectors.toList());
        Configs.getRawConfig(Configs.ConfigType.HELP).getLines().stream().filter(s -> s.length() > 0).forEach(list::add);
        return list.stream().sorted(Comparator.comparing(String::toString)).collect(Collectors.toList());
    }
}
