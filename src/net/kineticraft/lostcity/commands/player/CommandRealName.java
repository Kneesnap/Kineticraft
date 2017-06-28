package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.wrappers.KCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Get a player's username from their nickname.
 *
 * Created by egoscio on 6/25/17.
 */
public class CommandRealName extends PlayerCommand {

    public CommandRealName() {
        super("<nick>", "Displays a player's real name.", "realname", "rn");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        List<String> matches = Bukkit.getOnlinePlayers().stream().map(KCPlayer::getWrapper)
                .filter(k -> k.getNickname() != null) // Verify they have a nickname.
                .filter(k -> ChatColor.stripColor(k.getNickname()).toLowerCase().contains(args[0].toLowerCase())) // Verify nick matches.
                .map(KCPlayer::getUsername).collect(Collectors.toList());

        sender.sendMessage(ChatColor.GRAY + "Matches: " + (matches.isEmpty() ? ChatColor.RED + "None"
                : matches.stream().collect(Collectors.joining(ChatColor.GRAY + ", " + ChatColor.GREEN,
                ChatColor.GREEN.toString(), ""))));
    }
}