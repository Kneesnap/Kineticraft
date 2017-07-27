package net.kineticraft.lostcity.commands.player;

import net.kineticraft.lostcity.Core;
import net.kineticraft.lostcity.EnumRank;
import net.kineticraft.lostcity.commands.PlayerCommand;
import net.kineticraft.lostcity.data.KCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * List online players.
 * Created by Kneesnap on 7/11/2017.
 */
public class CommandList extends PlayerCommand {

    public CommandList() {
        super("", "Get a list of all online players.", "list", "who", "online");
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.GRAY + "There are " + ChatColor.GREEN + Core.getOnlinePlayers().size()
                + ChatColor.GRAY + " online players out of " + ChatColor.GREEN + Bukkit.getMaxPlayers()
                + ChatColor.GRAY + ".");
        Arrays.stream(EnumRank.values()).forEach(r -> sendGroup(sender, r));
    }

    private void sendGroup(CommandSender sender, EnumRank rank) {
        String players = Core.getOnlinePlayers().stream().map(KCPlayer::getWrapper)
                .filter(p -> p.getRank() == rank).map(KCPlayer::getUsername)
                .collect(Collectors.joining(ChatColor.WHITE + ", " + ChatColor.GRAY));

        if (players.length() > 0)
            sender.sendMessage(rank.getColor() + rank.getName() + ChatColor.WHITE + ": " + ChatColor.GRAY + players);
    }
}