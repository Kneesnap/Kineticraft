package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.KCPlayer;
import net.kineticraft.lostcity.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Get a player's username from their nickname.
 * Created by Kneesnap on 6/27/17.
 */
public class CommandRealName extends PlayerCommand {

    public CommandRealName() {
        super("<nick>", "Displays a player's real name.", "realname", "rn");
        autocomplete(p -> Bukkit.getOnlinePlayers().stream().map(KCPlayer::getWrapper)
                .map(KCPlayer::getNickname).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        List<String> matches = Bukkit.getOnlinePlayers().stream().map(KCPlayer::getWrapper)
                .filter(k -> Utils.containsIgnoreCase(k.getNickname(), args[0]) || Utils.containsIgnoreCase(k.getUsername(), args[0]))
                .map(KCPlayer::getUsername).collect(Collectors.toList());

        sender.sendMessage(ChatColor.GRAY + "Matches: " + (matches.isEmpty() ? ChatColor.RED + "None"
                : matches.stream().collect(Collectors.joining(ChatColor.GRAY + ", " + ChatColor.GREEN,
                ChatColor.GREEN.toString(), ""))));
    }
}